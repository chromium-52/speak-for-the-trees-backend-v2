package com.codeforcommunity.dto.report;

import java.util.List;

public class GetStewardshipReportResponse {
    public final List<Stewardship> stewardshipReport;

    public GetStewardshipReportResponse(List<Stewardship> stewardshipReport) {
        this.stewardshipReport = stewardshipReport;
    }

    public List<Stewardship> getStewardshipReport() {
        return stewardshipReport;
    }
}
