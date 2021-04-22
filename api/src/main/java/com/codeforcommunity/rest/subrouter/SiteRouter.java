package com.codeforcommunity.rest.subrouter;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.ISiteProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.site.FavoriteSitesResponse;
import com.codeforcommunity.dto.site.RecordStewardshipRequest;
import com.codeforcommunity.dto.site.StewardshipActivitiesResponse;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class SiteRouter implements IRouter {

  private final ISiteProcessor processor;

  public SiteRouter(ISiteProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerFavoriteSite(router);
    registerUnfavoriteSite(router);
    registerGetFavoriteSitesRoute(router);
    registerRecordStewardship(router);
    registerDeleteStewardship(router);
    registerGetStewardshipActivites(router);

    return router;
  }

  private void registerFavoriteSite(Router router) {
    Route favoriteSiteRoute = router.post("/:site_id/favorite");
    favoriteSiteRoute.handler(this::handleFavoriteSiteRoute);
  }

  private void handleFavoriteSiteRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int siteId = RestFunctions.getRequestParameterAsInt(ctx.request(), "site_id");

    processor.favoriteSite(userData, siteId);

    end(ctx.response(), 200);
  }

  private void registerUnfavoriteSite(Router router) {
    Route unfavoriteSiteRoute = router.post("/:site_id/unfavorite");
    unfavoriteSiteRoute.handler(this::handleUnfavoriteSiteRoute);
  }

  private void handleUnfavoriteSiteRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int siteId = RestFunctions.getRequestParameterAsInt(ctx.request(), "site_id");

    processor.unfavoriteSite(userData, siteId);

    end(ctx.response(), 200);
  }

  private void registerGetFavoriteSitesRoute(Router router) {
    Route getFavoriteSitesRoute = router.get("/favorites");
    getFavoriteSitesRoute.handler(this::handleGetFavoriteSitesRoute);
  }

  private void handleGetFavoriteSitesRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    FavoriteSitesResponse favoriteSitesResponse = processor.getFavoriteSites(userData);

    end(ctx.response(), 200, JsonObject.mapFrom(favoriteSitesResponse).toString());
  }

  private void registerRecordStewardship(Router router) {
    Route recordStewardshipRoute = router.post("/:site_id/record_stewardship");
    recordStewardshipRoute.handler(this::handleRecordStewardshipRoute);
  }

  private void handleRecordStewardshipRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int siteId = RestFunctions.getRequestParameterAsInt(ctx.request(), "site_id");

    RecordStewardshipRequest recordStewardshipRequest =
        RestFunctions.getJsonBodyAsClass(ctx, RecordStewardshipRequest.class);

    processor.recordStewardship(userData, siteId, recordStewardshipRequest);

    end(ctx.response(), 200);
  }

  private void registerDeleteStewardship(Router router) {
    Route deleteStewardshipRoute = router.post("/delete_stewardship/:activity_id");
    deleteStewardshipRoute.handler(this::handleDeleteStewardshipRoute);
  }

  private void handleDeleteStewardshipRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int activityId = RestFunctions.getRequestParameterAsInt(ctx.request(), "activity_id");

    processor.deleteStewardship(userData, activityId);

    end(ctx.response(), 200);
  }

  private void registerGetStewardshipActivites(Router router) {
    Route getStewardshipActivities = router.get("/:site_id/stewardship_activities");
    getStewardshipActivities.handler(this::handleGetStewardshipActivites);
  }

  private void handleGetStewardshipActivites(RoutingContext ctx) {
    int siteId = RestFunctions.getRequestParameterAsInt(ctx.request(), "site_id");

    StewardshipActivitiesResponse stewardshipActivitiesResponse =
        processor.getStewardshipActivities(siteId);

    end(ctx.response(), 200, JsonObject.mapFrom(stewardshipActivitiesResponse).toString());
  }
}
