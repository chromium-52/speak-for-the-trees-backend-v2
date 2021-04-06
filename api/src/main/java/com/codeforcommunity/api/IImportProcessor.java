package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.imports.ImportBlocksRequest;
import com.codeforcommunity.dto.imports.ImportNeighborhoodsRequest;
import com.codeforcommunity.dto.imports.ImportReservationsRequest;
import com.codeforcommunity.dto.imports.ImportSitesRequest;

public interface IImportProcessor {
  void importBlocks(JWTData userData, ImportBlocksRequest importBlocksRequest);

  void importNeighborhoods(JWTData userData, ImportNeighborhoodsRequest importNeighborhoodsRequest);

  void importReservations(JWTData userData, ImportReservationsRequest importReservationsRequest);

  void importSites(JWTData userData, ImportSitesRequest importSitesRequest);
}
