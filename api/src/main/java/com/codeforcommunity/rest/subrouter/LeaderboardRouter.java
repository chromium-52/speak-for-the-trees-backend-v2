package com.codeforcommunity.rest.subrouter;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.ILeaderboardProcessor;
import com.codeforcommunity.dto.leaderboard.GetLeaderboardRequest;
import com.codeforcommunity.dto.leaderboard.GetLeaderboardResponse;
import com.codeforcommunity.dto.leaderboard.LeaderboardEntry;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class LeaderboardRouter implements IRouter {

  private final ILeaderboardProcessor processor;

  public LeaderboardRouter(ILeaderboardProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerGetUsersLeaderboard(router);
    registerGetTeamsLeaderboard(router);

    return router;
  }

  private void registerGetUsersLeaderboard(Router router) {
    Route getUsersLeaderboard = router.get("/users");
    getUsersLeaderboard.handler(this::handleGetUsersLeaderboardRoute);
  }

  private void registerGetTeamsLeaderboard(Router router) {
    Route getTeamsLeaderboard = router.get("/teams");
    getTeamsLeaderboard.handler(this::handleGetTeamsLeaderboardRoute);
  }

  private void handleGetUsersLeaderboardRoute(RoutingContext ctx) {
    GetLeaderboardRequest getLeaderboardRequest =
        RestFunctions.getJsonBodyAsClass(ctx, GetLeaderboardRequest.class);

    GetLeaderboardResponse response = processor.getUsersLeaderboard(getLeaderboardRequest);

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleGetTeamsLeaderboardRoute(RoutingContext ctx) {
    GetLeaderboardRequest getLeaderboardRequest =
        RestFunctions.getJsonBodyAsClass(ctx, GetLeaderboardRequest.class);

    GetLeaderboardResponse response = processor.getTeamsLeaderboard(getLeaderboardRequest);

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }
}
