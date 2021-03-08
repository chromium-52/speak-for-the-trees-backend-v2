package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InviteUserRequest extends ApiDto {

  private Integer teamId;
  private List<InviteContact> users;

  public InviteUserRequest(Integer teamId, List<InviteContact> users) {
    this.teamId = teamId;
    this.users = users;
  }

  public InviteUserRequest() {}


  public Integer getTeamId() {
    return teamId;
  }

  public void setTeamId(Integer teamId) {
    this.teamId = teamId;
  }

  public List<InviteContact> getUsers() {
    return users;
  }

  public void setUsers(List<InviteContact> users) {
    this.users = users;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "invite_user_request.";
    List<String> fields = new ArrayList<>();

    if (teamId == null) {
      fields.add(fieldName + "team_id");
    }
    if (users == null) {
      fields.add(fieldName + "users");
    }
    // users.forEach(user -> user.validateFields(fieldPrefix));
    return fields;
  }
}
