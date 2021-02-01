package com.codeforcommunity.rest.subrouter;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.ITeamsProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.team.AddGoalRequest;
import com.codeforcommunity.dto.team.CreateTeamRequest;
import com.codeforcommunity.dto.team.DeleteGoalRequest;
import com.codeforcommunity.dto.team.GetTeamRequest;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class TeamsRouter implements IRouter {

  private final ITeamsProcessor processor;

  public TeamsRouter(ITeamsProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);
    registerCreateTeam(router);
    registerGetTeam(router);
    registerAddGoal(router);
    registerDeleteGoal(router);
    registerDisbandTeam(router);

    return router;
  }

  // Create a Team
  private void registerCreateTeam(Router router) {
    Route createTeamRoute = router.post("/create");
    createTeamRoute.handler(this::handleCreateTeamRoute);
  }

  private void handleCreateTeamRoute(RoutingContext routingContext) {
    JWTData userData = routingContext.get("jwt_data");
    CreateTeamRequest createTeamRequest =
        RestFunctions.getJsonBodyAsClass(routingContext, CreateTeamRequest.class);
    processor.createTeam(userData, createTeamRequest);
    end(routingContext.response(), 200);
  }

  // Get a Team
  private void registerGetTeam(Router router) {
    Route getTeamRoute = router.get("/:team_id");
    getTeamRoute.handler(this::handleGetTeamRoute);
  }

  private void handleGetTeamRoute(RoutingContext routingContext) {
    JWTData userData = routingContext.get("jwt_data");
    GetTeamRequest getTeamRequest =
        RestFunctions.getJsonBodyAsClass(routingContext, GetTeamRequest.class);
    processor.getTeam(userData, getTeamRequest);
    end(routingContext.response(), 200);
  }

  // Add a Goal
  private void registerAddGoal(Router router) {
    Route addGoalRoute = router.post("/:team_id/add_goal");
    addGoalRoute.handler(this::handleAddGoal);
  }

  private void handleAddGoal(RoutingContext routingContext) {
    JWTData userData = routingContext.get("jtw_data");
    AddGoalRequest addGoalRequest =
        RestFunctions.getJsonBodyAsClass(routingContext, AddGoalRequest.class);
    processor.addGoal(userData, addGoalRequest);
    end(routingContext.response(), 200);
  }

  // Delete a Goal
  private void registerDeleteGoal(Router router) {
    Route deleteGoalRoute = router.post("/:team_id/delete_goal/goal_id");
    deleteGoalRoute.handler(this::handleDeleteGoal);
  }

  private void handleDeleteGoal(RoutingContext routingContext) {
    JWTData userData = routingContext.get("jwt_data");
    DeleteGoalRequest deleteGoalRequest =
        RestFunctions.getJsonBodyAsClass(routingContext, DeleteGoalRequest.class);
    processor.deleteGoal(userData, deleteGoalRequest);
    end(routingContext.response(), 200);
  }

  private void registerDisbandTeam(Router router) {
    Route disbandTeamRoute = router.post("/:team_id/disband");
    disbandTeamRoute.handler(this::handleDisbandTeamRoute);
  }

  private void handleDisbandTeamRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");

    processor.disbandTeam(userData, teamId);

    end(ctx.response(), 200);
  }
}
