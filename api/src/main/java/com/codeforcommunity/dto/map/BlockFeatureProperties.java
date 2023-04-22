package com.codeforcommunity.dto.map;

import java.math.BigDecimal;

public class BlockFeatureProperties {
  private final Integer blockId;
  private final BigDecimal lat;
  private final BigDecimal lng;

  public BlockFeatureProperties(Integer blockId, BigDecimal lat, BigDecimal lng) {
    this.blockId = blockId;
    this.lat = lat;
    this.lng = lng;
  }

  public Integer getBlockId() {
    return blockId;
  }

  public BigDecimal getLat() {
    return lat;
  }

  public BigDecimal getLng() {
    return lng;
  }
}
