package com.codeforcommunity.dto.map;

import java.math.BigDecimal;

public class NeighborhoodFeatureProperties {
  private final Integer neighborhood_id;
  private final String name;
  private final Integer completion_perc;
  private final BigDecimal lat;
  private final BigDecimal lng;

  public NeighborhoodFeatureProperties(
      Integer neighborhood_id,
      String name,
      Integer completion_perc,
      BigDecimal lat,
      BigDecimal lng) {
    this.neighborhood_id = neighborhood_id;
    this.name = name;
    this.completion_perc = completion_perc;
    this.lat = lat;
    this.lng = lng;
  }

  public Integer getNeighborhood_id() {
    return neighborhood_id;
  }

  public String getName() {
    return name;
  }

  public Integer getCompletion_perc() {
    return completion_perc;
  }

  public BigDecimal getLat() {
    return lat;
  }

  public BigDecimal getLng() {
    return lng;
  }
}
