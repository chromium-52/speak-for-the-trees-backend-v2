package com.codeforcommunity.dto.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;

public class SiteFeatureProperties {
  Integer id;
  Boolean treePresent;
  Double diameter;
  String species;
  Timestamp updatedAt;
  String updatedBy; // username
  String address;

  public SiteFeatureProperties(
      Integer id,
      Boolean treePresent,
      Double diameter,
      String species,
      Timestamp updatedAt,
      String updatedBy,
      String address) {
    this.id = id;
    this.treePresent = treePresent;
    this.diameter = diameter;
    this.species = species;
    this.updatedAt = updatedAt;
    this.updatedBy = updatedBy;
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
}
