package com.codeforcommunity.dto.report;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class GetReportCSVRequest extends ApiDto {
    private Long previousDays;

    public GetReportCSVRequest(Long previousDays) {
        this.previousDays = previousDays;
    }

    private GetReportCSVRequest() {}

    public Long getPreviousDays() {
        return previousDays;
    }

    public void setPreviousDays(Long previousDays) {
        this.previousDays = previousDays;
    }

    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        String fieldName = fieldPrefix + "report_csv_request";
        List<String> fields = new ArrayList<>();

        if (previousDays == null) {
            fields.add(fieldName + "previousDays");
        }

        return fields;
    }
}
