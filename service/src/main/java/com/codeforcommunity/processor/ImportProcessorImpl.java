package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.BLOCKS;
import static org.jooq.generated.Tables.TEAMS;
import static org.jooq.generated.Tables.USERS;

import com.codeforcommunity.api.IImportProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.imports.BlockImport;
import com.codeforcommunity.dto.imports.ImportBlocksRequest;
import com.codeforcommunity.dto.imports.ImportNeighborhoodsRequest;
import com.codeforcommunity.dto.imports.ImportReservationsRequest;
import com.codeforcommunity.dto.imports.NeighborhoodImport;
import com.codeforcommunity.dto.imports.ReservationImport;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.exceptions.ResourceDoesNotExistException;
import com.codeforcommunity.exceptions.RouteInvalidException;
import com.codeforcommunity.exceptions.UserDoesNotExistException;
import org.jooq.DSLContext;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.records.BlocksRecord;
import org.jooq.generated.tables.records.NeighborhoodsRecord;
import org.jooq.generated.tables.records.ReservationsRecord;

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

    for (ReservationImport reservationImport : importReservationsRequest.getReservations()) {
      if (!db.fetchExists(
          db.selectFrom(BLOCKS).where(BLOCKS.ID.eq(reservationImport.getBlockId())))) {
        throw new ResourceDoesNotExistException(reservationImport.getBlockId(), "block");
      }
      if (reservationImport.getUserId() != null
          && (!db.fetchExists(
              db.selectFrom(USERS).where(USERS.ID.eq(reservationImport.getUserId()))))) {
        throw new UserDoesNotExistException(reservationImport.getUserId());
      }
      if (reservationImport.getTeamId() != null
          && (!db.fetchExists(
              db.selectFrom(TEAMS).where(TEAMS.ID.eq(reservationImport.getTeamId()))))) {
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
      if (reservationImport.getUserId() == null && reservationImport.getTeamId() == null) {
        reservation.setUserId(superAdminId);
      }
      reservation.setActionType(reservationImport.getActionType());
      reservation.setPerformedAt(reservationImport.getPerformedAt());

      reservation.store();
    }
  }
}
