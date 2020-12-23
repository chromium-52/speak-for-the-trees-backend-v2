package com.codeforcommunity.enums;

public enum TeamRole {
  None("none"),
  MEMBER("member"),
  LEADER("leader"),
  PENDING("pending");

  private String name;

  TeamRole(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static TeamRole from(String name) {
    for (TeamRole teamRole : TeamRole.values()) {
      if (teamRole.name.equals(name)) {
        return teamRole;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name `%s` doesn't correspond to any `TeamRole`", name));
  }
}
