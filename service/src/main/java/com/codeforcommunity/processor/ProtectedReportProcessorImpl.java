package com.codeforcommunity.processor;

import com.codeforcommunity.api.IProtectedReportProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.report.AdoptedSite;
import com.codeforcommunity.dto.report.GetAdoptionReportResponse;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AuthException;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.SelectSeekStep4;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.generated.Tables.ADOPTED_SITES;
import static org.jooq.generated.Tables.NEIGHBORHOODS;
import static org.jooq.generated.Tables.SITES;
import static org.jooq.generated.Tables.STEWARDSHIP;
import static org.jooq.generated.tables.Users.USERS;
import static org.jooq.impl.DSL.*;

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

//        List<AdoptedSite> adoptedSites = db.select(ADOPTED_SITES.DATE_ADOPTED)
//                .from(ADOPTED_SITES)
//                .fetchInto(AdoptedSite.class);

        // SelectSeekStep4<Record7<Integer, String, String, String, Date, Integer, String>, String, String, Date, Integer> subquery
        List<AdoptedSite> adoptedSites = db.select(SITES.ID, SITES.ADDRESS, concat(USERS.FIRST_NAME, val(" "), USERS.LAST_NAME), USERS.EMAIL, ADOPTED_SITES.DATE_ADOPTED, count(STEWARDSHIP.ID), NEIGHBORHOODS.NEIGHBORHOOD_NAME)
                .from(ADOPTED_SITES)
                .leftJoin(SITES)
                .on(ADOPTED_SITES.SITE_ID.eq(SITES.ID))
                .leftJoin(USERS)
                .on(ADOPTED_SITES.USER_ID.eq(USERS.ID))
                .leftJoin(STEWARDSHIP)
                .on(ADOPTED_SITES.SITE_ID.eq(STEWARDSHIP.SITE_ID))
                .leftJoin(NEIGHBORHOODS)
                .on(SITES.NEIGHBORHOOD_ID.eq(NEIGHBORHOODS.ID))
                .groupBy(SITES.ID, SITES.ADDRESS, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.EMAIL, ADOPTED_SITES.DATE_ADOPTED, NEIGHBORHOODS.NEIGHBORHOOD_NAME)
                .orderBy(USERS.FIRST_NAME, USERS.LAST_NAME, ADOPTED_SITES.DATE_ADOPTED, SITES.ID)
                .fetchInto(AdoptedSite.class);

//        List<AdoptedSite> adoptedSites = db.select(subquery.field(1).as("name"))
//                .select(subquery.field(6).as("number_of_activities"), STEWARDSHIP.ID, count())
//                .fetchInto(AdoptedSite.class);

//        adoptedSites = new ArrayList<>();
//        adoptedSites.add(new AdoptedSite(1, "addr", "name", "email", new Timestamp(1), 2, "neighborhood"));

        /*
        SELECT s.id, s.address, first_name || ' ' || last_name as name, email, date_adopted, count(s2.id) as number_of_activities, n.neighborhood_name
FROM adopted_sites
         LEFT JOIN sites s ON adopted_sites.site_id = s.id
         LEFT JOIN users ON adopted_sites.user_id = users.id
         LEFT JOIN stewardship s2 ON adopted_sites.site_id = s2.site_id
         LEFT JOIN neighborhoods n ON s.neighborhood_id = n.id
GROUP BY s.id, s.address, users.first_name, users.last_name, users.email, adopted_sites.date_adopted, n.neighborhood_name
ORDER BY users.first_name, users.last_name, date_adopted, s.id;
         */

        return new GetAdoptionReportResponse(adoptedSites);
    }
}
