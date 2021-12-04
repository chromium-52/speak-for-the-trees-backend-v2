package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.report.GetAdoptionReportResponse;

public interface IProtectedReportProcessor {

    /**
     * Returns a list of information about adopters and stewardship activities for each currently adopted site,
     * grouped by user and in alphabetical order
     */
    GetAdoptionReportResponse getAdoptionReport(JWTData userData);
}
