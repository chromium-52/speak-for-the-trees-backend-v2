package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.BLOCKS;
import static org.jooq.generated.Tables.ENTRY_USERNAMES;
import static org.jooq.generated.Tables.TEAMS;
import static org.jooq.generated.Tables.USERS;

import com.codeforcommunity.api.IImportProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.imports.BlockImport;
import com.codeforcommunity.dto.imports.ImportBlocksRequest;
import com.codeforcommunity.dto.imports.ImportNeighborhoodsRequest;
import com.codeforcommunity.dto.imports.ImportReservationsRequest;
import com.codeforcommunity.dto.imports.ImportSitesRequest;
import com.codeforcommunity.dto.imports.NeighborhoodImport;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.exceptions.ResourceDoesNotExistException;
import com.codeforcommunity.exceptions.RouteInvalidException;
import com.codeforcommunity.exceptions.UserDoesNotExistException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.records.BlocksRecord;
import org.jooq.generated.tables.records.EntryUsernamesRecord;
import org.jooq.generated.tables.records.NeighborhoodsRecord;
import org.jooq.generated.tables.records.ReservationsRecord;
import org.jooq.generated.tables.records.SiteEntriesRecord;
import org.jooq.generated.tables.records.SitesRecord;

public class ImportProcessorImpl implements IImportProcessor {
  private final DSLContext db;

  public ImportProcessorImpl(DSLContext db) {
    this.db = db;
  }

  @Override
  public void importBlocks(JWTData userData, ImportBlocksRequest importBlocksRequest) {
    if (userData.getPrivilegeLevel() != PrivilegeLevel.SUPER_ADMIN) {
      throw new AuthException("User does not have the required privilege level.");
    }

    if (!db.fetchExists(Tables.NEIGHBORHOODS)) {
      throw new RouteInvalidException("Blocks cannot be seeded when no neighborhoods exist.");
    }

    for (BlockImport blockImport : importBlocksRequest.getBlocks()) {
      BlocksRecord block = db.newRecord(BLOCKS);
      block.setId(blockImport.getBlockId());
      block.setNeighborhoodId(blockImport.getNeighborhoodId());
      block.setLat(blockImport.getLat());
      block.setLng(blockImport.getLng());
      block.setGeometry(blockImport.getGeometry());
      block.store();
    }
  }

  @Override
  public void importNeighborhoods(
      JWTData userData, ImportNeighborhoodsRequest importNeighborhoodsRequest) {
    if (userData.getPrivilegeLevel() != PrivilegeLevel.SUPER_ADMIN) {
      throw new AuthException("User does not have the required privilege level.");
    }

    for (NeighborhoodImport neighborhoodImport : importNeighborhoodsRequest.getNeighborhoods()) {
      NeighborhoodsRecord neighborhood = db.newRecord(Tables.NEIGHBORHOODS);
      neighborhood.setId(neighborhoodImport.getNeighborhoodId());
      neighborhood.setNeighborhoodName(neighborhoodImport.getName());
      neighborhood.setSqmiles(neighborhoodImport.getSqmiles());
      neighborhood.setLat(neighborhoodImport.getLat());
      neighborhood.setLng(neighborhoodImport.getLng());
      neighborhood.setGeometry(neighborhoodImport.getGeometry());

      neighborhood.store();
    }
  }

  @Override
  public void importReservations(
      JWTData userData, ImportReservationsRequest importReservationsRequest) {
    if (userData.getPrivilegeLevel() != PrivilegeLevel.SUPER_ADMIN) {
      throw new AuthException("User does not have the required privilege level.");
    }

    // Get superuser ID
    Integer superAdminId = userData.getUserId();

    // First check that none of the entries contain errors
    List<ReservationsRecord> records =
        importReservationsRequest.getReservations().stream()
            .map(
                reservationImport -> {
                  if (!db.fetchExists(
                      db.selectFrom(BLOCKS).where(BLOCKS.ID.eq(reservationImport.getBlockId())))) {
                    throw new ResourceDoesNotExistException(
                        reservationImport.getBlockId(), "block");
                  }
                  if (reservationImport.getUserId() != null
                      && (!db.fetchExists(
                          db.selectFrom(USERS)
                              .where(USERS.ID.eq(reservationImport.getUserId()))))) {
                    throw new UserDoesNotExistException(reservationImport.getUserId());
                  }
                  if (reservationImport.getTeamId() != null
                      && (!db.fetchExists(
                          db.selectFrom(TEAMS)
                              .where(TEAMS.ID.eq(reservationImport.getTeamId()))))) {
                    throw new ResourceDoesNotExistException(reservationImport.getTeamId(), "block");
                  }

                  ReservationsRecord reservation = db.newRecord(Tables.RESERVATIONS);
                  reservation.setBlockId(reservationImport.getBlockId());
                  if (reservationImport.getUserId() != null) {
                    reservation.setUserId(reservationImport.getUserId());
                  }
                  if (reservationImport.getTeamId() != null) {
                    reservation.setTeamId(reservationImport.getTeamId());
                  }
                  if (reservationImport.getUserId() == null
                      && reservationImport.getTeamId() == null) {
                    reservation.setUserId(superAdminId);
                  }
                  reservation.setActionType(reservationImport.getActionType());
                  reservation.setPerformedAt(reservationImport.getPerformedAt());

                  return reservation;
                })
            .collect(Collectors.toList());

    // If none of the blocks contained errors it's safe to store the blocks
    for (ReservationsRecord record : records) {
      record.store();
    }
  }

  private void importSiteEntryUsername(Integer siteEntryId, String username) {
    EntryUsernamesRecord record = db.newRecord(ENTRY_USERNAMES);

    record.setEntryId(siteEntryId);
    record.setUsername(username);

    record.store();
  }

  @Override
  public void importSites(JWTData userData, ImportSitesRequest importSitesRequest) {
    if (userData.getPrivilegeLevel() != PrivilegeLevel.SUPER_ADMIN) {
      throw new AuthException("User does not have the required privilege level.");
    }
    if ((!db.fetchExists(db.selectFrom(USERS).where(USERS.ID.eq(userData.getUserId()))))) {
      throw new UserDoesNotExistException(userData.getUserId());
    }

    List<SitesRecord> sitesRecords = new ArrayList<>();
    List<Map.Entry<SiteEntriesRecord, String>> pairList = new ArrayList<>();

    importSitesRequest
        .getSites()
        .forEach(
            siteImport -> {
              SitesRecord site = db.newRecord(Tables.SITES);
              SiteEntriesRecord siteEntry = db.newRecord(Tables.SITE_ENTRIES);

              if (siteImport.getBlockId() != null
                  && !db.fetchExists(
                      db.selectFrom(BLOCKS).where(BLOCKS.ID.eq(siteImport.getBlockId())))) {
                throw new ResourceDoesNotExistException(siteImport.getBlockId(), "block");
              }

              // Set all values for the site record
              site.setId(siteImport.getSiteId());
              site.setBlockId(siteImport.getBlockId());
              site.setLat(siteImport.getLat());
              site.setLng(siteImport.getLng());
              site.setCity(siteImport.getCity());
              site.setZip(siteImport.getZip());
              site.setAddress(siteImport.getAddress());
              if (siteImport.getDeletedAt() != null) {
                site.setDeletedAt(siteImport.getDeletedAt());
              }

              // Set all values for the site entry record
              siteEntry.setSiteId(siteImport.getSiteId());
              // siteEntry.setUserId(1); userId is not set due to lack of userId's
              siteEntry.setUpdatedAt(siteImport.getUpdatedAt());
              siteEntry.setQa(siteImport.getQa());
              siteEntry.setTreePresent(siteImport.getTreePresent());
              siteEntry.setStatus(siteImport.getStatus());
              siteEntry.setGenus(siteImport.getGenus());
              siteEntry.setSpecies(siteImport.getSpecies());
              siteEntry.setCommonName(siteImport.getCommonName());
              siteEntry.setConfidence(siteImport.getConfidence());
              siteEntry.setMultistem(siteImport.getMultistem());
              siteEntry.setDiameter(siteImport.getDiameter());
              siteEntry.setCircumference(siteImport.getCircumference());
              siteEntry.setCoverage(siteImport.getCoverage());
              siteEntry.setPruning(siteImport.getPruning());
              siteEntry.setCondition(siteImport.getCondition());
              siteEntry.setDiscoloring(siteImport.getDiscoloring());
              siteEntry.setLeaning(siteImport.getLeaning());
              siteEntry.setConstrictingGate(siteImport.getConstrictingGate());
              siteEntry.setWounds(siteImport.getWounds());
              siteEntry.setPooling(siteImport.getPooling());
              siteEntry.setStakesWithWires(siteImport.getStakesWithWires());
              siteEntry.setStakesWithoutWires(siteImport.getStakesWithoutWires());
              siteEntry.setLight(siteImport.getLight());
              siteEntry.setBicycle(siteImport.getBicycle());
              siteEntry.setBagEmpty(siteImport.getBagEmpty());
              siteEntry.setBagFilled(siteImport.getBagFilled());
              siteEntry.setTape(siteImport.getTape());
              siteEntry.setSuckerGrowth(siteImport.getSuckerGrowth());
              siteEntry.setSiteType(siteImport.getSiteType());
              siteEntry.setSidewalkWidth(siteImport.getSidewalkWidth());
              siteEntry.setSiteWidth(siteImport.getSiteWidth());
              siteEntry.setSiteLength(siteImport.getSiteLength());
              siteEntry.setMaterial(siteImport.getMaterial());
              siteEntry.setRaisedBed(siteImport.getRaisedBed());
              siteEntry.setFence(siteImport.getFence());
              siteEntry.setTrash(siteImport.getTrash());
              siteEntry.setWires(siteImport.getWires());
              siteEntry.setGrate(siteImport.getGrate());
              siteEntry.setStump(siteImport.getStump());
              siteEntry.setTreeNotes(siteImport.getTreeNotes());
              siteEntry.setSiteNotes(siteImport.getSiteNotes());
              siteEntry.setMelneaCassTrees(siteImport.getMelneaCassTrees());
              siteEntry.setMcbNumber(siteImport.getMcbNumber());
              siteEntry.setTreeDedicatedTo(siteImport.getTreeDedicatedTo());

              sitesRecords.add(site);
              pairList.add(new AbstractMap.SimpleEntry<>(siteEntry, siteImport.getUsername()));
            });

    for (SitesRecord record : sitesRecords) {
      record.store();
    }

    for (Map.Entry<SiteEntriesRecord, String> pair : pairList) {
      pair.getKey().store();
      Integer siteEntryId = pair.getKey().getId();
      String username = pair.getValue();
      if (username != null && !username.equals("")) {
        importSiteEntryUsername(siteEntryId, pair.getValue());
      }
    }
  }
}
