package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.team.*;

public interface ITeamsProcessor {
  void disbandTeam(JWTData userData, int teamId);

  void createTeam(JWTData userData, CreateTeamRequest request);

  TeamDataResponse getTeam(JWTData userData, GetTeamRequest getTeamRequest);

  void addGoal(JWTData userData, AddGoalRequest addGoalRequest);

  void deleteGoal(JWTData userData, DeleteGoalRequest deleteGoalRequest);
}
