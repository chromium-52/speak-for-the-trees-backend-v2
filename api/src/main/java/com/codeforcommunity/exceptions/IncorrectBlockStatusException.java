package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class IncorrectBlockStatusException extends HandledException {

  private final int blockId;
  private final String expectedStatus;

  public IncorrectBlockStatusException(int blockId, String expectedStatus) {
    this.blockId = blockId;
    this.expectedStatus = expectedStatus;
  }

  public int getBlockId() {
    return this.blockId;
  }

  public String getExpectedStatus() {
    return this.expectedStatus;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleIncorrectBlockStatus(ctx, this);
  }
}
