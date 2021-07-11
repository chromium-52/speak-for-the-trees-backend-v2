package com.codeforcommunity.dto.map;

import java.sql.Date;

public class SiteFeatureProperties {
  Integer id;
  Boolean treePresent;
  String commonName;
  Date plantingDate;
  Integer adopterId;
  String address;

  public SiteFeatureProperties(
      Integer id,
      Boolean treePresent,
      String commonName,
      Date plantingDate,
      Integer adopterId,
      String address) {
    this.id = id;
    this.treePresent = treePresent;
    this.commonName = commonName;
    this.plantingDate = plantingDate;
    this.adopterId = adopterId;
    this.address = address;
  }

  public Integer getId() {
    return id;
  }

  public Boolean getTreePresent() {
    return treePresent;
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

  public Integer getAdopterId() {
    return adopterId;
  }
}
