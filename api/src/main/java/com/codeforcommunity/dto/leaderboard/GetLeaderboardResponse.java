package com.codeforcommunity.dto.leaderboard;

import java.util.List;

public class GetLeaderboardResponse {
  private final List<LeaderboardEntry> entries;

  public GetLeaderboardResponse(List<LeaderboardEntry> entries) {
    this.entries = entries;
  }

  public List<LeaderboardEntry> getEntries() {
    return entries;
  }
}
