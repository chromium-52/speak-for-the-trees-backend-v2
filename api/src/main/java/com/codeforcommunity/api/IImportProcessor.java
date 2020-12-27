package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.imports.ImportBlocksRequest;
import com.codeforcommunity.dto.imports.ImportNeighborhoodsRequest;

public interface IImportProcessor {
  void importBlocks(JWTData userData, ImportBlocksRequest importBlocksRequest);

  void importNeighborhoods(JWTData userData, ImportNeighborhoodsRequest importNeighborhoodsRequest);
}
