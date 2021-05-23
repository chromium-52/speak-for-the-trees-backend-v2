package com.codeforcommunity.processor;

import static org.jooq.generated.tables.AdoptedSites.ADOPTED_SITES;
import static org.jooq.generated.tables.Blocks.BLOCKS;
import static org.jooq.generated.tables.EntryUsernames.ENTRY_USERNAMES;
import static org.jooq.generated.tables.Neighborhoods.NEIGHBORHOODS;
import static org.jooq.generated.tables.Reservations.RESERVATIONS;
import static org.jooq.generated.tables.SiteEntries.SITE_ENTRIES;
import static org.jooq.generated.tables.Sites.SITES;
import static org.jooq.generated.tables.Users.USERS;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.max;

import com.codeforcommunity.api.IMapProcessor;
import com.codeforcommunity.dto.map.BlockFeature;
import com.codeforcommunity.dto.map.BlockFeatureProperties;
import com.codeforcommunity.dto.map.BlockGeoResponse;
import com.codeforcommunity.dto.map.GeometryPoint;
import com.codeforcommunity.dto.map.NeighborhoodFeature;
import com.codeforcommunity.dto.map.NeighborhoodFeatureProperties;
import com.codeforcommunity.dto.map.NeighborhoodGeoResponse;
import com.codeforcommunity.dto.map.SiteFeature;
import com.codeforcommunity.dto.map.SiteFeatureProperties;
import com.codeforcommunity.dto.map.SiteGeoResponse;
import com.codeforcommunity.enums.ReservationAction;
import com.codeforcommunity.logger.SLogger;
import io.vertx.core.json.JsonObject;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.Record11;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.Select;
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
      Record11<
              Integer, // #1: Site ID
              Boolean, // #2: Tree Present
              Double, // #3: Diameter
              String, // #4: Species
              Timestamp, // #5: Updated At
              Date, // #6: Planting Date
              String, // #7: Username
              Integer, // #8: Adopter User ID
              String, // #9: Address
              BigDecimal, // #10: Lat
              BigDecimal> // #11: Lng
          sitesRecord) {
    SiteFeatureProperties properties =
        new SiteFeatureProperties(
            sitesRecord.value1(),
            sitesRecord.value2(),
            sitesRecord.value3(),
            sitesRecord.value4(),
            sitesRecord.value5(),
            sitesRecord.value6(),
            sitesRecord.value7(),
            sitesRecord.value8(),
            sitesRecord.value9());
    GeometryPoint geometry = new GeometryPoint(sitesRecord.value10(), sitesRecord.value11());
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
    int length1 = l1.size();
    int length2 = l2.size();
    int i = 0;
    int j = 0;
    List<T> newList = new ArrayList<>();
    while(i < length1 && j < length2) {
      T element1 = l1.get(i);
      T element2 = l2.get(j);
      if(comparator.compare(element1, element2) < 0) {
        newList.add(element1);
        i++;
      } else {
        newList.add(element2);
        j++;
      }
    }

    // Store remaining elements of first array
    if (i < length1) {
      newList.addAll(l1.subList(i, length1));
    }

    // Store remaining elements of second array
    if (j < length2) {
      newList.addAll(l2.subList(j, length2));
    }

    return newList;
  }

  @Override
  public SiteGeoResponse getSiteGeoJson() {
    Result<
            Record11<
                Integer, // Site ID
                Boolean, // Tree Present
                Double, // Diameter
                String, // Species
                Timestamp, // Updated At
                Date, // Planting Date
                String, // Username
                Integer, // Adopter User ID
                String, // Address
                BigDecimal, // Lat
                BigDecimal>> // Lng
        nonNullUserRecords =
            this.db
                .select(
                    SITES.ID,
                    SITE_ENTRIES.TREE_PRESENT,
                    SITE_ENTRIES.DIAMETER,
                    SITE_ENTRIES.SPECIES,
                    SITE_ENTRIES.UPDATED_AT,
                    SITE_ENTRIES.PLANTING_DATE,
                    USERS.USERNAME,
                    ADOPTED_SITES.USER_ID,
                    SITES.ADDRESS,
                    SITES.LAT,
                    SITES.LNG)
                .from(SITES)
                .innerJoin(SITE_ENTRIES)
                .on(SITES.ID.eq(SITE_ENTRIES.SITE_ID))
                .innerJoin(USERS)
                .on(SITE_ENTRIES.USER_ID.eq(USERS.ID))
                .leftJoin(ADOPTED_SITES)
                .on(ADOPTED_SITES.SITE_ID.eq(SITE_ENTRIES.ID))
                .where(
                    SITE_ENTRIES.UPDATED_AT.in(
                        this.db
                            .select(max(SITE_ENTRIES.UPDATED_AT))
                            .from(SITE_ENTRIES)
                            .groupBy(SITE_ENTRIES.SITE_ID)
                            .fetch()))
                .orderBy(SITES.ID)
                .fetch();
    Result<
            Record11<
                Integer, // Site ID
                Boolean, // Tree Present
                Double, // Diameter
                String, // Species
                Timestamp, // Updated At
                Date, // Planting Date
                String, // Username
                Integer, // Adopter User ID
                String, // Address
                BigDecimal, // Lat
                BigDecimal>> // Lng
        nullUserRecords =
            this.db
                .select(
                    SITES.ID,
                    SITE_ENTRIES.TREE_PRESENT,
                    SITE_ENTRIES.DIAMETER,
                    SITE_ENTRIES.SPECIES,
                    SITE_ENTRIES.UPDATED_AT,
                    SITE_ENTRIES.PLANTING_DATE,
                    ENTRY_USERNAMES.USERNAME,
                    ADOPTED_SITES.USER_ID,
                    SITES.ADDRESS,
                    SITES.LAT,
                    SITES.LNG)
                .from(SITES)
                .innerJoin(SITE_ENTRIES)
                .on(SITES.ID.eq(SITE_ENTRIES.SITE_ID))
                .innerJoin(ENTRY_USERNAMES)
                .on(SITE_ENTRIES.ID.eq(ENTRY_USERNAMES.ENTRY_ID))
                .leftJoin(ADOPTED_SITES)
                .on(ADOPTED_SITES.SITE_ID.eq(SITE_ENTRIES.ID))
                .where(
                    SITE_ENTRIES
                        .UPDATED_AT
                        .in(
                            this.db
                                .select(max(SITE_ENTRIES.UPDATED_AT))
                                .from(SITE_ENTRIES)
                                .groupBy(SITE_ENTRIES.SITE_ID)
                                .fetch())
                        .and(SITE_ENTRIES.USER_ID.isNull()))
                .orderBy(SITES.ID)
                .fetch();
    List<
            Record11<
                Integer, // Site ID
                Boolean, // Tree Present
                Double, // Diameter
                String, // Species
                Timestamp, // Updated At
                Date, // Planting Date
                String, // Username
                Integer, // Adopter User ID
                String, // Address
                BigDecimal, // Lat
                BigDecimal>> // Lng
        allSiteEntriesRecords =
            this.mergeSorted(
                nonNullUserRecords, nullUserRecords, Comparator.comparingInt(Record11::value1));
    List<SiteFeature> features =
        allSiteEntriesRecords.stream()
            .map(this::siteFeatureFromRecord)
            .collect(Collectors.toList());
    return new SiteGeoResponse(features);
  }
}
