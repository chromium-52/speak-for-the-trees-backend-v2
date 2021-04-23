package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class WrongAdoptionStatusException extends HandledException {
  private Boolean isAlreadyAdopted;

  public WrongAdoptionStatusException(Boolean isAlreadyAdopted) {
    this.isAlreadyAdopted = isAlreadyAdopted;
  }

  public Boolean getAlreadyAdopted() {
    return isAlreadyAdopted;
  }

  public void setAlreadyAdopted(Boolean alreadyAdopted) {
    isAlreadyAdopted = alreadyAdopted;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleWrongAdoptionStatusException(ctx, this);
  }
}
