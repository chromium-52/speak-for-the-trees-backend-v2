package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.api.IProtectedReportProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.report.GetReportCSVRequest;
import com.codeforcommunity.dto.report.GetAdoptionReportResponse;
import com.codeforcommunity.dto.report.GetStewardshipReportResponse;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.codeforcommunity.rest.ApiRouter.end;

public class ProtectedReportRouter implements IRouter {
    private static final String PREVIOUS_DAYS_QUERY_PARAM_NAME = "previousDays";
    // default is to include all adopted sites or stewardship activities
    private static final Integer DEFAULT_PREVIOUS_DAYS = -1;

    private final IProtectedReportProcessor processor;

    public ProtectedReportRouter(IProtectedReportProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Router initializeRouter(Vertx vertx) {
        Router router = Router.router(vertx);

        registerGetAdoptionReport(router);
        registerGetAdoptionReportCSV(router);
        registerGetStewardshipReport(router);
        registerGetStewardshipReportCSV(router);

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

    private void registerGetAdoptionReportCSV(Router router) {
        Route getAdoptionReportCSVRoute = router.get("/csv/adoption");
        getAdoptionReportCSVRoute.handler(this::handleGetAdoptionReportCSVRoute);
    }

    private void handleGetAdoptionReportCSVRoute(RoutingContext ctx) {
        JWTData userData = ctx.get("jwt_data");

        Integer previousDays = getValidatedPreviousDaysParam(ctx);

        GetReportCSVRequest getAdoptionReportCSVRequest = new GetReportCSVRequest(previousDays);

        String adoptionReportCSVResponse = processor.getAdoptionReportCSV(userData, getAdoptionReportCSVRequest);

        end(ctx.response(), 200, adoptionReportCSVResponse);
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

    private void registerGetStewardshipReportCSV(Router router) {
        Route getStewardshipReportCSVRoute = router.get("/csv/stewardship");
        getStewardshipReportCSVRoute.handler(this::handleGetStewardshipReportCSVRoute);
    }

    private void handleGetStewardshipReportCSVRoute(RoutingContext ctx) {
        JWTData userData = ctx.get("jwt_data");

        Integer previousDays = getValidatedPreviousDaysParam(ctx);

        GetReportCSVRequest getStewardshipReportCSVRequest = new GetReportCSVRequest(previousDays);

        String stewardshipReportCSVResponse = processor.getStewardshipReportCSV(userData, getStewardshipReportCSVRequest);

        end(ctx.response(), 200, stewardshipReportCSVResponse);
    }

    /**
     * Return the validated previousDays query parameter from the request URL. The parameter must be an integer greater
     * than or equal to 0. If the parameter is not given, a value of DEFAULT_PREVIOUS_DAYS is returned.
     *
     * @param ctx routing context
     * @return validated previousDays parameter
     */
    private Integer getValidatedPreviousDaysParam(RoutingContext ctx) {
        Optional<Integer> maybePreviousDays =
                RestFunctions.getOptionalQueryParam(ctx, PREVIOUS_DAYS_QUERY_PARAM_NAME, Integer::parseInt);
        Integer previousDays = maybePreviousDays.orElse(null);

        // validate user input
        if (previousDays == null || System.currentTimeMillis() - TimeUnit.DAYS.toMillis(previousDays) < 0) {
            previousDays = DEFAULT_PREVIOUS_DAYS;
        } else if (previousDays < 0) {
            previousDays = 0;
        }

        return previousDays;
    }
}