package com.codeforcommunity.dto.map;

public class SiteFeature {
  private final String type;
  private final SiteFeatureProperties properties;
  private final GeometryPoint geometry;

  public SiteFeature(SiteFeatureProperties properties, GeometryPoint geometry) {
    this.type = "Feature";
    this.properties = properties;
    this.geometry = geometry;
  }

  public String getType() {
    return type;
  }

  public SiteFeatureProperties getProperties() {
    return properties;
  }

  public GeometryPoint getGeometry() {
    return geometry;
  }
}
