package com.codeforcommunity.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public class AdoptedSite {
  private final int siteId;
  private final String address;
  private final String name;
  private final String email;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
  private final Timestamp dateAdopted;

  private final int activityCount;
  private final String neighborhood;

  public AdoptedSite(
      int siteId,
      String address,
      String name,
      String email,
      Timestamp dateAdopted,
      int activityCount,
      String neighborhood) {
    this.siteId = siteId;
    this.address = address;
    this.name = name;
    this.email = email;
    this.dateAdopted = dateAdopted;
    this.activityCount = activityCount;
    this.neighborhood = neighborhood;
  }

  public int getSiteId() {
    return siteId;
  }

  public String getAddress() {
    return address;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public Timestamp getDateAdopted() {
    return dateAdopted;
  }

  public int getActivityCount() {
    return activityCount;
  }

  public String getNeighborhood() {
    return neighborhood;
  }
}
