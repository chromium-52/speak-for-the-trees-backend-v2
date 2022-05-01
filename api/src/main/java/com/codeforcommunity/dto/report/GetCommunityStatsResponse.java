package com.codeforcommunity.dto.report;

public class GetCommunityStatsResponse {
  private final CommunityStats communityStats;

  public GetCommunityStatsResponse(CommunityStats communityStats) {
    this.communityStats = communityStats;
  }

  public CommunityStats getCommunityStats() {
    return this.communityStats;
  }
}
