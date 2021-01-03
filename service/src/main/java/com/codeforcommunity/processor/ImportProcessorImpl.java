package com.codeforcommunity.processor;

import com.codeforcommunity.api.IImportProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.imports.BlockImport;
import com.codeforcommunity.dto.imports.ImportBlocksRequest;
import com.codeforcommunity.dto.imports.ImportNeighborhoodsRequest;
import com.codeforcommunity.dto.imports.NeighborhoodImport;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AuthException;
import org.jooq.DSLContext;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.records.BlocksRecord;
import org.jooq.generated.tables.records.NeighborhoodsRecord;

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

    for (BlockImport blockImport : importBlocksRequest.getBlocks()) {
      BlocksRecord block = db.newRecord(Tables.BLOCKS);
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
}
