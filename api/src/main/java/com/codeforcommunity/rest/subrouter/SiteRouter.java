package com.codeforcommunity.rest.subrouter;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.ISiteProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.site.RecordStewardshipRequest;
import com.codeforcommunity.dto.site.UpdateSiteRequest;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
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
    registerRecordStewardship(router);
    registerUpdateSite(router);
    registerDeleteSite(router);

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
}
