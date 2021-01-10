package com.codeforcommunity.api;

import com.codeforcommunity.dto.map.BlockGeoResponse;
import com.codeforcommunity.dto.map.NeighborhoodGeoResponse;

public interface IMapProcessor {

  BlockGeoResponse getBlockGeoJson();

  NeighborhoodGeoResponse getNeighborhoodGeoJson();
}
