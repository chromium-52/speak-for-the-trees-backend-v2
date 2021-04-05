package com.codeforcommunity.dto.team;

import com.codeforcommunity.enums.TeamRole;

public class UsersTeamDataResponse {
  private Integer userId;
  private Integer teamId;
  private TeamRole teamRole;

  public UsersTeamDataResponse(Integer userId, Integer teamId, TeamRole teamRole) {
    this.userId = userId;
    this.teamId = teamId;
    this.teamRole = teamRole;
    // TODO fix to be the same as the APISpec or change the APISpec
  }

  private UsersTeamDataResponse() {}

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getTeamId() {
    return teamId;
  }

  public void setTeamId(Integer teamId) {
    this.teamId = teamId;
  }

  public TeamRole getTeamRole() {
    return teamRole;
  }

  public void setTeamRole(TeamRole teamRole) {
    this.teamRole = teamRole;
  }
}
