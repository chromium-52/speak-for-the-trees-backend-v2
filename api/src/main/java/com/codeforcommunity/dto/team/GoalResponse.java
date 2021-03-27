package com.codeforcommunity.dto.team;

import java.sql.Timestamp;

public class GoalResponse {
  private final int goalId;
  private final int goal;
  private final Timestamp startDate;
  private final Timestamp completeBy;
  private final Timestamp completionDate;

  public GoalResponse(
      int goalId, int goal, Timestamp startDate, Timestamp completeBy, Timestamp completionDate) {
    this.goalId = goalId;
    this.goal = goal;
    this.startDate = startDate;
    this.completeBy = completeBy;
    this.completionDate = completionDate;
  }

  public int getGoalId() {
    return goalId;
  }

  public int getGoal() {
    return goal;
  }

  public Timestamp getStartDate() {
    return startDate;
  }

  public Timestamp getCompleteBy() {
    return completeBy;
  }

  public Timestamp getCompletionDate() {
    return completionDate;
  }
}
