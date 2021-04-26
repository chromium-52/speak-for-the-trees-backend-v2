package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.*;
import static org.jooq.generated.Tables.ADOPTED_SITES;
import static org.jooq.generated.Tables.SITES;
import static org.jooq.generated.Tables.STEWARDSHIP;

import com.codeforcommunity.api.ISiteProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.site.AdoptedSitesResponse;
import com.codeforcommunity.dto.site.RecordStewardshipRequest;
import com.codeforcommunity.dto.site.StewardshipActivitiesResponse;
import com.codeforcommunity.dto.site.StewardshipActivity;
import com.codeforcommunity.dto.site.UpdateSiteRequest;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.exceptions.ResourceDoesNotExistException;
import com.codeforcommunity.exceptions.WrongAdoptionStatusException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.AdoptedSitesRecord;
import org.jooq.generated.tables.records.SiteEntriesRecord;
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

  private Boolean isAlreadyAdopted(int userId, int siteId) {
    return db.fetchExists(
        db.selectFrom(ADOPTED_SITES)
            .where(ADOPTED_SITES.USER_ID.eq(userId))
            .and(ADOPTED_SITES.SITE_ID.eq(siteId)));
  }

  /**
   * Throws an exception if the user is not an admin or super admin.
   *
   * @param level the privilege level of the user calling the route
   */
  void isAdminCheck(PrivilegeLevel level) {
    if (!(level.equals(PrivilegeLevel.ADMIN) || level.equals(PrivilegeLevel.SUPER_ADMIN))) {
      throw new AuthException("User does not have the required privilege level.");
    }
  }

  @Override
  public void adoptSite(JWTData userData, int siteId) {
    checkSiteExists(siteId);
    if (isAlreadyAdopted(userData.getUserId(), siteId)) {
      throw new WrongAdoptionStatusException(true);
    }

    AdoptedSitesRecord record = db.newRecord(ADOPTED_SITES);
    record.setUserId(userData.getUserId());
    record.setSiteId(siteId);
    record.store();
  }

  @Override
  public void unadoptSite(JWTData userData, int siteId) {
    checkSiteExists(siteId);
    if (!isAlreadyAdopted(userData.getUserId(), siteId)) {
      throw new WrongAdoptionStatusException(false);
    }

    db.deleteFrom(ADOPTED_SITES)
        .where(ADOPTED_SITES.USER_ID.eq(userData.getUserId()))
        .and(ADOPTED_SITES.SITE_ID.eq(siteId));
  }

  @Override
  public AdoptedSitesResponse getAdoptedSites(JWTData userData) {
    List<Integer> favoriteSites =
        db.selectFrom(ADOPTED_SITES)
            .where(ADOPTED_SITES.USER_ID.eq(userData.getUserId()))
            .fetch(ADOPTED_SITES.SITE_ID);

    return new AdoptedSitesResponse(favoriteSites);
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
  public void updateSite(JWTData userData, int siteId, UpdateSiteRequest updateSiteRequest) {
    checkSiteExists(siteId);

    SiteEntriesRecord lastEntry =
        db.selectFrom(SITE_ENTRIES)
            .where(SITE_ENTRIES.SITE_ID.eq(siteId))
            .orderBy(SITE_ENTRIES.UPDATED_AT.desc())
            .limit(1)
            .fetchOne();

    SiteEntriesRecord record = db.newRecord(SITE_ENTRIES);

    record.setUserId(userData.getUserId());
    record.setSiteId(siteId);
    record.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    if (updateSiteRequest.isTree_present() != null) {
      record.setTreePresent(updateSiteRequest.isTree_present());
    } else {
      record.setTreePresent(lastEntry.getTreePresent());
    }

    if (updateSiteRequest.getStatus() != null) {
      record.setStatus(updateSiteRequest.getStatus());
    } else {
      record.setStatus(lastEntry.getStatus());
    }

    if (updateSiteRequest.getGenus() != null) {
      record.setGenus(updateSiteRequest.getGenus());
    } else {
      record.setGenus(lastEntry.getGenus());
    }

    if (updateSiteRequest.getSpecies() != null) {
      record.setSpecies(updateSiteRequest.getSpecies());
    } else {
      record.setSpecies(lastEntry.getSpecies());
    }

    if (updateSiteRequest.getCommon_name() != null) {
      record.setCommonName(updateSiteRequest.getCommon_name());
    } else {
      record.setCommonName(lastEntry.getCommonName());
    }

    if (updateSiteRequest.getConfidence() != null) {
      record.setConfidence(updateSiteRequest.getConfidence());
    } else {
      record.setConfidence(lastEntry.getConfidence());
    }

    if (updateSiteRequest.getDiameter() != null) {
      record.setDiameter(updateSiteRequest.getDiameter());
    } else {
      record.setDiameter(lastEntry.getDiameter());
    }

    if (updateSiteRequest.getCircumference() != null) {
      record.setCircumference(updateSiteRequest.getCircumference());
    } else {
      record.setCircumference(lastEntry.getCircumference());
    }

    if (updateSiteRequest.isMultistem() != null) {
      record.setMultistem(updateSiteRequest.isMultistem());
    } else {
      record.setMultistem(lastEntry.getMultistem());
    }

    if (updateSiteRequest.getCoverage() != null) {
      record.setCoverage(updateSiteRequest.getCoverage());
    } else {
      record.setCoverage(lastEntry.getCoverage());
    }

    if (updateSiteRequest.getPruning() != null) {
      record.setPruning(updateSiteRequest.getPruning());
    } else {
      record.setPruning(lastEntry.getPruning());
    }

    if (updateSiteRequest.getCondition() != null) {
      record.setCondition(updateSiteRequest.getCondition());
    } else {
      record.setCondition(lastEntry.getCondition());
    }

    if (updateSiteRequest.isDiscoloring() != null) {
      record.setDiscoloring(updateSiteRequest.isDiscoloring());
    } else {
      record.setDiscoloring(lastEntry.getDiscoloring());
    }

    if (updateSiteRequest.isLeaning() != null) {
      record.setLeaning(updateSiteRequest.isLeaning());
    } else {
      record.setLeaning(lastEntry.getLeaning());
    }

    if (updateSiteRequest.isConstricting_gate() != null) {
      record.setConstrictingGate(updateSiteRequest.isConstricting_gate());
    } else {
      record.setConstrictingGate(lastEntry.getConstrictingGate());
    }

    if (updateSiteRequest.isWounds() != null) {
      record.setWounds(updateSiteRequest.isWounds());
    } else {
      record.setWounds(lastEntry.getWounds());
    }

    if (updateSiteRequest.isPooling() != null) {
      record.setPooling(updateSiteRequest.isPooling());
    } else {
      record.setPooling(lastEntry.getPooling());
    }

    if (updateSiteRequest.isStakes_with_wires() != null) {
      record.setStakesWithWires(updateSiteRequest.isStakes_with_wires());
    } else {
      record.setStakesWithWires(lastEntry.getStakesWithWires());
    }

    if (updateSiteRequest.isStakes_without_wires() != null) {
      record.setStakesWithoutWires(updateSiteRequest.isStakes_without_wires());
    } else {
      record.setStakesWithoutWires(lastEntry.getStakesWithoutWires());
    }

    if (updateSiteRequest.isLight() != null) {
      record.setLight(updateSiteRequest.isLight());
    } else {
      record.setLight(lastEntry.getLight());
    }

    if (updateSiteRequest.isBicycle() != null) {
      record.setBicycle(updateSiteRequest.isBicycle());
    } else {
      record.setBicycle(lastEntry.getBicycle());
    }

    if (updateSiteRequest.isBag_empty() != null) {
      record.setBagEmpty(updateSiteRequest.isBag_empty());
    } else {
      record.setBagEmpty(lastEntry.getBagEmpty());
    }

    if (updateSiteRequest.isBag_filled() != null) {
      record.setBagFilled(updateSiteRequest.isBag_filled());
    } else {
      record.setBagFilled(lastEntry.getBagFilled());
    }

    if (updateSiteRequest.isTape() != null) {
      record.setTape(updateSiteRequest.isTape());
    } else {
      record.setTape(lastEntry.getTape());
    }

    if (updateSiteRequest.isSucker_growth() != null) {
      record.setSuckerGrowth(updateSiteRequest.isSucker_growth());
    } else {
      record.setSuckerGrowth(lastEntry.getSuckerGrowth());
    }

    if (updateSiteRequest.getSite_type() != null) {
      record.setSiteType(updateSiteRequest.getSite_type());
    } else {
      record.setSiteType(lastEntry.getSiteType());
    }

    if (updateSiteRequest.getSidewalk_width() != null) {
      record.setSidewalkWidth(updateSiteRequest.getSidewalk_width());
    } else {
      record.setSidewalkWidth(lastEntry.getSidewalkWidth());
    }

    if (updateSiteRequest.getSite_width() != null) {
      record.setSiteWidth(updateSiteRequest.getSite_width());
    } else {
      record.setSiteWidth(lastEntry.getSiteWidth());
    }

    if (updateSiteRequest.getSite_length() != null) {
      record.setSiteLength(updateSiteRequest.getSite_length());
    } else {
      record.setSiteLength(lastEntry.getSiteLength());
    }

    if (updateSiteRequest.getMaterial() != null) {
      record.setMaterial(updateSiteRequest.getMaterial());
    } else {
      record.setMaterial(lastEntry.getMaterial());
    }

    if (updateSiteRequest.isRaised_bed() != null) {
      record.setRaisedBed(updateSiteRequest.isRaised_bed());
    } else {
      record.setRaisedBed(lastEntry.getRaisedBed());
    }

    if (updateSiteRequest.isFence() != null) {
      record.setFence(updateSiteRequest.isFence());
    } else {
      record.setFence(lastEntry.getFence());
    }

    if (updateSiteRequest.isTrash() != null) {
      record.setTrash(updateSiteRequest.isTrash());
    } else {
      record.setTrash(lastEntry.getTrash());
    }

    if (updateSiteRequest.isWires() != null) {
      record.setWires(updateSiteRequest.isWires());
    } else {
      record.setWires(lastEntry.getWires());
    }

    if (updateSiteRequest.isGrate() != null) {
      record.setGrate(updateSiteRequest.isGrate());
    } else {
      record.setGrate(lastEntry.getGrate());
    }

    if (updateSiteRequest.isStump() != null) {
      record.setStump(updateSiteRequest.isStump());
    } else {
      record.setStump(lastEntry.getStump());
    }

    if (updateSiteRequest.getTree_notes() != null) {
      record.setTreeNotes(updateSiteRequest.getTree_notes());
    } else {
      record.setTreeNotes(lastEntry.getTreeNotes());
    }

    if (updateSiteRequest.getSite_notes() != null) {
      record.setSiteNotes(updateSiteRequest.getSite_notes());
    } else {
      record.setSiteNotes(lastEntry.getSiteNotes());
    }

    record.store();
  }

  @Override
  public void deleteSite(JWTData userData, int siteId) {
    isAdminCheck(userData.getPrivilegeLevel());
    checkSiteExists(siteId);

    db.deleteFrom(SITES).where(SITES.ID.eq(siteId));
    db.deleteFrom(SITE_ENTRIES).where(SITE_ENTRIES.SITE_ID.eq(siteId));
  }

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
