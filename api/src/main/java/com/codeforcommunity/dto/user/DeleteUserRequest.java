package com.codeforcommunity.dto.user;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class DeleteUserRequest extends ApiDto {
  private String password;

  public DeleteUserRequest(String password) {
    this.password = password;
  }

  private DeleteUserRequest() {}

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "delete_user_request.";
    List<String> fields = new ArrayList<>();

    if (password == null) {
      fields.add(fieldName + "password");
    }
    return fields;
  }
}
