package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class BlockNotReservedException extends HandledException {

  private final int blockId;

  public BlockNotReservedException(int blockId) {
    this.blockId = blockId;
  }

  public int getBlockId() {
    return this.blockId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleBlockNotReserved(ctx, this);
  }
}
