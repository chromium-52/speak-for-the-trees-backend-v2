package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.FAVORITE_SITES;
import static org.jooq.generated.Tables.SITES;
import static org.jooq.generated.Tables.STEWARDSHIP;

import com.codeforcommunity.api.ISiteProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.site.FavoriteSitesResponse;
import com.codeforcommunity.dto.site.RecordStewardshipRequest;
import com.codeforcommunity.dto.site.StewardshipActivitiesResponse;
import com.codeforcommunity.dto.site.StewardshipActivity;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.exceptions.ResourceDoesNotExistException;
import com.codeforcommunity.exceptions.WrongFavoriteStatusException;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.FavoriteSitesRecord;
import org.jooq.generated.tables.records.StewardshipRecord;

public class SiteProcessorImpl implements ISiteProcessor {

  private final DSLContext db;

  public SiteProcessorImpl(DSLContext db) {
    this.db = db;
  }

  private void checkSiteExists(int siteId) {
    if (!db.fetchExists(db.selectFrom(SITES).where(SITES.ID.eq(siteId)))) {
      throw new ResourceDoesNotExistException(siteId, "Site");
    }
  }

  private Boolean isAlreadyFavorite(int userId, int siteId) {
    return db.fetchExists(
        db.selectFrom(FAVORITE_SITES)
            .where(FAVORITE_SITES.USER_ID.eq(userId))
            .and(FAVORITE_SITES.SITE_ID.eq(siteId)));
  }

  @Override
  public void favoriteSite(JWTData userData, int siteId) {
    checkSiteExists(siteId);
    if (isAlreadyFavorite(userData.getUserId(), siteId)) {
      throw new WrongFavoriteStatusException(true);
    }

    FavoriteSitesRecord record = db.newRecord(FAVORITE_SITES);
    record.setUserId(userData.getUserId());
    record.setSiteId(siteId);
    record.store();
  }

  @Override
  public void unfavoriteSite(JWTData userData, int siteId) {
    checkSiteExists(siteId);
    if (!isAlreadyFavorite(userData.getUserId(), siteId)) {
      throw new WrongFavoriteStatusException(false);
    }

    db.deleteFrom(FAVORITE_SITES)
        .where(FAVORITE_SITES.USER_ID.eq(userData.getUserId()))
        .and(FAVORITE_SITES.SITE_ID.eq(siteId));
  }

  @Override
  public FavoriteSitesResponse getFavoriteSites(JWTData userData) {
    List<FavoriteSitesRecord> favoriteSites =
        db.selectFrom(FAVORITE_SITES)
            .where(FAVORITE_SITES.USER_ID.eq(userData.getUserId()))
            .fetch();
    List<Integer> favorites = new ArrayList<>();

    favoriteSites.forEach(site -> favorites.add(site.getSiteId()));

    return new FavoriteSitesResponse(favorites);
  }

  @Override
  public void recordStewardship(
      JWTData userData, int siteId, RecordStewardshipRequest recordStewardshipRequest) {
    checkSiteExists(siteId);

    StewardshipRecord record = db.newRecord(STEWARDSHIP);
    record.setUserId(userData.getUserId());
    record.setPerformedOn(recordStewardshipRequest.getDate());
    record.setWatered(recordStewardshipRequest.getWatered());
    record.setMulched(recordStewardshipRequest.getMulched());
    record.setCleaned(recordStewardshipRequest.getCleaned());
    record.setWeeded(recordStewardshipRequest.getWeeded());

    record.store();
  }

  @Override
  public void deleteStewardship(JWTData userData, int activityId) {
    StewardshipRecord activity =
        db.selectFrom(STEWARDSHIP).where(STEWARDSHIP.ID.eq(activityId)).fetchOne();

    if (activity == null) {
      throw new ResourceDoesNotExistException(activityId, "Stewardship Activity");
    }
    if (!(activity.getUserId().equals(userData.getUserId())
        || userData.getPrivilegeLevel().equals(PrivilegeLevel.SUPER_ADMIN)
        || userData.getPrivilegeLevel().equals(PrivilegeLevel.ADMIN))) {
      throw new AuthException(
          "User needs to be an admin or the activity's author to delete the record.");
    }

    db.deleteFrom(STEWARDSHIP).where(STEWARDSHIP.ID.eq(activityId));
  }

  @Override
  public StewardshipActivitiesResponse getStewardshipActivities(int siteId) {
    List<StewardshipRecord> records =
        db.selectFrom(STEWARDSHIP).where(STEWARDSHIP.SITE_ID.eq(siteId)).fetch();
    List<StewardshipActivity> activities = new ArrayList<>();

    records.forEach(
        record -> {
          StewardshipActivity stewardshipActivity =
              new StewardshipActivity(
                  record.getId(),
                  record.getUserId(),
                  record.getPerformedOn(),
                  record.getWatered(),
                  record.getMulched(),
                  record.getCleaned(),
                  record.getWeeded());
          activities.add(stewardshipActivity);
        });

    return new StewardshipActivitiesResponse(activities);
  }
}
