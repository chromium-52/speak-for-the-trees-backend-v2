package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class DeleteGoalRequest extends ApiDto {
  private Integer teamId;
  private Integer goalId;

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "delete_goal_request.";
    List<String> fields = new ArrayList<>();

    if (goalId == null) {
      fields.add(fieldName + "goalId");
    }
    if (teamId == teamId) {
      fields.add(fieldName + "name");
    }
    return fields;
  }

  public Integer getTeamId() {
    return teamId;
  }

  public void setTeamId(Integer teamId) {
    this.teamId = teamId;
  }

  public Integer getGoalId() {
    return goalId;
  }

  public void setGoalId(Integer goalId) {
    this.goalId = goalId;
  }
}
