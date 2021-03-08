package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.team.AddGoalRequest;
import com.codeforcommunity.dto.team.CreateTeamRequest;
import com.codeforcommunity.dto.team.InviteUserRequest;
import com.codeforcommunity.dto.team.TeamDataResponse;
import com.codeforcommunity.dto.team.TransferOwnershipRequest;
import com.codeforcommunity.dto.team.UsersTeamDataResponse;

import java.util.List;

public interface ITeamsProcessor {
  void disbandTeam(JWTData userData, int teamId);

  void createTeam(JWTData userData, CreateTeamRequest createTeamRequest);

  TeamDataResponse getTeam(JWTData userData, int teamId);

  void addGoal(JWTData userData, AddGoalRequest addGoalRequest, int teamId);

  void deleteGoal(JWTData userData, int teamId, int goalId);

  void inviteUser(JWTData userData, InviteUserRequest inviteUserRequest, int teamId);

  List<UsersTeamDataResponse> getApplicants(JWTData userData, int teamId);

  void applyToTeam(JWTData userData, int teamId);

  void approveUser(JWTData userData, int teamId, int memberId);

  void rejectUser(JWTData userData, int teamId, int memberId);

  void kickUser(JWTData userData, int teamId, int memberId);

  void leaveTeam(JWTData userData, int teamId);

  void transferOwnership(JWTData userData, TransferOwnershipRequest transferOwnershipRequest, int teamId);
}
