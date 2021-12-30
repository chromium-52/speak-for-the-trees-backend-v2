package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.api.IProtectedReportProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.report.GetAdoptionReportResponse;
import com.codeforcommunity.dto.report.GetStewardshipReportResponse;
import com.codeforcommunity.rest.IRouter;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static com.codeforcommunity.rest.ApiRouter.end;

public class ProtectedReportRouter implements IRouter {
    private final IProtectedReportProcessor processor;

    public ProtectedReportRouter(IProtectedReportProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Router initializeRouter(Vertx vertx) {
        Router router = Router.router(vertx);

        registerGetAdoptionReport(router);
        registerGetStewardshipReport(router);

        return router;
    }

    private void registerGetAdoptionReport(Router router) {
        Route getAdoptionReportRoute = router.get("/adoption");
        getAdoptionReportRoute.handler(this::handleGetAdoptionReportRoute);
    }

    private void handleGetAdoptionReportRoute(RoutingContext ctx) {
        JWTData userData = ctx.get("jwt_data");

        GetAdoptionReportResponse adoptionReportResponse = processor.getAdoptionReport(userData);

        end(ctx.response(), 200, JsonObject.mapFrom(adoptionReportResponse).toString());
    }

    private void registerGetStewardshipReport(Router router) {
        Route getStewardshipReportRoute = router.get("/stewardship");
        getStewardshipReportRoute.handler(this::handleGetStewardshipReportRoute);
    }

    private void handleGetStewardshipReportRoute(RoutingContext ctx) {
        JWTData userData = ctx.get("jwt_data");

        GetStewardshipReportResponse stewardshipReportResponse = processor.getStewardshipReport(userData);

        end(ctx.response(), 200, JsonObject.mapFrom(stewardshipReportResponse).toString());
    }
}
