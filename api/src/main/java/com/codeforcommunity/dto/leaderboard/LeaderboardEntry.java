package com.codeforcommunity.dto.leaderboard;

public class LeaderboardEntry {
  private int id;
  private String name;
  private int blocksCounted;

  public LeaderboardEntry(int id, String name, int blocksCounted) {
    this.id = id;
    this.name = name;
    this.blocksCounted = blocksCounted;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getBlocksCounted() {
    return blocksCounted;
  }
}
