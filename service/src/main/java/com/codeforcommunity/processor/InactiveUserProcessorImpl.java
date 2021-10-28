package com.codeforcommunity.processor;

import com.codeforcommunity.api.IInactiveUserProcessor;
import com.codeforcommunity.requester.Emailer;
import io.vertx.core.Vertx;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.generated.tables.records.AdoptedSitesRecord;
import org.jooq.generated.tables.records.StewardshipRecord;
import org.jooq.generated.tables.records.UsersRecord;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.jooq.generated.Tables.*;

public class InactiveUserProcessorImpl implements IInactiveUserProcessor {
  private final Emailer emailer;
  private final DSLContext db;
  private final List<Integer> emailedIds;
  private static final int PERIOD = 21;

  public InactiveUserProcessorImpl(DSLContext db, Emailer emailer) {
    this.emailer = emailer;
    this.db = db;
    this.emailedIds = new ArrayList<>();

    Vertx vertx = Vertx.vertx();


    //Note: First check is 24h after starting
    long timerId = vertx.setPeriodic(TimeUnit.DAYS.toMillis(1), id -> {
      this.emailInactiveUsers();
    });
  }


  //Query users who haven't performed activity in the given range
  private List<UsersRecord> getInactiveUsers(int range) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(new Timestamp(System.currentTimeMillis()));
    Date currentDate = new Date(cal.getTime().getTime());
    cal.add(Calendar.DAY_OF_MONTH, -1 * range); //21 days ago
    Date floorDate = new Date(cal.getTime().getTime());
    Result<UsersRecord> users = db.selectFrom(USERS).fetch();
    List<UsersRecord> inactiveUsers = new ArrayList<>();
    for (UsersRecord user : users) {
      Result<StewardshipRecord> records = db.
              selectFrom(STEWARDSHIP).
              where(STEWARDSHIP.ID.equal(user.getId())).
              and(STEWARDSHIP.PERFORMED_ON.betweenSymmetric(floorDate, currentDate)).
              fetch();
      if (records.isEmpty()) {
        inactiveUsers.add(user);
      } else emailedIds.remove(user.getId()); //No longer inactive, allow to send email again
    }
    return inactiveUsers;
  }


  private List<UsersRecord> getInactiveUsers() {
    return this.getInactiveUsers(InactiveUserProcessorImpl.PERIOD);
  }

  //Gets address string from site id
  private String getSiteAddress(Integer siteId) {
    return db.selectFrom(SITES).where(SITES.ID.equal(siteId)).fetch().get(0).getAddress();
  }

  @Override
  public void emailInactiveUsers() {
    List<UsersRecord> users = getInactiveUsers();
    for (UsersRecord user : users) {
      Result<AdoptedSitesRecord> userSites = db.selectFrom(ADOPTED_SITES).where(ADOPTED_SITES.USER_ID.equal(user.getId())).fetch();
      if (userSites.isEmpty() || emailedIds.contains(user.getId())) {
        continue; //No sites or have already emailed them
      }
      Integer siteId = userSites.get(0).getSiteId();
      emailer.sendInactiveEmail(user.getEmail(), user.getFirstName(), getSiteAddress(siteId), siteId.toString());
      emailedIds.add(user.getId());
    }
  }
}
