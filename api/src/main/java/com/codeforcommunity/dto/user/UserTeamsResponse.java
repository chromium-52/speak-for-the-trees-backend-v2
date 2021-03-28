package com.codeforcommunity.dto.user;

import java.util.List;

public class UserTeamsResponse {
  private List<Team> teams;

  public List<Team> getTeams() {
    return teams;
  }

  public void setTeams(List<Team> teams) {
    this.teams = teams;
  }

  public UserTeamsResponse(List<Team> teams) {
    this.teams = teams;
  }
}
