package com.codeforcommunity.dto.map;

import io.vertx.core.json.JsonObject;

public class NeighborhoodFeature {
  private final String type;
  private final NeighborhoodFeatureProperties properties;
  private final JsonObject geometry;

  public NeighborhoodFeature(NeighborhoodFeatureProperties properties, JsonObject geometry) {
    this.type = "Feature";
    this.properties = properties;
    this.geometry = geometry;
  }

  public String getType() {
    return type;
  }

  public NeighborhoodFeatureProperties getProperties() {
    return properties;
  }

  public JsonObject getGeometry() {
    return geometry;
  }
}
