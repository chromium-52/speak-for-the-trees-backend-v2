package com.codeforcommunity.dto.report;

import java.util.List;

public class GetAdoptionReportResponse {
    public final List<AdoptedSite> adoptionReport;

    public GetAdoptionReportResponse(List<AdoptedSite> adoptionReport) {
        this.adoptionReport = adoptionReport;
    }

    public List<AdoptedSite> getAdoptionReport() {
        return adoptionReport;
    }
}
