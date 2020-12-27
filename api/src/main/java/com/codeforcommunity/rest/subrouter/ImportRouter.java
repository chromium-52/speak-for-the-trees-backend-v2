package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.api.IImportProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.imports.ImportBlocksRequest;
import com.codeforcommunity.dto.imports.ImportNeighborhoodsRequest;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static com.codeforcommunity.rest.ApiRouter.end;

public class ImportRouter implements IRouter {

    private final IImportProcessor processor;

    public ImportRouter(IImportProcessor processor) { this.processor = processor; }

    @Override
    public Router initializeRouter(Vertx vertx) {
        Router router = Router.router(vertx);

        registerImportBlocks(router);
        registerImportNeighborhoods(router);

        return router;
    }

    private void registerImportBlocks(Router router) {
        Route importBlocksRoute = router.post("/blocks");
        importBlocksRoute.handler(this::handleImportBlocksRoute);
    }

    private void registerImportNeighborhoods(Router router) {
        Route importNeighborhoodsRoute = router.post("/neighborhoods");
        importNeighborhoodsRoute.handler(this::handleImportNeighborhoodsRoute);
    }

    private void handleImportBlocksRoute(RoutingContext ctx) {
        JWTData userData = ctx.get("jwt_data");
        ImportBlocksRequest importBlocksRequest =
                RestFunctions.getJsonBodyAsClass(ctx, ImportBlocksRequest.class);

        processor.importBlocks(userData, importBlocksRequest);

        end(ctx.response(), 200);
    }

    private void handleImportNeighborhoodsRoute(RoutingContext ctx) {
        JWTData userData = ctx.get("jwt_data");
        ImportNeighborhoodsRequest importNeighborhoodsRequest =
                RestFunctions.getJsonBodyAsClass(ctx, ImportNeighborhoodsRequest.class);

        processor.importNeighborhoods(userData, importNeighborhoodsRequest);

        end(ctx.response(), 200);
    }
}
