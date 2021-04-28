package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class UserDeletedException extends HandledException {
  private String identifierMessage;

  public UserDeletedException(int userId) {
    this.identifierMessage = "id = " + userId;
  }

  public UserDeletedException(String email) {
    this.identifierMessage = "email = " + email;
  }

  public String getIdentifierMessage() {
    return this.identifierMessage;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleUserDeleted(ctx, this);
  }
}
