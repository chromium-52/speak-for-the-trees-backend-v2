package com.codeforcommunity.dto.map;

import java.util.List;

public class BlockGeoResponse {
  private final String type;
  private final String name;
  private final List<BlockFeature> features;

  public BlockGeoResponse(List<BlockFeature> features) {
    this.type = "FeatureCollection";
    this.name = "blocks";
    this.features = features;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public List<BlockFeature> getFeatures() {
    return features;
  }
}
