package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class BlockNotCompleteException extends HandledException {

  private final int blockId;

  public BlockNotCompleteException(int blockId) {
    this.blockId = blockId;
  }

  public int getBlockId() {
    return this.blockId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleBlockNotComplete(ctx, this);
  }
}
