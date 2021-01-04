package com.codeforcommunity.dto.map;

import java.util.List;

public class NeighborhoodGeoResponse {
  private final String type;
  private final String name;
  private final List<NeighborhoodFeature> features;

  public NeighborhoodGeoResponse(List<NeighborhoodFeature> features) {
    this.type = "FeatureCollection";
    this.name = "neighborhoods";
    this.features = features;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public List<NeighborhoodFeature> getFeatures() {
    return features;
  }
}
