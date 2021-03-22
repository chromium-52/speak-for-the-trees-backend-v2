package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class MemberApplicationException extends HandledException {
  private int teamId;
  private int userId;

  public MemberApplicationException(Integer teamId, Integer userId) {
    super();
    this.teamId = teamId;
    this.userId = userId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleMemberApplicationException(ctx, this);
  }

  public int getTeamId() {
    return teamId;
  }

  public int getUserId() {
    return userId;
  }
}
