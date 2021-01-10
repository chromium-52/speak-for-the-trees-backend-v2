package com.codeforcommunity.dto.map;

import java.math.BigDecimal;

public class BlockFeatureProperties {
  private final Integer block_id;
  private final BigDecimal lat;
  private final BigDecimal lng;

  public BlockFeatureProperties(Integer block_id, BigDecimal lat, BigDecimal lng) {
    this.block_id = block_id;
    this.lat = lat;
    this.lng = lng;
  }

  public Integer getBlock_id() {
    return block_id;
  }

  public BigDecimal getLat() {
    return lat;
  }

  public BigDecimal getLng() {
    return lng;
  }
}
