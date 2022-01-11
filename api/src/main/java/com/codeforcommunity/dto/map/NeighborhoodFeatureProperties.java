package com.codeforcommunity.dto.map;

import java.math.BigDecimal;

public class NeighborhoodFeatureProperties {
  private final Integer neighborhoodId;
  private final String name;
  private final Integer completionPerc;
  private final Double canopyCoverage;
  private final BigDecimal lat;
  private final BigDecimal lng;

  public NeighborhoodFeatureProperties(
      Integer neighborhoodId,
      String name,
      Integer completionPerc,
      Double canopyCoverage,
      BigDecimal lat,
      BigDecimal lng) {
    this.neighborhoodId = neighborhoodId;
    this.name = name;
    this.completionPerc = completionPerc;
    this.canopyCoverage = canopyCoverage;
    this.lat = lat;
    this.lng = lng;
  }

  public Integer getNeighborhoodId() {
    return neighborhoodId;
  }

  public String getName() {
    return name;
  }

  public Integer getCompletionPerc() {
    return completionPerc;
  }

  public Double getCanopyCoverage() {
    return canopyCoverage;
  }

  public BigDecimal getLat() {
    return lat;
  }

  public BigDecimal getLng() {
    return lng;
  }
}
