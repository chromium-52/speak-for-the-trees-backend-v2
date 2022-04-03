package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.ADOPTED_SITES;
import static org.jooq.generated.Tables.BLOCKS;
import static org.jooq.generated.Tables.NEIGHBORHOODS;
import static org.jooq.generated.Tables.SITES;
import static org.jooq.generated.Tables.USERS;
import static org.jooq.generated.Tables.SITE_ENTRIES;
import static org.jooq.generated.Tables.STEWARDSHIP;
import static org.jooq.impl.DSL.max;

import com.codeforcommunity.api.IProtectedSiteProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.site.AddSiteRequest;
import com.codeforcommunity.dto.site.AddSitesRequest;
import com.codeforcommunity.dto.site.AdoptedSitesResponse;
import com.codeforcommunity.dto.site.EditSiteRequest;
import com.codeforcommunity.dto.site.NameSiteEntryRequest;
import com.codeforcommunity.dto.site.RecordStewardshipRequest;
import com.codeforcommunity.dto.site.UpdateSiteRequest;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.exceptions.LinkedResourceDoesNotExistException;
import com.codeforcommunity.exceptions.ResourceDoesNotExistException;
import com.codeforcommunity.exceptions.WrongAdoptionStatusException;
import com.codeforcommunity.requester.Emailer;
import io.vertx.core.Vertx;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.AdoptedSitesRecord;
import org.jooq.generated.tables.records.SiteEntriesRecord;
import org.jooq.generated.tables.records.SitesRecord;
import org.jooq.generated.tables.records.UsersRecord;
import org.jooq.generated.tables.records.StewardshipRecord;

public class ProtectedSiteProcessorImpl implements IProtectedSiteProcessor {

  private final DSLContext db;
  private final Emailer emailer;
  // for an adopted site, maps its site ID to the number of days ago that the last inactive email
  // was sent to its adopter for not recording stewardship activities for the site
  private final Map<Integer, Integer> inactiveSiteEmailTracker;
  private static final int INACTIVITY_PERIOD = 21;

  public ProtectedSiteProcessorImpl(DSLContext db, Emailer emailer) {
    this.db = db;
    this.emailer = emailer;
    this.inactiveSiteEmailTracker = new HashMap<>();

    Vertx vertx = Vertx.vertx();
    // First check is 24 hours after starting
    long timerId =
        vertx.setPeriodic(
            TimeUnit.DAYS.toMillis(1),
            id -> {
              this.updateLastEmailedDays();
              this.emailInactiveUsers();
            });
  }

  /**
   * Check if a site with the given siteId exists.
   *
   * @param siteId to check
   */
  private void checkSiteExists(int siteId) {
    if (!db.fetchExists(db.selectFrom(SITES).where(SITES.ID.eq(siteId)))) {
      throw new ResourceDoesNotExistException(siteId, "Site");
    }
  }

  /**
   * Check if a block with the given blockId exists.
   *
   * @param blockId to check
   */
  private void checkBlockExists(int blockId) {
    if (!db.fetchExists(db.selectFrom(BLOCKS).where(BLOCKS.ID.eq(blockId)))) {
      throw new ResourceDoesNotExistException(blockId, "Block");
    }
  }

  /**
   * Check if a neighborhood with the given neighborhoodId exists.
   *
   * @param neighborhoodId to check
   */
  private void checkNeighborhoodExists(int neighborhoodId) {
    if (!db.fetchExists(db.selectFrom(NEIGHBORHOODS).where(NEIGHBORHOODS.ID.eq(neighborhoodId)))) {
      throw new ResourceDoesNotExistException(neighborhoodId, "Neighborhood");
    }
  }

  private Boolean isAlreadyAdopted(int siteId) {
    return db.fetchExists(db.selectFrom(ADOPTED_SITES).where(ADOPTED_SITES.SITE_ID.eq(siteId)));
  }

  private Boolean isAlreadyAdoptedByUser(int userId, int siteId) {
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
    if (!isAdmin(level)) {
      throw new AuthException("User does not have the required privilege level.");
    }
  }

  /**
   * Is the user an admin or super admin.
   *
   * @param level the privilege level of the user calling the route
   * @return true if user is ADMIN or SUPER_ADMIN, else false
   */
  boolean isAdmin(PrivilegeLevel level) {
    return level.equals(PrivilegeLevel.ADMIN) || level.equals(PrivilegeLevel.SUPER_ADMIN);
  }

  @Override
  public void adoptSite(JWTData userData, int siteId, Date dateAdopted) {
    checkSiteExists(siteId);
    if (isAlreadyAdopted(siteId)) {
      throw new WrongAdoptionStatusException(true);
    }

    AdoptedSitesRecord record = db.newRecord(ADOPTED_SITES);
    record.setUserId(userData.getUserId());
    record.setSiteId(siteId);
    record.setDateAdopted(dateAdopted);
    record.store();
  }

  @Override
  public void unadoptSite(JWTData userData, int siteId) {
    checkSiteExists(siteId);
    if (!isAlreadyAdoptedByUser(userData.getUserId(), siteId)) {
      throw new WrongAdoptionStatusException(false);
    }

    db.deleteFrom(ADOPTED_SITES)
        .where(ADOPTED_SITES.USER_ID.eq(userData.getUserId()))
        .and(ADOPTED_SITES.SITE_ID.eq(siteId))
        .execute();
  }

  @Override
  public void forceUnadoptSite(JWTData userData, int siteId) {
    isAdminCheck(userData.getPrivilegeLevel());
    checkSiteExists(siteId);
    if (!isAlreadyAdopted(siteId)) {
      throw new WrongAdoptionStatusException(false);
    }

    AdoptedSitesRecord adoptedSite = db.selectFrom(ADOPTED_SITES)
            .where(ADOPTED_SITES.SITE_ID.eq(siteId))
            .fetchInto(AdoptedSitesRecord.class)
            .get(0);

    Integer adopterId = adoptedSite.getUserId();

    UsersRecord adopter = db.selectFrom(USERS)
            .where(USERS.ID.eq(adopterId))
            .fetchOne();

    if(isAdmin(adopter.getPrivilegeLevel()) && !(userData.getPrivilegeLevel().equals(PrivilegeLevel.SUPER_ADMIN))) {
      throw new AuthException("User does not have the required privilege level.");
    }

    db.deleteFrom(ADOPTED_SITES)
            .where(ADOPTED_SITES.SITE_ID.eq(siteId))
            .execute();
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
    if (!isAlreadyAdoptedByUser(userData.getUserId(), siteId)) {
      throw new WrongAdoptionStatusException(false);
    }

    StewardshipRecord record = db.newRecord(STEWARDSHIP);
    record.setUserId(userData.getUserId());
    record.setSiteId(siteId);
    record.setPerformedOn(recordStewardshipRequest.getDate());
    record.setWatered(recordStewardshipRequest.getWatered());
    record.setMulched(recordStewardshipRequest.getMulched());
    record.setCleaned(recordStewardshipRequest.getCleaned());
    record.setWeeded(recordStewardshipRequest.getWeeded());

    record.store();
  }

  @Override
  public void addSite(JWTData userData, AddSiteRequest addSiteRequest) {
    if (addSiteRequest.getBlockId() != null) {
      checkBlockExists(addSiteRequest.getBlockId());
    }

    checkNeighborhoodExists(addSiteRequest.getNeighborhoodId());

    SitesRecord sitesRecord = db.newRecord(SITES);

    int newId = db.select(max(SITES.ID)).from(SITES).fetchOne(0, Integer.class) + 1;

    sitesRecord.setId(newId);
    sitesRecord.setBlockId(addSiteRequest.getBlockId());
    sitesRecord.setLat(addSiteRequest.getLat());
    sitesRecord.setLng(addSiteRequest.getLng());
    sitesRecord.setCity(addSiteRequest.getCity());
    sitesRecord.setZip(addSiteRequest.getZip());
    sitesRecord.setAddress(addSiteRequest.getAddress());
    sitesRecord.setNeighborhoodId(addSiteRequest.getNeighborhoodId());

    sitesRecord.store();

    SiteEntriesRecord siteEntriesRecord = db.newRecord(SITE_ENTRIES);

    siteEntriesRecord.setUserId(userData.getUserId());
    siteEntriesRecord.setSiteId(sitesRecord.getId());
    siteEntriesRecord.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    siteEntriesRecord.setTreePresent(addSiteRequest.isTreePresent());
    siteEntriesRecord.setStatus(addSiteRequest.getStatus());
    siteEntriesRecord.setGenus(addSiteRequest.getGenus());
    siteEntriesRecord.setSpecies(addSiteRequest.getSpecies());
    siteEntriesRecord.setCommonName(addSiteRequest.getCommonName());
    siteEntriesRecord.setConfidence(addSiteRequest.getConfidence());
    siteEntriesRecord.setDiameter(addSiteRequest.getDiameter());
    siteEntriesRecord.setCircumference(addSiteRequest.getCircumference());
    siteEntriesRecord.setMultistem(addSiteRequest.isMultistem());
    siteEntriesRecord.setCoverage(addSiteRequest.getCoverage());
    siteEntriesRecord.setPruning(addSiteRequest.getPruning());
    siteEntriesRecord.setCondition(addSiteRequest.getCondition());
    siteEntriesRecord.setDiscoloring(addSiteRequest.isDiscoloring());
    siteEntriesRecord.setLeaning(addSiteRequest.isLeaning());
    siteEntriesRecord.setConstrictingGrate(addSiteRequest.isConstrictingGrate());
    siteEntriesRecord.setWounds(addSiteRequest.isWounds());
    siteEntriesRecord.setPooling(addSiteRequest.isPooling());
    siteEntriesRecord.setStakesWithWires(addSiteRequest.isStakesWithWires());
    siteEntriesRecord.setStakesWithoutWires(addSiteRequest.isStakesWithoutWires());
    siteEntriesRecord.setLight(addSiteRequest.isLight());
    siteEntriesRecord.setBicycle(addSiteRequest.isBicycle());
    siteEntriesRecord.setBagEmpty(addSiteRequest.isBagEmpty());
    siteEntriesRecord.setBagFilled(addSiteRequest.isBagFilled());
    siteEntriesRecord.setTape(addSiteRequest.isTape());
    siteEntriesRecord.setSuckerGrowth(addSiteRequest.isSuckerGrowth());
    siteEntriesRecord.setSiteType(addSiteRequest.getSiteType());
    siteEntriesRecord.setSidewalkWidth(addSiteRequest.getSidewalkWidth());
    siteEntriesRecord.setSiteWidth(addSiteRequest.getSiteWidth());
    siteEntriesRecord.setSiteLength(addSiteRequest.getSiteLength());
    siteEntriesRecord.setMaterial(addSiteRequest.getMaterial());
    siteEntriesRecord.setRaisedBed(addSiteRequest.isRaisedBed());
    siteEntriesRecord.setFence(addSiteRequest.isFence());
    siteEntriesRecord.setTrash(addSiteRequest.isTrash());
    siteEntriesRecord.setWires(addSiteRequest.isWires());
    siteEntriesRecord.setGrate(addSiteRequest.isGrate());
    siteEntriesRecord.setStump(addSiteRequest.isStump());
    siteEntriesRecord.setTreeNotes(addSiteRequest.getTreeNotes());
    siteEntriesRecord.setSiteNotes(addSiteRequest.getSiteNotes());

    siteEntriesRecord.store();
  }

  public void updateSite(JWTData userData, int siteId, UpdateSiteRequest updateSiteRequest) {
    checkSiteExists(siteId);

    SiteEntriesRecord record = db.newRecord(SITE_ENTRIES);

    int newId = db.select(max(SITE_ENTRIES.ID)).from(SITE_ENTRIES).fetchOne(0, Integer.class) + 1;

    record.setId(newId);
    record.setUserId(userData.getUserId());
    record.setSiteId(siteId);
    record.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    record.setTreePresent(updateSiteRequest.isTreePresent());
    record.setStatus(updateSiteRequest.getStatus());
    record.setGenus(updateSiteRequest.getGenus());
    record.setSpecies(updateSiteRequest.getSpecies());
    record.setCommonName(updateSiteRequest.getCommonName());
    record.setConfidence(updateSiteRequest.getConfidence());
    record.setDiameter(updateSiteRequest.getDiameter());
    record.setCircumference(updateSiteRequest.getCircumference());
    record.setMultistem(updateSiteRequest.isMultistem());
    record.setCoverage(updateSiteRequest.getCoverage());
    record.setPruning(updateSiteRequest.getPruning());
    record.setCondition(updateSiteRequest.getCondition());
    record.setDiscoloring(updateSiteRequest.isDiscoloring());
    record.setLeaning(updateSiteRequest.isLeaning());
    record.setConstrictingGrate(updateSiteRequest.isConstrictingGrate());
    record.setWounds(updateSiteRequest.isWounds());
    record.setPooling(updateSiteRequest.isPooling());
    record.setStakesWithWires(updateSiteRequest.isStakesWithWires());
    record.setStakesWithoutWires(updateSiteRequest.isStakesWithoutWires());
    record.setLight(updateSiteRequest.isLight());
    record.setBicycle(updateSiteRequest.isBicycle());
    record.setBagEmpty(updateSiteRequest.isBagEmpty());
    record.setBagFilled(updateSiteRequest.isBagFilled());
    record.setTape(updateSiteRequest.isTape());
    record.setSuckerGrowth(updateSiteRequest.isSuckerGrowth());
    record.setSiteType(updateSiteRequest.getSiteType());
    record.setSidewalkWidth(updateSiteRequest.getSidewalkWidth());
    record.setSiteWidth(updateSiteRequest.getSiteWidth());
    record.setSiteLength(updateSiteRequest.getSiteLength());
    record.setMaterial(updateSiteRequest.getMaterial());
    record.setRaisedBed(updateSiteRequest.isRaisedBed());
    record.setFence(updateSiteRequest.isFence());
    record.setTrash(updateSiteRequest.isTrash());
    record.setWires(updateSiteRequest.isWires());
    record.setGrate(updateSiteRequest.isGrate());
    record.setStump(updateSiteRequest.isStump());
    record.setTreeNotes(updateSiteRequest.getTreeNotes());
    record.setSiteNotes(updateSiteRequest.getSiteNotes());

    record.store();
  }

  @Override
  public void editSite(JWTData userData, int siteId, EditSiteRequest editSiteRequest) {
    isAdminCheck(userData.getPrivilegeLevel());
    checkSiteExists(siteId);
    if (editSiteRequest.getBlockId() != null) {
      checkBlockExists(editSiteRequest.getBlockId());
    }
    checkNeighborhoodExists(editSiteRequest.getNeighborhoodId());

    SitesRecord site = db.selectFrom(SITES).where(SITES.ID.eq(siteId)).fetchOne();

    site.setId(siteId);
    site.setBlockId(editSiteRequest.getBlockId());
    site.setAddress(editSiteRequest.getAddress());
    site.setCity(editSiteRequest.getCity());
    site.setZip(editSiteRequest.getZip());
    site.setLat(editSiteRequest.getLat());
    site.setLng(editSiteRequest.getLng());
    site.setNeighborhoodId(editSiteRequest.getNeighborhoodId());

    site.store();
  }

  @Override
  public void addSites(JWTData userData, AddSitesRequest addSitesRequest) {

    isAdminCheck(userData.getPrivilegeLevel());

    addSitesRequest
        .getSites()
        .forEach(
            newSite -> {
              addSite(userData, newSite);
            });
  }

  @Override
  public void deleteSite(JWTData userData, int siteId) {
    isAdminCheck(userData.getPrivilegeLevel());
    checkSiteExists(siteId);

    SitesRecord site = db.selectFrom(SITES).where(SITES.ID.eq(siteId)).fetchOne();
    site.setDeletedAt(new Timestamp(System.currentTimeMillis()));
    site.store();
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

    db.deleteFrom(STEWARDSHIP).where(STEWARDSHIP.ID.eq(activityId)).execute();
  }

  public void nameSiteEntry(JWTData userData, int siteId, NameSiteEntryRequest nameSiteEntryRequest) {
    checkSiteExists(siteId);
    if (!isAlreadyAdoptedByUser(userData.getUserId(), siteId)) {
      throw new AuthException("User is not the site's adopter.");
    }

    SiteEntriesRecord siteEntry = db.selectFrom(SITE_ENTRIES)
        .where(SITE_ENTRIES.SITE_ID.eq(siteId))
        .orderBy(SITE_ENTRIES.UPDATED_AT)
        .limit(1)
        .fetchOne();

    if (siteEntry == null) {
      throw new LinkedResourceDoesNotExistException("Site Entry",
                                                    userData.getUserId(),
                                                    "User",
                                                    siteId,
                                                    "Site");
    }

    siteEntry.setTreeName(nameSiteEntryRequest.getName());
    siteEntry.store();
  }

  @Override
  public void emailInactiveUsers() {
    Map<AdoptedSitesRecord, UsersRecord> adoptedSitesMap = getInactiveSitesAndUsers(INACTIVITY_PERIOD);
    for (AdoptedSitesRecord adoptedSite : adoptedSitesMap.keySet()) {
      UsersRecord user = adoptedSitesMap.get(adoptedSite);
      Integer adoptedSiteId = adoptedSite.getSiteId();
      emailer.sendInactiveEmail(
          user.getEmail(), user.getFirstName(), getSiteAddress(adoptedSiteId), adoptedSiteId.toString());
      inactiveSiteEmailTracker.put(adoptedSiteId, 0);
    }
  }

  /**
   * Increments the tracked IDs' day counters, removing them once the period expires so that an
   * inactive email can be sent again for the adopted site if it continued to be inactive since the
   * last email was sent.
   */
  private void updateLastEmailedDays() {
    for (Integer siteId : inactiveSiteEmailTracker.keySet()) {
      inactiveSiteEmailTracker.put(siteId, inactiveSiteEmailTracker.get(siteId) + 1);
      if (inactiveSiteEmailTracker.get(siteId) >= INACTIVITY_PERIOD) {
        inactiveSiteEmailTracker.remove(siteId);
      }
    }
  }

  /** Query users who haven't performed activity within the last _range_ number of days. */
  private Map<AdoptedSitesRecord, UsersRecord> getInactiveSitesAndUsers(int range) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(new Timestamp(System.currentTimeMillis()));
    cal.add(Calendar.DAY_OF_MONTH, -1 * range); // _range_ days ago
    java.sql.Date inactiveCutoffDate = new Date(cal.getTime().getTime());
    java.sql.Date currentDate = new Date(cal.getTime().getTime());

    Map<AdoptedSitesRecord, UsersRecord> siteMap = new HashMap<>();
    List<AdoptedSitesRecord> adoptedSites = db.selectFrom(ADOPTED_SITES).fetchInto(AdoptedSitesRecord.class);
    // for every adopted site, check if it has a stewardship activity recorded in the last _range_
    // days and if not, add it to _siteMap_ to mark it as inactive
    for (AdoptedSitesRecord adoptedSite : adoptedSites) {
      if (!inactiveSiteEmailTracker.containsKey(adoptedSite.getSiteId())) {
        List<StewardshipRecord> stewardshipRecords =
            db.selectFrom(STEWARDSHIP)
                .where(STEWARDSHIP.SITE_ID.eq(adoptedSite.getSiteId()))
                .and(STEWARDSHIP.PERFORMED_ON.between(inactiveCutoffDate, currentDate))
                .fetchInto(StewardshipRecord.class);
        if (stewardshipRecords.isEmpty()) { // adopted site is inactive
          UsersRecord user =
              db.selectFrom(USERS).where(USERS.ID.eq(adoptedSite.getUserId())).fetch().get(0);
          siteMap.put(adoptedSite, user);
        }
      }
    }
    return siteMap;
  }

  /** Gets address string of the site from its ID. */
  private String getSiteAddress(Integer siteId) {
    return db.selectFrom(SITES).where(SITES.ID.eq(siteId)).fetch().get(0).getAddress();
  }
}
