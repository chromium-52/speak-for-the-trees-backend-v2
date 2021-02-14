package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class MemberApplicationException extends HandledException{
    private Integer teamId;
    private Integer userId;

    public MemberApplicationException(Integer teamId, Integer userId) {
        super();
        this.teamId = teamId;
        this.userId = userId;
    }

    @Override
    public void callHandler(FailureHandler handler, RoutingContext ctx) {
        handler.handleUserAlreadyAppliedForTeam(ctx, this);
    }

    public Integer getTeamId() {
        return teamId;
    }

    public Integer getUserId() {
        return userId;
    }
}
