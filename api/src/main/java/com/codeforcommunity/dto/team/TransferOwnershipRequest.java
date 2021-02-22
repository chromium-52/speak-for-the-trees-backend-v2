package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class TransferOwnershipRequest extends ApiDto {
  private Integer teamId;
  private Integer newLeaderId;

  public TransferOwnershipRequest(Integer teamId, Integer newLeaderId) {
    this.teamId = teamId;
    this.newLeaderId = newLeaderId;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "transfer_ownership_request.";
    List<String> fields = new ArrayList<>();
    if (teamId == null) {
      fields.add(fieldName + "team_id");
    }
    if (newLeaderId == null) {
      fields.add(fieldName + "newLeaderId");
    }
    return fields;
  }

  public Integer getTeamId() {
    return teamId;
  }

  public void setTeamId(Integer teamId) {
    this.teamId = teamId;
  }

  public Integer getNewLeaderId() {
    return newLeaderId;
  }

  public void setNewLeaderId(Integer newLeaderId) {
    this.newLeaderId = newLeaderId;
  }
}
