package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class ResourceDoesNotExistException extends HandledException {

  private final int resourceId;
  private final String resourceType;

  public ResourceDoesNotExistException(int resourceId, String resourceType) {
    this.resourceId = resourceId;
    this.resourceType = resourceType;
  }

  public int getResourceId() {
    return this.resourceId;
  }

  public String getResourceType() {
    return this.resourceType;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleResourceDoesNotExist(ctx, this);
  }
}
