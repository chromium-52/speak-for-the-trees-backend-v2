package com.codeforcommunity.processor;

import com.codeforcommunity.api.IProtectedReportProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.report.AdoptedSite;
import com.codeforcommunity.dto.report.GetAdoptionReportResponse;
import com.codeforcommunity.dto.report.GetReportCSVRequest;
import com.codeforcommunity.dto.report.GetStewardshipReportResponse;
import com.codeforcommunity.dto.report.Stewardship;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AuthException;
import org.jooq.DSLContext;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
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
    private static final Integer MISSING_PREVIOUS_DAYS = -1;

    private final DSLContext db;

    public ProtectedReportProcessorImpl(DSLContext db) {
        this.db = db;
    }

    /**
     * Throws an exception if the user is not an admin or super admin.
     *
     * @param level the privilege level of the user calling the route
     */
    private void isAdminCheck(PrivilegeLevel level) {
        if (!(level.equals(PrivilegeLevel.ADMIN) || level.equals(PrivilegeLevel.SUPER_ADMIN))) {
            throw new AuthException("User does not have the required privilege level.");
        }
    }

    /**
     * Query sites that have been adopted at or after the given time.
     *
     * @param startMilli sites adopted after this time, in milliseconds, are included in the returned list
     * @return list of sites that have been adopted at or after the given time
     */
    private List<AdoptedSite> queryAdoptedSites(long startMilli) {
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
                .where(ADOPTED_SITES.DATE_ADOPTED.ge(new Date(startMilli)))
                .or(startMilli == 0)
                .groupBy(SITES.ID, SITES.ADDRESS, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.EMAIL, ADOPTED_SITES.DATE_ADOPTED,
                        NEIGHBORHOODS.NEIGHBORHOOD_NAME)
                .orderBy(USERS.FIRST_NAME, USERS.LAST_NAME, ADOPTED_SITES.DATE_ADOPTED, SITES.ID)
                .fetchInto(AdoptedSite.class);

        return adoptedSites;
    }

    @Override
    public GetAdoptionReportResponse getAdoptionReport(JWTData userData) {
        isAdminCheck(userData.getPrivilegeLevel());

        // get all adopted sites
        List<AdoptedSite> adoptedSites = queryAdoptedSites(0);

        return new GetAdoptionReportResponse(adoptedSites);
    }

    @Override
    public String getAdoptionReportCSV(JWTData userData, GetReportCSVRequest getReportCSVRequest) {
        isAdminCheck(userData.getPrivilegeLevel());

        Integer previousDays = getReportCSVRequest.getPreviousDays();
        // if previousDays parameter is not given, all adopted sites are returned
        LocalDate startLocalDate = previousDays == MISSING_PREVIOUS_DAYS
                ? LocalDate.ofEpochDay(0) : LocalDate.now().minusDays(previousDays);
        long startMilli = startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        List<AdoptedSite> adoptedSites = queryAdoptedSites(startMilli);

        StringBuilder builder = new StringBuilder();
        builder.append("Site ID, Address, Name, Email, Date Adopted, Activity Count, Neighborhood\n");
        for (AdoptedSite site : adoptedSites) {
            builder.append(site.getSiteId() + ", " + site.getAddress() + ", " + site.getName() + ", " + site.getEmail() +
                    ", " + site.getDateAdopted() + ", " + site.getActivityCount() + ", " + site.getNeighborhood() + "\n");
        }

        return builder.toString();
    }

    /**
     * Query stewardship activities that have been performed at or after the given date.
     *
     * @param startDate stewardship activities performed after this date are included in the returned list
     * @return list of stewardship activities that have been performed at or after the given date
     */
    private List<Stewardship> queryStewardships(Date startDate) {
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
                .where(STEWARDSHIP.PERFORMED_ON.ge(startDate))
                .orderBy(USERS.FIRST_NAME, USERS.LAST_NAME, STEWARDSHIP.PERFORMED_ON, SITES.ID)
                .fetchInto(Stewardship.class);

        return stewardships;
    }

    @Override
    public GetStewardshipReportResponse getStewardshipReport(JWTData userData) {
        isAdminCheck(userData.getPrivilegeLevel());

        // get all stewardships
        List<Stewardship> stewardships = queryStewardships(new Date(0));

        return new GetStewardshipReportResponse(stewardships);
    }

    @Override
    public String getStewardshipReportCSV(JWTData userData, GetReportCSVRequest getReportCSVRequest) {
        isAdminCheck(userData.getPrivilegeLevel());

        Integer previousDays = getReportCSVRequest.getPreviousDays();
        // if previousDays parameter is not given, all adopted sites are returned
        LocalDate startLocalDate = previousDays == MISSING_PREVIOUS_DAYS
                ? LocalDate.ofEpochDay(0) : LocalDate.now().minusDays(previousDays);
        Date startDate = new Date(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

        List<Stewardship> stewardships = queryStewardships(startDate);

        StringBuilder builder = new StringBuilder();
        builder.append("Site ID, Address, Name, Email, Date Performed, Watered, Mulched, Cleaned, Weeded, Neighborhood\n");
        for (Stewardship site : stewardships) {
            builder.append(site.getSiteId() + ", " + site.getAddress() + ", " + site.getName() + ", " + site.getEmail() +
                    ", " + site.getDatePerformed() + ", " + site.getWatered() + ", " + site.getMulched() + ", " +
                    site.getCleaned() + ", " + site.getWeeded() + ", " + site.getNeighborhood() + "\n");
        }

        return builder.toString();
    }
}
