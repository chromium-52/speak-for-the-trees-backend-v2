package com.codeforcommunity.dto.imports;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BlockImport extends ApiDto {
  private Integer blockId;
  private Integer neighborhoodId;
  private BigDecimal lat;
  private BigDecimal lng;
  private String geometry;

  public BlockImport(
      Integer blockId, Integer neighborhoodId, BigDecimal lat, BigDecimal lng, String geometry) {
    this.blockId = blockId;
    this.neighborhoodId = neighborhoodId;
    this.lat = lat;
    this.lng = lng;
    this.geometry = geometry;
  }

  private BlockImport() {}

  public Integer getBlockId() {
    return blockId;
  }

  public void setBlockId(Integer blockId) {
    this.blockId = blockId;
  }

  public Integer getNeighborhoodId() {
    return neighborhoodId;
  }

  public void setNeighborhoodId(Integer neighborhoodId) {
    this.neighborhoodId = neighborhoodId;
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

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "blocks.";
    List<String> fields = new ArrayList<>();

    if (blockId == null) {
      fields.add(fieldName + "blockId");
    }
    if (neighborhoodId == null) {
      fields.add(fieldName + "neighborhoodId");
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

    return fields;
  }
}
