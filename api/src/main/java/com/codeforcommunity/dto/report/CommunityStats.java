package com.codeforcommunity.dto.report;

public class CommunityStats {
  private final Integer adoptedCount;
  private final Integer treesAdopted;
  private final Integer stewardshipActivities;

  public CommunityStats(Integer adopterCount, Integer treesAdopted, Integer stewardshipActivities) {
    this.adoptedCount = adopterCount;
    this.treesAdopted = treesAdopted;
    this.stewardshipActivities = stewardshipActivities;
  }

  public Integer getAdoptedCount() {
    return this.adoptedCount;
  }

  public Integer getTreesAdopted() {
    return this.treesAdopted;
  }

  public Integer getStewardshipActivities() {
    return this.stewardshipActivities;
  }
}