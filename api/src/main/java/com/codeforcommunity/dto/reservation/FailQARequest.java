package com.codeforcommunity.dto.reservation;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class FailQARequest extends ApiDto {
  private Integer blockID;

  public FailQARequest(Integer blockID, Integer teamID) {
    this.blockID = blockID;
  }

  private FailQARequest() {}

  public Integer getBlockID() {
    return blockID;
  }

  public void setBlockID(Integer blockID) {
    this.blockID = blockID;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "fail_qa_request";
    List<String> fields = new ArrayList<>();

    if (blockID == null) {
      fields.add(fieldName + "blockID");
    }

    return fields;
  }
}
