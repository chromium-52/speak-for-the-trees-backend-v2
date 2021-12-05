package com.codeforcommunity.dto.report;

import java.util.List;

public class GetStewardshipReportResponse {
    public final List<Stewardship> stewardships;

    public GetStewardshipReportResponse(List<Stewardship> stewardships) {
        this.stewardships = stewardships;
    }

    public List<Stewardship> getStewardships() {
        return stewardships;
    }
}
