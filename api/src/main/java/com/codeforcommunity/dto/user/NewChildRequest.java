package com.codeforcommunity.dto.user;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.InvalidPasswordException;

import java.util.ArrayList;
import java.util.List;

public class NewChildRequest extends ApiDto {

  private String childUsername;
  private String childEmail;
  private String childPassword;
  private String childFirstName;
  private String childLastName;

  public String getChildUsername() {
    return childUsername;
  }

  public void setChildUsername(String childUsername) {
    this.childUsername = childUsername;
  }

  public String getChildEmail() {
    return childEmail;
  }

  public void setChildEmail(String childEmail) {
    this.childEmail = childEmail;
  }

  public String getChildPassword() {
    return childPassword;
  }

  public void setChildPassword(String childPassword) {
    this.childPassword = childPassword;
  }

  public String getChildFirstName() {
    return childFirstName;
  }

  public void setChildFirstName(String childFirstName) {
    this.childFirstName = childFirstName;
  }

  public String getChildLastName() {
    return childLastName;
  }

  public void setChildLastName(String childLastName) {
    this.childLastName = childLastName;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) {
    String fieldName = fieldPrefix + "new_user_request.";
    List<String> fields = new ArrayList<>();

    if (isEmpty(childUsername) || childUsername.length() > 36) {
      fields.add(fieldName + "child_username");
    }
    if (childEmail == null || emailInvalid(childEmail)) {
      fields.add(fieldName + "child_email");
    }
    if (isEmpty(childFirstName) || childFirstName.length() > 36) {
      fields.add(fieldName + "child_first_name");
    }
    if (isEmpty(childLastName) || childLastName.length() > 36) {
      fields.add(fieldName + "child_last_name");
    }
    if (childPassword == null) {
      fields.add(fieldName + "child_password");
    }
    // Only throw this exception if there are no issues with other fields
    if (passwordInvalid(childPassword) && fields.size() == 0) {
      throw new InvalidPasswordException();
    }
    return fields;
  }
}
