package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.FAVORITE_SITES;
import static org.jooq.generated.Tables.SITES;
import static org.jooq.generated.Tables.STEWARDSHIP;

import com.codeforcommunity.api.ISiteProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.site.RecordStewardshipRequest;
import com.codeforcommunity.exceptions.ResourceDoesNotExistException;
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
      // throw exception
    }

    FavoriteSitesRecord record = db.newRecord(FAVORITE_SITES);
    record.setUserId(userData.getUserId());
    record.setSiteId(siteId);
    record.store();
  }

  @Override
  public void unfavoriteSite(JWTData userData, int siteId) {
    checkSiteExists(siteId);
    if (isAlreadyFavorite(userData.getUserId(), siteId)) {
      throw new ResourceDoesNotExistException(-1, "FavoriteSiteRecord");
    }

    db.deleteFrom(FAVORITE_SITES)
        .where(FAVORITE_SITES.USER_ID.eq(userData.getUserId()))
        .and(FAVORITE_SITES.SITE_ID.eq(siteId));
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
}
