package com.codeforcommunity.api;

import com.codeforcommunity.dto.map.BlockGeoResponse;
import com.codeforcommunity.dto.map.NeighborhoodGeoResponse;
import com.codeforcommunity.dto.map.SiteGeoResponse;

public interface IMapProcessor {

  BlockGeoResponse getBlockGeoJson();

  NeighborhoodGeoResponse getNeighborhoodGeoJson();

  SiteGeoResponse getSiteGeoJson();
}
