package com.codeforcommunity.dto.map;

import java.util.List;

public class SiteGeoResponse {
  private final String type;
  private final String name;
  private final List<SiteFeature> features;

  public SiteGeoResponse(List<SiteFeature> features) {
    this.type = "FeatureCollection";
    this.name = "sites";
    this.features = features;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public List<SiteFeature> getFeatures() {
    return features;
  }
}
