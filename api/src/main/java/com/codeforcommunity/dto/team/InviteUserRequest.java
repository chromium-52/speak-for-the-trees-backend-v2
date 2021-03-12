package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class InviteUserRequest extends ApiDto {

  private List<InviteContact> users;

  public InviteUserRequest(List<InviteContact> users) {
    this.users = users;
  }

  public InviteUserRequest() {}

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

    if (users == null) {
      fields.add(fieldName + "users");
    }
    // users.forEach(user -> user.validateFields(fieldPrefix));
    return fields;
  }
}
