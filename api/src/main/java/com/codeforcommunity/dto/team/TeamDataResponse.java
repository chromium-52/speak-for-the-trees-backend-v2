package com.codeforcommunity.dto.team;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public class TeamDataResponse {
  private final Integer id;
  private final String teamName;
  private final String bio;
  private final Boolean finished;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
  private final Timestamp createdAt;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
  private final Timestamp deletedAt;

  public TeamDataResponse(
      Integer id,
      String teamName,
      String bio,
      Boolean finished,
      Timestamp createdAt,
      Timestamp deletedAt) {
    this.id = id;
    this.teamName = teamName;
    this.bio = bio;
    this.finished = finished;
    this.createdAt = createdAt;
    this.deletedAt = deletedAt;
  }


  public String getTeamName() {
    return teamName;
  }

  public String getBio() {
    return bio;
  }

  public boolean isFinished() {
    return finished;
  }

  public Timestamp getDeletedAt() {
    return deletedAt;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }
}
