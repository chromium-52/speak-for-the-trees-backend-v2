package com.codeforcommunity.dto.report;

import java.util.List;

public class GetAdoptionReportResponse {
    public final List<AdoptedSite> adoptedSites;

    public GetAdoptionReportResponse(List<AdoptedSite> adoptedSites) {
        this.adoptedSites = adoptedSites;
    }

    public List<AdoptedSite> getAdoptedSites() {
        return adoptedSites;
    }
}
