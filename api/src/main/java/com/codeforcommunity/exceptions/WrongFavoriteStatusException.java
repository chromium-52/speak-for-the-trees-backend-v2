package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class WrongFavoriteStatusException extends HandledException {
    private Boolean isAlreadyFavorite;

    public WrongFavoriteStatusException(Boolean isAlreadyFavorite) {
        this.isAlreadyFavorite = isAlreadyFavorite;
    }

    public Boolean getAlreadyFavorite() {
        return isAlreadyFavorite;
    }

    public void setAlreadyFavorite(Boolean alreadyFavorite) {
        isAlreadyFavorite = alreadyFavorite;
    }

    @Override
    public void callHandler(FailureHandler handler, RoutingContext ctx) {
        handler.handleWrongFavoriteStatusException(ctx, this);
    }
}
