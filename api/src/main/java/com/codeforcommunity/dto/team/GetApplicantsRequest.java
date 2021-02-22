package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class GetApplicantsRequest extends ApiDto {
  private Integer teamId;

  public GetApplicantsRequest(Integer teamId) {
    this.teamId = teamId;
  }

  public GetApplicantsRequest() {}

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "get_applicants_request.";
    List<String> fields = new ArrayList<>();

    if (teamId == null) {
      fields.add(fieldName + "teamId");
    }
    return fields;
  }

  public Integer getTeamId() {
    return teamId;
  }

  public void setTeamId(Integer teamId) {
    this.teamId = teamId;
  }
}
