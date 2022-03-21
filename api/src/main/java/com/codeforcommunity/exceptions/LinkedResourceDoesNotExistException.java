package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

/**
 * Resource does not exist exception for resources
 * which combine two other resources,
 * and which have no meaningful id on their own.
 */
public class LinkedResourceDoesNotExistException extends HandledException {

  private final int resource1Id;
  private final int resource2Id;
  private final String resource1Type;
  private final String resource2Type;
  private final String linkedResourceType;

  public LinkedResourceDoesNotExistException(String linkedResourceType,
                                             int resource1Id,
                                             String resource1Type,
                                             int resource2Id,
                                             String resource2Type) {
    this.resource1Id = resource1Id;
    this.resource2Id = resource2Id;
    this.resource1Type = resource1Type;
    this.resource2Type = resource2Type;
    this.linkedResourceType = linkedResourceType;
  }

  public int getResource1Id() {
    return this.resource1Id;
  }

  public int getResource2Id() {
    return this.resource2Id;
  }

  public String getResource1Type() {
    return this.resource1Type;
  }

  public String getResource2Type() {
    return this.resource2Type;
  }

  public String getlinkedResourceType() {
    return this.linkedResourceType;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleLinkedResourceDoesNotExist(ctx, this);
  }
}
