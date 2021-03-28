package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class LeaderCannotLeaveTeamException extends HandledException {
  private int teamId;
  private int userId;

  public int getTeamId() {
    return teamId;
  }

  public int getUserId() {
    return userId;
  }

  public LeaderCannotLeaveTeamException(int teamId, int userId) {
    this.teamId = teamId;
    this.userId = userId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleLeaderCannotLeaveTeamException(ctx, this);
  }
}
