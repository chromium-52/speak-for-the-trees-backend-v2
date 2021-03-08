package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class MemberStatusException extends HandledException {
  private Integer teamId;
  private Integer userId;

  public Integer getTeamId() {
    return teamId;
  }

  public Integer getUserId() {
    return userId;
  }

  public MemberStatusException(Integer teamId, Integer userId) {
    this.teamId = teamId;
    this.userId = userId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleMemberStatusException(ctx, this);
  }
}
