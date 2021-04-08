package com.codeforcommunity.rest.subrouter;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.ITeamsProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.team.AddGoalRequest;
import com.codeforcommunity.dto.team.CreateTeamRequest;
import com.codeforcommunity.dto.team.InviteUsersRequest;
import com.codeforcommunity.dto.team.TeamDataResponse;
import com.codeforcommunity.dto.team.TeamGoalDataResponse;
import com.codeforcommunity.dto.team.TransferOwnershipRequest;
import com.codeforcommunity.dto.team.UsersTeamDataResponse;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.List;

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
    registerApplyToTeam(router);
    registerInviteUser(router);
    registerApproveUser(router);
    registerRejectUserRoute(router);
    registerGetApplicants(router);
    registerKickUserRoute(router);
    registerLeaveTeamRoute(router);
    registerTransferTeamOwnership(router);
    registerGetTeamsRoute(router);

    return router;
  }

  // Create a Team
  private void registerCreateTeam(Router router) {
    Route createTeamRoute = router.post("/create");
    createTeamRoute.handler(this::handleCreateTeamRoute);
  }

  private void handleCreateTeamRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    CreateTeamRequest createTeamRequest =
        RestFunctions.getJsonBodyAsClass(ctx, CreateTeamRequest.class);
    processor.createTeam(userData, createTeamRequest);
    end(ctx.response(), 200);
  }

  // Get a Team
  private void registerGetTeam(Router router) {
    Route getTeamRoute = router.get("/:team_id");
    getTeamRoute.handler(this::handleGetTeamRoute);
  }

  private void handleGetTeamRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");
    TeamGoalDataResponse getTeamResponse = processor.getTeam(userData, teamId);
    end(ctx.response(), 200, JsonObject.mapFrom(getTeamResponse).toString());
  }

  // Add a Goal
  private void registerAddGoal(Router router) {
    Route addGoalRoute = router.post("/:team_id/add_goal");
    addGoalRoute.handler(this::handleAddGoal);
  }

  private void handleAddGoal(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");
    AddGoalRequest addGoalRequest = RestFunctions.getJsonBodyAsClass(ctx, AddGoalRequest.class);
    processor.addGoal(userData, addGoalRequest, teamId);
    end(ctx.response(), 200);
  }

  // Delete a Goal
  private void registerDeleteGoal(Router router) {
    Route deleteGoalRoute = router.post("/:team_id/delete_goal/:goal_id");
    deleteGoalRoute.handler(this::handleDeleteGoal);
  }

  private void handleDeleteGoal(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");
    int goalId = RestFunctions.getRequestParameterAsInt(ctx.request(), "goal_id");
    processor.deleteGoal(userData, teamId, goalId);
    end(ctx.response(), 200);
  }

  private void registerInviteUser(Router router) {
    Route inviteUserRoute = router.post("/:team_id/invite");
    inviteUserRoute.handler(this::handleInviteUser);
  }

  private void handleInviteUser(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");
    InviteUsersRequest inviteUserRequest =
        RestFunctions.getJsonBodyAsClass(ctx, InviteUsersRequest.class);
    processor.inviteUser(userData, inviteUserRequest, teamId);
    end(ctx.response(), 200);
  }

  private void registerGetApplicants(Router router) {
    Route getApplicantsRoute = router.get("/:team_id/applicants");
    getApplicantsRoute.handler(this::handleGetApplicantsRoute);
  }

  private void handleGetApplicantsRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");
    List<UsersTeamDataResponse> response = processor.getApplicants(userData, teamId);
    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void registerApplyToTeam(Router router) {
    Route applyToTeamRoute = router.post("/:team_id/apply");
    applyToTeamRoute.handler(this::handleApplyToTeamRoute);
  }

  private void handleApplyToTeamRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");
    processor.applyToTeam(userData, teamId);
    end(ctx.response(), 200);
  }

  private void registerApproveUser(Router router) {
    Route approveUserRoute = router.post("/:team_id/applicants/:user_id/approve");
    approveUserRoute.handler(this::handleApproveUserRoute);
  }

  private void handleApproveUserRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");
    int memberId = RestFunctions.getRequestParameterAsInt(ctx.request(), "user_id");
    processor.approveUser(userData, teamId, memberId);
    end(ctx.response(), 200);
  }

  private void registerRejectUserRoute(Router router) {
    Route rejectUserRoute = router.post("/:team_id/applicants/:user_id/reject");
    rejectUserRoute.handler(this::handleRejectUserRoute);
  }

  private void handleRejectUserRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");
    int memberId = RestFunctions.getRequestParameterAsInt(ctx.request(), "user_id");
    processor.rejectUser(userData, teamId, memberId);
    end(ctx.response(), 200);
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

  private void registerKickUserRoute(Router router) {
    Route kickUserRoute = router.post("/:team_id/members/:member_id/kick");
    kickUserRoute.handler(this::handleKickUserRoute);
  }

  private void handleKickUserRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");
    int memberId = RestFunctions.getRequestParameterAsInt(ctx.request(), "member_id");
    processor.kickUser(userData, teamId, memberId);
    end(ctx.response(), 200);
  }

  private void registerLeaveTeamRoute(Router router) {
    Route leaveTeamRoute = router.post("/:team_id/leave");
    leaveTeamRoute.handler(this::handleLeaveTeamRoute);
  }

  private void handleLeaveTeamRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");
    processor.leaveTeam(userData, teamId);
    end(ctx.response(), 200);
  }

  private void registerTransferTeamOwnership(Router router) {
    Route transferOwnershipRoute = router.post("/:team_id/transfer_ownership");
    transferOwnershipRoute.handler(this::handleTransferOwnershipRoute);
  }

  private void handleTransferOwnershipRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int teamId = RestFunctions.getRequestParameterAsInt(ctx.request(), "team_id");
    TransferOwnershipRequest transferOwnershipRequest =
        RestFunctions.getJsonBodyAsClass(ctx, TransferOwnershipRequest.class);
    processor.transferOwnership(userData, transferOwnershipRequest, teamId);
    end(ctx.response(), 200);
  }

  private void registerGetTeamsRoute(Router router) {
    Route getTeamsRoute = router.get("/teams");
    getTeamsRoute.handler(this::handleGetTeamsRoute);
  }

  private void handleGetTeamsRoute(RoutingContext ctx) {
    List<TeamDataResponse> getTeamsResponse = processor.getTeams();
    end(ctx.response(), 200, JsonObject.mapFrom(getTeamsResponse).toString());
  }

}
