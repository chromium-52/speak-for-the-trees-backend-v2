package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class RouteInvalidException extends HandledException {
  private final String message;

  public RouteInvalidException(String message) {
    super(message);
    this.message = message;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleRouteInvalid(ctx, this.message);
  }
}
