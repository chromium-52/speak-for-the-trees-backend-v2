package com.codeforcommunity.dto.team;

import com.codeforcommunity.enums.TeamRole;
import java.util.Map;

public class UsersResponse {
  private Map<Integer, TeamRole> users;

  public void setUsers(Map<Integer, TeamRole> users) {
    this.users = users;
  }

  public UsersResponse(Map<Integer, TeamRole> users) {
    this.users = users;
  }

  private UsersResponse() {}

  public Map<Integer, TeamRole> getUsers() {
    return users;
  }
}
