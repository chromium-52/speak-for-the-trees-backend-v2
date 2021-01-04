package com.codeforcommunity.dto.map;

import io.vertx.core.json.JsonObject;

public class BlockFeature {
  private final String type;
  private final BlockFeatureProperties properties;
  private final JsonObject geometry;

  public BlockFeature(BlockFeatureProperties properties, JsonObject geometry) {
    this.type = "Feature";
    this.properties = properties;
    this.geometry = geometry;
  }

  public String getType() {
    return type;
  }

  public BlockFeatureProperties getProperties() {
    return properties;
  }

  public JsonObject getGeometry() {
    return geometry;
  }
}
