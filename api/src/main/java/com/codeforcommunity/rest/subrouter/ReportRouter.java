package com.codeforcommunity.rest.subrouter;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.IReportProcessor;
import com.codeforcommunity.dto.report.GetCommunityStatsResponse;
import com.codeforcommunity.rest.IRouter;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ReportRouter implements IRouter {

  private final IReportProcessor processor;

  public ReportRouter(IReportProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerGetCommunityStats(router);

    return router;
  }

  private void registerGetCommunityStats(Router router) {
    Route getCommunityStatsRoute = router.get("/stats");
    getCommunityStatsRoute.handler(this::handleGetCommunityStatsRoute);
  }

  private void handleGetCommunityStatsRoute(RoutingContext ctx) {
    GetCommunityStatsResponse communityStatsResponse = processor.getCommunityStats();

    end(ctx.response(), 200, JsonObject.mapFrom(communityStatsResponse).toString());
  }
}
