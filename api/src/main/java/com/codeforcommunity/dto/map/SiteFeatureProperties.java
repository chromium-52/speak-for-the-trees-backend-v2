package com.codeforcommunity.dto.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Date;
import java.sql.Timestamp;

public class SiteFeatureProperties {
  Integer id;
  Boolean treePresent;
  Double diameter;
  String species;
  Timestamp updatedAt;
  Date plantingDate;
  String updatedBy; // username
  Integer adopterId;
  String address;

  public SiteFeatureProperties(
      Integer id,
      Boolean treePresent,
      Double diameter,
      String species,
      Timestamp updatedAt,
      Date plantingDate,
      String updatedBy,
      Integer adopterId,
      String address) {
    this.id = id;
    this.treePresent = treePresent;
    this.diameter = diameter;
    this.species = species;
    this.updatedAt = updatedAt;
    this.plantingDate = plantingDate;
    this.updatedBy = updatedBy;
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

  public Double getDiameter() {
    return diameter;
  }

  public String getSpecies() {
    return species;
  }

  @JsonProperty("updated_at")
  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  @JsonProperty("updated_by")
  public String getUpdatedBy() {
    return updatedBy;
  }

  public String getAddress() {
    return address;
  }

  public Date getPlantingDate() {
    return plantingDate;
  }

  public Integer getAdopterId() {
    return adopterId;
  }
}
