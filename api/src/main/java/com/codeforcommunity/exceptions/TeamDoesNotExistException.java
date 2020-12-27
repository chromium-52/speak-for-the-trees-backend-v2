package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class TeamDoesNotExistException extends HandledException {

  private final int teamId;

  public TeamDoesNotExistException(int teamId) {
    this.teamId = teamId;
  }

  public int getTeamId() {
    return this.teamId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleTeamDoesNotExist(ctx, this);
  }
}
