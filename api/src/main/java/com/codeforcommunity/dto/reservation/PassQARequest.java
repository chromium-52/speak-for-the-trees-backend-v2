package com.codeforcommunity.dto.reservation;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class PassQARequest extends ApiDto {
  private Integer blockID;

  public PassQARequest(Integer blockID, Integer teamID) {
    this.blockID = blockID;
  }

  private PassQARequest() {}

  public Integer getBlockID() {
    return blockID;
  }

  public void setBlockID(Integer blockID) {
    this.blockID = blockID;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "pass_qa__request";
    List<String> fields = new ArrayList<>();

    if (blockID == null) {
      fields.add(fieldName + "blockID");
    }

    return fields;
  }
}
