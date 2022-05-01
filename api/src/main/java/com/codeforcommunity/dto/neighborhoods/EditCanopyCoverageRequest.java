package com.codeforcommunity.dto.neighborhoods;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class EditCanopyCoverageRequest extends ApiDto {
  private Double canopyCoverage;

  public EditCanopyCoverageRequest(Double canopyCoverage) {
    this.canopyCoverage = canopyCoverage;
  }

  private EditCanopyCoverageRequest() {}

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "edit_canopy";
    List<String> fields = new ArrayList<>();

    if (this.canopyCoverage == null) {
      fields.add(fieldName + "canopyCoverage");
    }

    return fields;
  }

  public Double getCanopyCoverage() {
    return this.canopyCoverage;
  }
}
