package com.codeforcommunity.rest.subrouter;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.IMapProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.map.BlockGeoResponse;
import com.codeforcommunity.dto.map.NeighborhoodGeoResponse;
import com.codeforcommunity.dto.map.SiteGeoResponse;
import com.codeforcommunity.rest.IRouter;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MapRouter implements IRouter {

  private final IMapProcessor processor;

  public MapRouter(IMapProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerGetBlocks(router);
    registerGetNeighborhoods(router);
    registerGetSites(router);

    return router;
  }

  private void registerGetBlocks(Router router) {
    Route getBlocksRoute = router.get("/blocks");
    getBlocksRoute.handler(this::handleGetBlocks);
  }

  private void registerGetNeighborhoods(Router router) {
    Route getNeighborhoodsRoute = router.get("/neighborhoods");
    getNeighborhoodsRoute.handler(this::handleGetNeighborhoods);
  }

  private void registerGetSites(Router router) {
    Route getSitesRoute = router.get("/sites");
    getSitesRoute.handler(this::handleGetSites);
  }

  private void handleGetBlocks(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    BlockGeoResponse response = processor.getBlockGeoJson();

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleGetNeighborhoods(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    NeighborhoodGeoResponse response = processor.getNeighborhoodGeoJson();

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleGetSites(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    SiteGeoResponse response = processor.getSiteGeoJson();

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }
}
