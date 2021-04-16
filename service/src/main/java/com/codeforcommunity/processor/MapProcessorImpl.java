package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.*;
import static org.jooq.impl.DSL.count;

import com.codeforcommunity.api.IMapProcessor;
import com.codeforcommunity.dto.map.*;
import com.codeforcommunity.enums.ReservationAction;
import com.codeforcommunity.logger.SLogger;
import io.vertx.core.json.JsonObject;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.jooq.*;
import org.jooq.generated.tables.records.BlocksRecord;
import org.jooq.generated.tables.records.NeighborhoodsRecord;

public class MapProcessorImpl implements IMapProcessor {

  private final SLogger logger = new SLogger(MapProcessorImpl.class);
  private final DSLContext db;

  public MapProcessorImpl(DSLContext db) {
    this.db = db;
  }

  /** Create a corresponding BlockFeature from a BlocksRecord. */
  private BlockFeature blockFeatureFromRecord(BlocksRecord blocksRecord) {
    BlockFeatureProperties properties =
        new BlockFeatureProperties(
            blocksRecord.getId(), blocksRecord.getLat(), blocksRecord.getLng());
    try {
      JsonObject geometry = new JsonObject(blocksRecord.getGeometry());
      return new BlockFeature(properties, geometry);
    } catch (Exception e) {
      String errorMessage =
          String.format(
              "Exception thrown while processing conversion of geometry to JSON for block id [%d]",
              blocksRecord.getId());
      logger.error(errorMessage, e);
      throw e;
    }
  }

  /**
   * Given a neighborhoodId, return the percent of blocks that have been completed or are in QA in
   * it as an integer between 0 and 100.
   */
  private Integer getNeighborhoodCompletionPercentage(int neighborhoodId) {
    // This counts the number of blocks that are in the given neighborhood
    int totalNeighborhoodBlocks =
        db.select(count())
            .from(BLOCKS)
            .where(BLOCKS.NEIGHBORHOOD_ID.eq(neighborhoodId))
            .fetchOne(0, Integer.class);

    // This joins each block with their most recent reservations table entry
    Select<Record2<Integer, ReservationAction>> subquery =
        db.select(BLOCKS.ID, RESERVATIONS.ACTION_TYPE)
            .distinctOn(BLOCKS.ID)
            .from(BLOCKS)
            .join(RESERVATIONS)
            .onKey()
            .where(BLOCKS.NEIGHBORHOOD_ID.eq(neighborhoodId))
            .orderBy(BLOCKS.ID, RESERVATIONS.PERFORMED_AT.desc());

    // This counts the number of rows in the above query that have their most recent reservation
    // action as complete or qa
    int completedNeighborhoodBlocks =
        db.select(count())
            .from(subquery)
            .where(
                subquery
                    .field(1, ReservationAction.class)
                    .in(ReservationAction.COMPLETE, ReservationAction.QA))
            .fetchOne(0, Integer.class);

    double completionPercent = (double) completedNeighborhoodBlocks / totalNeighborhoodBlocks;
    return (int) Math.floor(completionPercent * 100);
  }

  /** Create a corresponding NeighborhoodFeature for a given neighborhoodsRecord */
  private NeighborhoodFeature neighborhoodFeatureFromRecord(
      NeighborhoodsRecord neighborhoodsRecord) {
    Integer neighborhoodCompletionPercentage =
        getNeighborhoodCompletionPercentage(neighborhoodsRecord.getId());
    NeighborhoodFeatureProperties properties =
        new NeighborhoodFeatureProperties(
            neighborhoodsRecord.getId(),
            neighborhoodsRecord.getNeighborhoodName(),
            neighborhoodCompletionPercentage,
            neighborhoodsRecord.getLat(),
            neighborhoodsRecord.getLng());
    try {
      JsonObject geometry = new JsonObject(neighborhoodsRecord.getGeometry());
      return new NeighborhoodFeature(properties, geometry);
    } catch (Exception e) {
      String errorMessage =
          String.format(
              "Exception thrown while processing conversion of geometry to JSON for neighborhood id [%d]",
              neighborhoodsRecord.getId());
      logger.error(errorMessage, e);
      throw e;
    }
  }

  private SiteFeature siteFeatureFromRecord(
      Record9<Integer, Boolean, Double, String, Timestamp, String, String, BigDecimal, BigDecimal>
          sitesRecord) {
    SiteFeatureProperties properties =
        new SiteFeatureProperties(
            sitesRecord.value1(),
            sitesRecord.value2(),
            sitesRecord.value3(),
            sitesRecord.value4(),
            sitesRecord.value5(),
            sitesRecord.value6(),
            sitesRecord.value7());
    GeometryPoint geometry = new GeometryPoint(sitesRecord.value8(), sitesRecord.value9());
    return new SiteFeature(properties, geometry);
  }

  @Override
  public BlockGeoResponse getBlockGeoJson() {
    List<BlockFeature> features =
        this.db.selectFrom(BLOCKS).stream()
            .map(this::blockFeatureFromRecord)
            .collect(Collectors.toList());
    return new BlockGeoResponse(features);
  }

  @Override
  public NeighborhoodGeoResponse getNeighborhoodGeoJson() {
    List<NeighborhoodFeature> features =
        this.db.selectFrom(NEIGHBORHOODS).stream()
            .map(this::neighborhoodFeatureFromRecord)
            .collect(Collectors.toList());
    return new NeighborhoodGeoResponse(features);
  }

  private <T> List<T> mergeSorted(List<T> l1, List<T> l2, Comparator<T> comparator) {
    if (l1.isEmpty()) {
      return l2;
    }
    if (l2.isEmpty()) {
      return l1;
    }
    T element1 = l1.get(0);
    T element2 = l2.get(0);
    List<T> newList = new ArrayList<>();
    if (comparator.compare(element1, element2) < 0) {
      newList.add(element1);
      newList.addAll(mergeSorted(l1.subList(1, l1.size()), l2, comparator));
      return newList;
    }
    newList.add(element2);
    newList.addAll(mergeSorted(l1, l2.subList(1, l2.size()), comparator));
    return newList;
  }

  @Override
  public SiteGeoResponse getSiteGeoJson() {
    List<
            Record9<
                Integer,
                Boolean,
                Double,
                String,
                Timestamp,
                String,
                String,
                BigDecimal,
                BigDecimal>>
        nonNullUserRecords =
            this.db
                .select(
                    SITE_ENTRIES.ID,
                    SITE_ENTRIES.TREE_PRESENT,
                    SITE_ENTRIES.DIAMETER,
                    SITE_ENTRIES.SPECIES,
                    SITE_ENTRIES.UPDATED_AT,
                    USERS.USERNAME,
                    SITES.ADDRESS,
                    SITES.LAT,
                    SITES.LNG)
                .from(SITE_ENTRIES)
                .leftJoin(USERS)
                .on(SITE_ENTRIES.USER_ID.eq(USERS.ID))
                .leftJoin(SITES)
                .on(SITE_ENTRIES.SITE_ID.eq(SITES.ID))
                .where(SITE_ENTRIES.USER_ID.isNotNull())
                .orderBy(SITE_ENTRIES.ID)
                .fetch();
    List<
            Record9<
                Integer,
                Boolean,
                Double,
                String,
                Timestamp,
                String,
                String,
                BigDecimal,
                BigDecimal>>
        nullUserRecords =
            this.db
                .select(
                    SITE_ENTRIES.ID,
                    SITE_ENTRIES.TREE_PRESENT,
                    SITE_ENTRIES.DIAMETER,
                    SITE_ENTRIES.SPECIES,
                    SITE_ENTRIES.UPDATED_AT,
                    ENTRY_USERNAMES.USERNAME,
                    SITES.ADDRESS,
                    SITES.LAT,
                    SITES.LNG)
                .from(SITE_ENTRIES)
                .leftJoin(ENTRY_USERNAMES)
                .on(SITE_ENTRIES.ID.eq(ENTRY_USERNAMES.ENTRY_ID))
                .leftJoin(SITES)
                .on(SITE_ENTRIES.SITE_ID.eq(SITES.ID))
                .where(SITE_ENTRIES.USER_ID.isNull())
                .orderBy(SITE_ENTRIES.ID)
                .fetch();
    List<
            Record9<
                Integer,
                Boolean,
                Double,
                String,
                Timestamp,
                String,
                String,
                BigDecimal,
                BigDecimal>>
        allSiteEntriesRecords =
            this.mergeSorted(
                nonNullUserRecords, nullUserRecords, Comparator.comparingInt(Record9::value1));
    List<SiteFeature> features =
        allSiteEntriesRecords.stream()
            .map(this::siteFeatureFromRecord)
            .collect(Collectors.toList());
    return new SiteGeoResponse(features);
  }
}
