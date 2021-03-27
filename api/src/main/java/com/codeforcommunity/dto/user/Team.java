package com.codeforcommunity.dto.user;

public class Team {
  private Integer teamId;
  private String teamName;

  public Team(Integer teamId, String teamName) {
    this.teamId = teamId;
    this.teamName = teamName;
  }

  public Integer getTeamId() {
    return teamId;
  }

  public void setTeamId(Integer teamId) {
    this.teamId = teamId;
  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }
}
