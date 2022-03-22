package com.codeforcommunity.processor;

import com.codeforcommunity.api.IProtectedNeighborhoodsProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.neighborhoods.SendEmailRequest;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.requester.Emailer;
import java.util.HashSet;
import java.util.List;
import org.jooq.DSLContext;

import static org.jooq.generated.Tables.ADOPTED_SITES;
import static org.jooq.generated.Tables.NEIGHBORHOODS;
import static org.jooq.generated.Tables.SITES;
import static org.jooq.generated.tables.Users.USERS;

public class ProtectedNeighborhoodsProcessorImpl implements IProtectedNeighborhoodsProcessor {
  private final DSLContext db;
  private final Emailer emailer;

  public ProtectedNeighborhoodsProcessorImpl(DSLContext db, Emailer emailer) {
    this.db = db;
    this.emailer = emailer;
  }

  /**
   * Throws an exception if the user is not an admin or super admin.
   *
   * @param level the privilege level of the user calling the route
   */
  private void isAdminCheck(PrivilegeLevel level) {
    if (!(level.equals(PrivilegeLevel.ADMIN) || level.equals(PrivilegeLevel.SUPER_ADMIN))) {
      throw new AuthException("User does not have the required privilege level.");
    }
  }

  @Override
  public void sendEmail(JWTData userData, SendEmailRequest sendEmailRequest) {
    isAdminCheck(userData.getPrivilegeLevel());

    List<Integer> neighborhoodIDs = sendEmailRequest.getNeighborhoodIDs();
    if (neighborhoodIDs.size() == 0) {
      neighborhoodIDs = db.select(NEIGHBORHOODS.ID).from(NEIGHBORHOODS).fetchInto(Integer.class);
    }

    String emailBody = sendEmailRequest.getEmailBody();

    List<String> userEmailRecords = db.select(USERS.EMAIL)
        .from(USERS)
        .leftJoin(ADOPTED_SITES)
        .on(USERS.ID.eq(ADOPTED_SITES.USER_ID))
        .leftJoin(SITES)
        .on(ADOPTED_SITES.SITE_ID.eq(SITES.ID))
        .leftJoin(NEIGHBORHOODS)
        .on(SITES.NEIGHBORHOOD_ID.eq(NEIGHBORHOODS.ID))
        .where(NEIGHBORHOODS.ID.in(neighborhoodIDs)).fetchInto(String.class);

    emailer.sendNeighborhoodsEmail(new HashSet<>(userEmailRecords), emailBody);
  }
}