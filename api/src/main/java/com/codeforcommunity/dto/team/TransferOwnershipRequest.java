package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class TransferOwnershipRequest extends ApiDto {
  private Integer newLeaderUserId;

  public TransferOwnershipRequest(Integer newLeaderId) {
    this.newLeaderUserId = newLeaderId;
  }

  private TransferOwnershipRequest() {}

  public Integer getNewLeaderId() {
    return newLeaderUserId;
  }

  public void setNewLeaderId(Integer newLeaderId) {
    this.newLeaderUserId = newLeaderId;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "transfer_ownership_request.";
    List<String> fields = new ArrayList<>();
    if (newLeaderUserId == null) {
      fields.add(fieldName + "newLeaderId");
    }
    return fields;
  }
}
