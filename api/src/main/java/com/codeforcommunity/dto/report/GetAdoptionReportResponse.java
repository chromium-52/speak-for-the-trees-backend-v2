package com.codeforcommunity.dto.report;

import java.util.List;

public class GetAdoptionReportResponse {
    public final List<AdoptedSite> adoption_report;

    public GetAdoptionReportResponse(List<AdoptedSite> adoptedSites) {
        this.adoption_report = adoptedSites;
    }

    public List<AdoptedSite> getAdoptedSites() {
        return adoption_report;
    }
}
