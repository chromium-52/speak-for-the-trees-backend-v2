package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.*;

import com.codeforcommunity.api.ITeamsProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.team.*;
import com.codeforcommunity.enums.TeamRole;
import com.codeforcommunity.exceptions.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.generated.tables.records.GoalsRecord;
import org.jooq.generated.tables.records.TeamsRecord;
import org.jooq.generated.tables.records.UsersRecord;
import org.jooq.generated.tables.records.UsersTeamsRecord;

public class TeamsProcessorImpl implements ITeamsProcessor {
  private final DSLContext db;

  public TeamsProcessorImpl(DSLContext db) {
    this.db = db;
  }

  @Override
  public void disbandTeam(JWTData userData, int teamId) {
    UsersTeamsRecord usersTeamsRecord =
        db.selectFrom(USERS_TEAMS)
            .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
            .and(USERS_TEAMS.TEAM_ID.eq(teamId))
            .fetchOne();

    if (usersTeamsRecord == null || usersTeamsRecord.getTeamRole() != TeamRole.LEADER) {
      throw new WrongTeamRoleException(teamId, usersTeamsRecord.getTeamRole());
    }

    TeamsRecord team = db.selectFrom(TEAMS).where(TEAMS.ID.eq(teamId)).fetchOne();
    team.setDeletedAt(new Timestamp(System.currentTimeMillis()));
    team.store();
  }

  @Override
  public void createTeam(JWTData userData, CreateTeamRequest request) {
    TeamsRecord team = db.newRecord(TEAMS);
    team.setBio(request.getBio());
    team.setTeamName(request.getName());
    team.store();
    UsersTeamsRecord usersTeam = db.newRecord(USERS_TEAMS);
    usersTeam.setTeamId(team.getId());
    usersTeam.setTeamRole(TeamRole.LEADER);
    usersTeam.setUserId(userData.getUserId());
    usersTeam.store();
  }

  @Override
  public TeamDataResponse getTeam(JWTData userData, GetTeamRequest getTeamRequest) {
    TeamsRecord team =
        db.selectFrom(TEAMS).where(TEAMS.ID.eq(getTeamRequest.getTeamId())).fetchOne();
    if (team == null) {
      throw new TeamDoesNotExistException(getTeamRequest.getTeamId());
    }

    return new TeamDataResponse(
        team.getId(),
        team.getTeamName(),
        team.getBio(),
        team.getFinished(),
        team.getCreatedAt(),
        team.getDeletedAt());
  }

  @Override
  public void addGoal(JWTData userData, AddGoalRequest addGoalRequest) {
    UsersTeamsRecord usersTeamsRecord =
            db.selectFrom(USERS_TEAMS)
                    .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
                    .and(USERS_TEAMS.TEAM_ID.eq(addGoalRequest.getTeamId()))
                    .fetchOne();

    if (usersTeamsRecord != null && usersTeamsRecord.getTeamRole() == TeamRole.LEADER) {
      GoalsRecord goal = db.newRecord(GOALS);
      goal.setGoal(addGoalRequest.getGoal());
      goal.getStartAt(addGoalRequest.getCompleteBy());
      goal.getCompleteBy(addGoalRequest.getCompleteBy());
      goal.store();
    } else {
      throw new WrongTeamRoleException(addGoalRequest.getTeamId(), usersTeamsRecord.getTeamRole());
    }
  }

  @Override
  public void deleteGoal(JWTData userData, DeleteGoalRequest deleteGoalRequest) {
    UsersTeamsRecord usersTeamsRecord =
            db.selectFrom(USERS_TEAMS)
                    .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
                    .and(USERS_TEAMS.TEAM_ID.eq(deleteGoalRequest.getTeamId()))
                    .fetchOne();

    if (usersTeamsRecord != null && usersTeamsRecord.getTeamRole() == TeamRole.LEADER) {
      db.delete(GOALS).where(GOALS.ID.eq(deleteGoalRequest.getGoalId()));
    }
  }

  @Override
  public void inviteUser(JWTData userData, InviteUserRequest inviteUserRequest) {
    //TODO What if the user does not exist
    // MAYBE this should just send a link that allows a user to request to join a team?
    UsersTeamsRecord inviterUserTeamsRecord =
            db.selectFrom(USERS_TEAMS)
                    .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
                    .and(USERS_TEAMS.TEAM_ID.eq(inviteUserRequest.getTeamId()))
                    .fetchOne();

    if (inviterUserTeamsRecord != null && inviterUserTeamsRecord.getTeamRole() == TeamRole.LEADER) {
      inviteUserRequest.getUsers().forEach((name, email) -> {
        // Only invite a user if they are not a part of the team already
        if (!db.fetchExists(USERS_TEAMS.USERS_TEAMS.join(USERS).on(USERS.ID.eq(USERS_TEAMS.USER_ID))
                .where(USERS.EMAIL.eq(email).and(TEAMS.ID.eq(inviteUserRequest.getTeamId()))
        ))) {
          UsersRecord invitedUser = db.selectFrom(USERS).where(USERS.EMAIL.eq(email)).fetchOne();
          if (invitedUser != null) {
            UsersTeamsRecord usersTeams = db.newRecord(USERS_TEAMS);
            usersTeams.setTeamId(inviteUserRequest.getTeamId());
            usersTeams.setUserId(invitedUser.getId());
            usersTeams.setTeamRole(TeamRole.PENDING);
            usersTeams.store();
            //TODO SEND EMAIL
          }
        }
      });
    } else {
      throw new WrongTeamRoleException(inviteUserRequest.getTeamId(), inviterUserTeamsRecord.getTeamRole());
    }
  }

  @Override
  public List<UsersTeamDataResponse> getApplicants(JWTData userData, GetApplicantsRequest getApplicantsRequest) {
    List<UsersTeamsRecord> applicants = db.selectFrom(USERS_TEAMS).where(USERS_TEAMS.TEAM_ROLE.eq(TeamRole.PENDING)).fetch();
    List<UsersTeamDataResponse> responses = new ArrayList<>();
    applicants.forEach(app -> {
      UsersTeamDataResponse response = new UsersTeamDataResponse(app.getUserId(), app.getTeamId(), app.getTeamRole());
      responses.add(response);
    });
    return responses;
  }

  @Override
  public void applyToTeam(JWTData userData, ApplyToTeamRequest applyToTeamRequest) {
    if (db.fetchExists(USERS_TEAMS.where(USERS_TEAMS.TEAM_ID.eq(applyToTeamRequest.getTeamId())
            .and(USERS_TEAMS.USER_ID.eq(applyToTeamRequest.getUserId()))))) {
      throw new MemberApplicationException(applyToTeamRequest.getTeamId(), applyToTeamRequest.getUserId());
    } else {
      UsersRecord invitedUser = db.selectFrom(USERS).where(USERS.ID.eq(applyToTeamRequest.getUserId())).fetchOne();
      if (invitedUser != null) {
        UsersTeamsRecord usersTeams = db.newRecord(USERS_TEAMS);
        usersTeams.setTeamId(applyToTeamRequest.getTeamId());
        usersTeams.setUserId(invitedUser.getId());
        usersTeams.setTeamRole(TeamRole.PENDING);
        usersTeams.store();
      } else {
        throw new UserDoesNotExistException(applyToTeamRequest.getUserId());
      }
    }
  }

  @Override
  public void approveUser(JWTData userData, ApproveUserRequest applyToTeamRequest) {
    UsersTeamsRecord leaderTeamsRecord =
            db.selectFrom(USERS_TEAMS)
                    .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
                    .and(USERS_TEAMS.TEAM_ID.eq(applyToTeamRequest.getTeamId()))
                    .fetchOne();
    if(leaderTeamsRecord != null && leaderTeamsRecord.getTeamRole() == TeamRole.LEADER) {
      UsersTeamsRecord approvedUserRecord = db.selectFrom(USERS_TEAMS
              .where(USERS_TEAMS.USER_ID.eq(applyToTeamRequest.getUserId()))).fetchOne();
      if (approvedUserRecord.getTeamRole() == TeamRole.PENDING) {
        approvedUserRecord.setTeamRole(TeamRole.MEMBER);
        approvedUserRecord.update();
      } else {
        throw new MemberStatusException(applyToTeamRequest.getUserId(), applyToTeamRequest.getTeamId());
      }
    } else {
      throw new WrongTeamRoleException(leaderTeamsRecord.getTeamId(), leaderTeamsRecord.getTeamRole());
    }
  }

  @Override
  public void rejectUser(JWTData userData, RejectUserRequest rejectUserRequest) {
    UsersTeamsRecord leaderTeamsRecord =
            db.selectFrom(USERS_TEAMS)
                    .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
                    .and(USERS_TEAMS.TEAM_ID.eq(rejectUserRequest.getTeamId()))
                    .fetchOne();
    if(leaderTeamsRecord != null && leaderTeamsRecord.getTeamRole() == TeamRole.LEADER) {
      UsersTeamsRecord approvedUserRecord = db.selectFrom(USERS_TEAMS
              .where(USERS_TEAMS.USER_ID.eq(rejectUserRequest.getUserId()))).fetchOne();
      if (approvedUserRecord.getTeamRole() == TeamRole.PENDING) {
        approvedUserRecord.delete();
      } else {
        throw new MemberStatusException(rejectUserRequest.getUserId(), rejectUserRequest.getTeamId());
      }
    } else {
      throw new WrongTeamRoleException(leaderTeamsRecord.getTeamId(), leaderTeamsRecord.getTeamRole());
    }
  }
}
