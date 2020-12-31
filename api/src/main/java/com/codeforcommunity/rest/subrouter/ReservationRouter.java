package com.codeforcommunity.rest.subrouter;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.IReservationProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.reservation.*;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ReservationRouter implements IRouter {

  private final IReservationProcessor processor;

  public ReservationRouter(IReservationProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerMakeReservation(router);
    registerCompleteReservation(router);
    registerReleaseReservation(router);
    registerUncompleteReservation(router);
    registerMarkForQA(router);
    registerPassQA(router);
    registerFailQA(router);

    return router;
  }

  private void registerMakeReservation(Router router) {
    Route makeReservationRoute = router.post("/reserve");
    makeReservationRoute.handler(this::handleMakeReservationRoute);
  }

  private void registerCompleteReservation(Router router) {
    Route completeReservationRoute = router.post("/complete");
    completeReservationRoute.handler(this::handleCompleteReservationRoute);
  }

  private void registerReleaseReservation(Router router) {
    Route releaseReservationRoute = router.post("/release");
    releaseReservationRoute.handler(this::handleReleaseReservationRoute);
  }

  private void registerUncompleteReservation(Router router) {
    Route uncompleteReservationRoute = router.post("/uncomplete");
    uncompleteReservationRoute.handler(this::handleUncompleteReservationRoute);
  }

  private void registerMarkForQA(Router router) {
    Route markForQARoute = router.post("/qa");
    markForQARoute.handler(this::handleMarkForQARoute);
  }

  private void registerPassQA(Router router) {
    Route passQARoute = router.post("/pass_qa");
    passQARoute.handler(this::handlePassQARoute);
  }

  private void registerFailQA(Router router) {
    Route failQARoute = router.post("/fail_qa");
    failQARoute.handler(this::handleFailQARoute);
  }

  private void handleMakeReservationRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    MakeReservationRequest makeReservationRequest =
        RestFunctions.getJsonBodyAsClass(ctx, MakeReservationRequest.class);

    processor.makeReservation(userData, makeReservationRequest);

    end(ctx.response(), 200);
  }

  private void handleCompleteReservationRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    CompleteReservationRequest completeReservationRequest =
        RestFunctions.getJsonBodyAsClass(ctx, CompleteReservationRequest.class);

    processor.completeReservation(userData, completeReservationRequest);

    end(ctx.response(), 200);
  }

  private void handleReleaseReservationRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    BlockIDRequest releaseReservationRequest =
        RestFunctions.getJsonBodyAsClass(ctx, BlockIDRequest.class);

    processor.releaseReservation(userData, releaseReservationRequest);

    end(ctx.response(), 200);
  }

  private void handleUncompleteReservationRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    BlockIDRequest uncompleteReservationRequest =
        RestFunctions.getJsonBodyAsClass(ctx, BlockIDRequest.class);

    processor.uncompleteReservation(userData, uncompleteReservationRequest);

    end(ctx.response(), 200);
  }

  private void handleMarkForQARoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    BlockIDRequest markForQARequest =
        RestFunctions.getJsonBodyAsClass(ctx, BlockIDRequest.class);

    processor.markForQA(userData, markForQARequest);

    end(ctx.response(), 200);
  }

  private void handlePassQARoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    BlockIDRequest passQARequest = RestFunctions.getJsonBodyAsClass(ctx, BlockIDRequest.class);

    processor.passQA(userData, passQARequest);

    end(ctx.response(), 200);
  }

  private void handleFailQARoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    BlockIDRequest failQARequest = RestFunctions.getJsonBodyAsClass(ctx, BlockIDRequest.class);

    processor.failQA(userData, failQARequest);

    end(ctx.response(), 200);
  }
}
