package com.codeforcommunity.exceptions;

import com.codeforcommunity.enums.TeamRole;
import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class WrongTeamRoleException extends HandledException {
  private int teamId;
  private TeamRole teamRole;

  public WrongTeamRoleException(int teamId, TeamRole teamRole) {
    this.teamId = teamId;
    this.teamRole = teamRole;
  }

  public int getTeamId() {
    return teamId;
  }

  public TeamRole getTeamRole() {
    return teamRole;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleWrongTeamRoleException(ctx, this);
  }
}
