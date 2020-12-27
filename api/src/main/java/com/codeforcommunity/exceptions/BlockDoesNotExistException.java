package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class BlockDoesNotExistException extends HandledException {

  private final int blockId;

  public BlockDoesNotExistException(int blockId) {
    this.blockId = blockId;
  }

  public int getBlockId() {
    return this.blockId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleBlockDoesNotExist(ctx, this);
  }
}
