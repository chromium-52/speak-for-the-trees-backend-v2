package com.codeforcommunity.dto.user;

public class UserDataResponse {

  private String firstName;
  private String lastName;
  private String email;
  private String username;

  public UserDataResponse(String firstName, String lastName, String email, String username) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getUsername() {
    return username;
  }
}
