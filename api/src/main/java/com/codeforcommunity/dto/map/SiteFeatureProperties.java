package com.codeforcommunity.dto.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Date;
import java.sql.Timestamp;

public class SiteFeatureProperties {
  Integer id;
  Boolean treePresent;
  Double diameter;
  String commonName;
  Timestamp updatedAt;
  Date plantingDate;
  Integer adopterId;
  String address;

  public SiteFeatureProperties(
      Integer id,
      Boolean treePresent,
      Double diameter,
      String commonName,
      Timestamp updatedAt,
      Date plantingDate,
      Integer adopterId,
      String address) {
    this.id = id;
    this.treePresent = treePresent;
    this.diameter = diameter;
    this.commonName = commonName;
    this.plantingDate = plantingDate;
    this.updatedAt = updatedAt;
    this.adopterId = adopterId;
    this.address = address;
  }

  public Integer getId() {
    return id;
  }

  @JsonProperty("tree_present")
  public Boolean getTreePresent() {
    return treePresent;
  }

  public Double diameter() {
    return diameter;
  }

  public String getCommonName() {
    return commonName;
  }

  public String getAddress() {
    return address;
  }

  public Date getPlantingDate() {
    return plantingDate;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public Integer getAdopterId() {
    return adopterId;
  }
}
