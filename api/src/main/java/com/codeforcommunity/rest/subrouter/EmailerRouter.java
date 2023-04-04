package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.api.IEmailerProcessor;
import com.codeforcommunity.api.ILeaderboardProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.emailer.AddTemplateRequest;
import com.codeforcommunity.dto.leaderboard.GetLeaderboardRequest;
import com.codeforcommunity.dto.leaderboard.GetLeaderboardResponse;
import com.codeforcommunity.dto.neighborhoods.EditCanopyCoverageRequest;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;

import java.util.Optional;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static com.codeforcommunity.rest.ApiRouter.end;

public class EmailerRouter implements IRouter {

  private final IEmailerProcessor processor;

  public EmailerRouter(IEmailerProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerAddTemplate(router);

    return router;
  }

  private void registerAddTemplate(Router router) {
    Route addTemplate = router.get("/add_template");
    addTemplate.handler(this::handleAddTemplate);
  }

  private void handleAddTemplate(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    AddTemplateRequest addTemplateRequest =
            RestFunctions.getJsonBodyAsClass(ctx, AddTemplateRequest.class);

    processor.addTemplate(userData, addTemplateRequest);

    end(ctx.response(), 200);
  }
}
