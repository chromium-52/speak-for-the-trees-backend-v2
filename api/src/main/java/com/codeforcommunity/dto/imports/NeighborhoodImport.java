package com.codeforcommunity.dto.imports;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NeighborhoodImport extends ApiDto {
  private Integer neighborhoodId;
  private String name;
  private BigDecimal sqmiles;
  private BigDecimal lat;
  private BigDecimal lng;
  private String geometry;
  private Double canopyCoverage;

  public NeighborhoodImport(
      Integer neighborhoodId,
      String name,
      BigDecimal sqmiles,
      BigDecimal lat,
      BigDecimal lng,
      String geometry,
      Double canopyCoverage) {
    this.neighborhoodId = neighborhoodId;
    this.name = name;
    this.sqmiles = sqmiles;
    this.lat = lat;
    this.lng = lng;
    this.geometry = geometry;
    this.canopyCoverage = canopyCoverage;
  }

  private NeighborhoodImport() {}

  public Integer getNeighborhoodId() {
    return neighborhoodId;
  }

  public void setNeighborhoodId(Integer neighborhoodId) {
    this.neighborhoodId = neighborhoodId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getSqmiles() {
    return sqmiles;
  }

  public void setSqmiles(BigDecimal sqmiles) {
    this.sqmiles = sqmiles;
  }

  public BigDecimal getLat() {
    return lat;
  }

  public void setLat(BigDecimal lat) {
    this.lat = lat;
  }

  public BigDecimal getLng() {
    return lng;
  }

  public void setLng(BigDecimal lng) {
    this.lng = lng;
  }

  public String getGeometry() {
    return geometry;
  }

  public void setGeometry(String geometry) {
    this.geometry = geometry;
  }

  public Double getCanopyCoverage() {
    return canopyCoverage;
  }

  public void setCanopyCoverage(Double canopyCoverage) {
    this.canopyCoverage = canopyCoverage;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "neighborhood.";
    List<String> fields = new ArrayList<>();

    if (neighborhoodId == null) {
      fields.add(fieldName + "neighborhoodId");
    }
    if (isEmpty(name) || name.length() > 30) {
      fields.add(fieldName + "name");
    }
    if (sqmiles == null || sqmiles.compareTo(BigDecimal.ZERO) <= 0) {
      fields.add(fieldName + "sqmiles");
    }
    if (lat == null) {
      fields.add(fieldName + "lat");
    }
    if (lng == null) {
      fields.add(fieldName + "lng");
    }
    if (isEmpty(geometry)) {
      fields.add(fieldName + "geometry");
    }
    if (canopyCoverage == null || canopyCoverage < 0 || canopyCoverage > 1) {
      fields.add(fieldName + "canopyCoverage");
    }

    return fields;
  }
}
