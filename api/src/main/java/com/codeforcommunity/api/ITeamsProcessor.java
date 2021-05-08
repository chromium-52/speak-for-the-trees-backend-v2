package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.team.AddGoalRequest;
import com.codeforcommunity.dto.team.CreateTeamRequest;
import com.codeforcommunity.dto.team.InviteUsersRequest;
import com.codeforcommunity.dto.team.GetTeamsResponse;
import com.codeforcommunity.dto.team.TeamGoalDataResponse;
import com.codeforcommunity.dto.team.TransferOwnershipRequest;
import com.codeforcommunity.dto.team.UsersResponse;

public interface ITeamsProcessor {
  void disbandTeam(JWTData userData, int teamId);

  void createTeam(JWTData userData, CreateTeamRequest createTeamRequest);

  TeamGoalDataResponse getTeam(JWTData userData, int teamId);

  void addGoal(JWTData userData, AddGoalRequest addGoalRequest, int teamId);

  void deleteGoal(JWTData userData, int teamId, int goalId);

  void inviteUser(JWTData userData, InviteUsersRequest inviteUserRequest, int teamId);

  UsersResponse getApplicants(int teamId);

  void applyToTeam(JWTData userData, int teamId);

  void approveUser(JWTData userData, int teamId, int memberId);

  void rejectUser(JWTData userData, int teamId, int memberId);

  void kickUser(JWTData userData, int teamId, int memberId);

  void leaveTeam(JWTData userData, int teamId);

  UsersResponse getMembers(int teamId);

  void transferOwnership(
      JWTData userData, TransferOwnershipRequest transferOwnershipRequest, int teamId);

  GetTeamsResponse getTeams();
}
