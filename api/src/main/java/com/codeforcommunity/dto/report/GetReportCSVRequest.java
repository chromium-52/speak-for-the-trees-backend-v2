package com.codeforcommunity.dto.report;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class GetReportCSVRequest extends ApiDto {
    private Integer previousDays;

    public GetReportCSVRequest(Integer previousDays) {
        this.previousDays = previousDays;
    }

    private GetReportCSVRequest() {}

    public Integer getPreviousDays() {
        return previousDays;
    }

    public void setPreviousDays(Integer previousDays) {
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
