package com.codeforcommunity.rest.subrouter;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.ISiteProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.site.AdoptedSitesResponse;
import com.codeforcommunity.dto.site.RecordStewardshipRequest;
import com.codeforcommunity.dto.site.StewardshipActivitiesResponse;
import com.codeforcommunity.dto.site.UpdateSiteRequest;
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

    registerAdoptSite(router);
    registerUnadoptSite(router);
    registerGetAdoptedSitesRoute(router);
    registerRecordStewardship(router);
    registerUpdateSite(router);
    registerDeleteSite(router);
    registerDeleteStewardship(router);
    registerGetStewardshipActivites(router);

    return router;
  }

  private void registerAdoptSite(Router router) {
    Route adoptSiteRoute = router.post("/:site_id/adopt");
    adoptSiteRoute.handler(this::handleAdoptSiteRoute);
  }

  private void handleAdoptSiteRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int siteId = RestFunctions.getRequestParameterAsInt(ctx.request(), "site_id");

    processor.adoptSite(userData, siteId);

    end(ctx.response(), 200);
  }

  private void registerUnadoptSite(Router router) {
    Route unadoptSiteRoute = router.post("/:site_id/unadopt");
    unadoptSiteRoute.handler(this::handleUnadoptSiteRoute);
  }

  private void handleUnadoptSiteRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int siteId = RestFunctions.getRequestParameterAsInt(ctx.request(), "site_id");

    processor.unadoptSite(userData, siteId);

    end(ctx.response(), 200);
  }

  private void registerGetAdoptedSitesRoute(Router router) {
    Route getAdoptedSitesRoute = router.get("/adopted_sites");
    getAdoptedSitesRoute.handler(this::handleGetAdoptedSitesRoute);
  }

  private void handleGetAdoptedSitesRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    AdoptedSitesResponse adoptedSitesResponse = processor.getAdoptedSites(userData);

    end(ctx.response(), 200, JsonObject.mapFrom(adoptedSitesResponse).toString());
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

  private void registerUpdateSite(Router router) {
    Route updateSiteRoute = router.post("/:site_id/update");
    updateSiteRoute.handler(this::handleUpdateSiteRoute);
  }

  private void handleUpdateSiteRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int siteId = RestFunctions.getRequestParameterAsInt(ctx.request(), "site_id");

    UpdateSiteRequest updateSiteRequest =
        RestFunctions.getJsonBodyAsClass(ctx, UpdateSiteRequest.class);

    processor.updateSite(userData, siteId, updateSiteRequest);

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

  private void registerDeleteSite(Router router) {
    Route deleteSiteRoute = router.post("/:site_id/delete");
    deleteSiteRoute.handler(this::handleDeleteSiteRoute);
  }

  private void handleDeleteSiteRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int siteId = RestFunctions.getRequestParameterAsInt(ctx.request(), "site_id");

    processor.deleteSite(userData, siteId);

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
