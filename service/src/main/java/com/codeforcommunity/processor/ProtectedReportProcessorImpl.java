package com.codeforcommunity.processor;

import com.codeforcommunity.api.IProtectedReportProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.report.AdoptedSite;
import com.codeforcommunity.dto.report.GetAdoptionReportResponse;
import com.codeforcommunity.dto.report.GetStewardshipReportResponse;
import com.codeforcommunity.dto.report.Stewardship;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AuthException;
import org.jooq.DSLContext;
import java.util.List;

import static org.jooq.generated.Tables.ADOPTED_SITES;
import static org.jooq.generated.Tables.NEIGHBORHOODS;
import static org.jooq.generated.Tables.SITES;
import static org.jooq.generated.Tables.STEWARDSHIP;
import static org.jooq.generated.tables.Users.USERS;
import static org.jooq.impl.DSL.concat;
import static org.jooq.impl.DSL.val;
import static org.jooq.impl.DSL.count;

public class ProtectedReportProcessorImpl implements IProtectedReportProcessor {

    private final DSLContext db;

    public ProtectedReportProcessorImpl(DSLContext db) {
        this.db = db;
    }

    /**
     * Throws an exception if the user is not an admin or super admin.
     *
     * @param level the privilege level of the user calling the route
     */
    void isAdminCheck(PrivilegeLevel level) {
        if (!(level.equals(PrivilegeLevel.ADMIN) || level.equals(PrivilegeLevel.SUPER_ADMIN))) {
            throw new AuthException("User does not have the required privilege level.");
        }
    }

    @Override
    public GetAdoptionReportResponse getAdoptionReport(JWTData userData) {
        isAdminCheck(userData.getPrivilegeLevel());

        List<AdoptedSite> adoptedSites = db.select(SITES.ID, SITES.ADDRESS, concat(USERS.FIRST_NAME, val(" "), USERS.LAST_NAME),
                        USERS.EMAIL, ADOPTED_SITES.DATE_ADOPTED, count(STEWARDSHIP.ID), NEIGHBORHOODS.NEIGHBORHOOD_NAME)
                .from(ADOPTED_SITES)
                .leftJoin(SITES)
                .on(ADOPTED_SITES.SITE_ID.eq(SITES.ID))
                .leftJoin(USERS)
                .on(ADOPTED_SITES.USER_ID.eq(USERS.ID))
                .leftJoin(STEWARDSHIP)
                .on(ADOPTED_SITES.SITE_ID.eq(STEWARDSHIP.SITE_ID))
                .leftJoin(NEIGHBORHOODS)
                .on(SITES.NEIGHBORHOOD_ID.eq(NEIGHBORHOODS.ID))
                .groupBy(SITES.ID, SITES.ADDRESS, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.EMAIL, ADOPTED_SITES.DATE_ADOPTED,
                        NEIGHBORHOODS.NEIGHBORHOOD_NAME)
                .orderBy(USERS.FIRST_NAME, USERS.LAST_NAME, ADOPTED_SITES.DATE_ADOPTED, SITES.ID)
                .fetchInto(AdoptedSite.class);

        return new GetAdoptionReportResponse(adoptedSites);
    }

    @Override
    public GetStewardshipReportResponse getStewardshipReport(JWTData userData) {
        isAdminCheck(userData.getPrivilegeLevel());

        List<Stewardship> stewardships = db.select(SITES.ID, SITES.ADDRESS, concat(USERS.FIRST_NAME, val(" "), USERS.LAST_NAME),
                        USERS.EMAIL, STEWARDSHIP.PERFORMED_ON, STEWARDSHIP.WATERED, STEWARDSHIP.MULCHED, STEWARDSHIP.CLEANED,
                        STEWARDSHIP.WEEDED, NEIGHBORHOODS.NEIGHBORHOOD_NAME)
                .from(STEWARDSHIP)
                .leftJoin(SITES)
                .on(STEWARDSHIP.SITE_ID.eq(SITES.ID))
                .leftJoin(USERS)
                .on(USERS.ID.eq(STEWARDSHIP.USER_ID))
                .leftJoin(NEIGHBORHOODS)
                .on(SITES.NEIGHBORHOOD_ID.eq(NEIGHBORHOODS.ID))
                .orderBy(USERS.FIRST_NAME, USERS.LAST_NAME, STEWARDSHIP.PERFORMED_ON, SITES.ID)
                .fetchInto(Stewardship.class);

        return new GetStewardshipReportResponse(stewardships);
    }
}
