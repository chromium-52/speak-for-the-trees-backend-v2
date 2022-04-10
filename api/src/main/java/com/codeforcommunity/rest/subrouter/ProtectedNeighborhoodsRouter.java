package com.codeforcommunity.rest.subrouter;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.IProtectedNeighborhoodsProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.neighborhoods.SendEmailRequest;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ProtectedNeighborhoodsRouter implements IRouter {

  private final IProtectedNeighborhoodsProcessor processor;

  public ProtectedNeighborhoodsRouter(IProtectedNeighborhoodsProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerSendEmailToNeighborhoods(router);

    return router;
  }

  private void registerSendEmailToNeighborhoods(Router router) {
    Route adoptSiteRoute = router.get("/send_email");
    adoptSiteRoute.handler(this::handleSendEmailToNeighborhoods);
  }

  private void handleSendEmailToNeighborhoods(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    SendEmailRequest sendEmailRequest = RestFunctions.getJsonBodyAsClass(ctx, SendEmailRequest.class);

    processor.sendEmailToNeighborhoods(userData, sendEmailRequest);

    end(ctx.response(), 200);
  }
}
