package com.codeforcommunity.dto.team;

import java.sql.Timestamp;
import java.util.List;

public class TeamGoalDataResponse extends TeamDataResponse {
  private final List<GoalResponse> goals;

  public TeamGoalDataResponse(
      Integer id,
      String teamName,
      String bio,
      Boolean finished,
      List<GoalResponse> goals,
      Timestamp createdAt,
      Timestamp deletedAt) {
    super(id, teamName, bio, finished, createdAt, deletedAt);
    this.goals = goals;
  }

  public List<GoalResponse> getGoals() {
    return goals;
  }
}
