package com.codeforcommunity.dto.map;

import java.math.BigDecimal;

public class NeighborhoodFeatureProperties {
  private final Integer neighborhood_id;
  private final String name;
  private final Integer completion_perc;
  private final Double canopy_coverage;
  private final BigDecimal lat;
  private final BigDecimal lng;

  public NeighborhoodFeatureProperties(
      Integer neighborhood_id,
      String name,
      Integer completion_perc,
      Double canopy_coverage,
      BigDecimal lat,
      BigDecimal lng) {
    this.neighborhood_id = neighborhood_id;
    this.name = name;
    this.completion_perc = completion_perc;
    this.canopy_coverage = canopy_coverage;
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

  public Double getCanopy_coverage() {
    return canopy_coverage;
  }

  public BigDecimal getLat() {
    return lat;
  }

  public BigDecimal getLng() {
    return lng;
  }
}
