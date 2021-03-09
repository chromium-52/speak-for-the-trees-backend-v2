package com.codeforcommunity.dto.imports;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;

import java.util.ArrayList;
import java.util.List;

public class SiteImport extends ApiDto {
    private Integer siteId;

    public SiteImport(Integer siteId) {
        this.siteId = siteId;
    }

    private SiteImport() {}

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId() {
        this.siteId = siteId;
    }

    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        String fieldName = fieldPrefix + "sites.";
        List<String> fields = new ArrayList<>();

        if (siteId == null) {
            fields.add(fieldName + "blockId");
        }

        return fields;
    }
}
