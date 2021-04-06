package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.api.ISiteProcessor;
import com.codeforcommunity.rest.IRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class SiteRouter implements IRouter {

  private final ISiteProcessor processor;

  public SiteRouter(ISiteProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    return router;
  }
}
