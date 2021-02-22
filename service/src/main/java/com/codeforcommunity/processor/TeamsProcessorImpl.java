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
  public TeamDataResponse getTeam(JWTData userData, Integer teamId) {
    TeamsRecord team = db.selectFrom(TEAMS).where(TEAMS.ID.eq(teamId)).fetchOne();
    if (team == null) {
      throw new TeamDoesNotExistException(teamId);
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
  public void addGoal(JWTData userData, AddGoalRequest addGoalRequest, int teamId) {
    UsersTeamsRecord usersTeamsRecord =
        db.selectFrom(USERS_TEAMS)
            .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
            .and(USERS_TEAMS.TEAM_ID.eq(teamId))
            .fetchOne();

    if (usersTeamsRecord != null && usersTeamsRecord.getTeamRole() == TeamRole.LEADER) {
      GoalsRecord goal = db.newRecord(GOALS);
      goal.setTeamId(teamId);
      goal.setGoal(addGoalRequest.getGoal());
      goal.setStartAt(addGoalRequest.getStart_at());
      goal.setCompleteBy(addGoalRequest.getComplete_by());
      goal.store();
    } else {
      throw new WrongTeamRoleException(teamId, usersTeamsRecord.getTeamRole());
    }
  }

  @Override
  public void deleteGoal(JWTData userData, int teamId, int goalId) {
    UsersTeamsRecord usersTeamsRecord =
        db.selectFrom(USERS_TEAMS)
            .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
            .and(USERS_TEAMS.TEAM_ID.eq(teamId))
            .fetchOne();

    if (usersTeamsRecord != null && usersTeamsRecord.getTeamRole() == TeamRole.LEADER) {
      db.deleteFrom(GOALS).where(GOALS.ID.eq(goalId));
    } else {
      throw new WrongTeamRoleException(teamId, usersTeamsRecord.getTeamRole());
    }
  }

  @Override
  public void inviteUser(JWTData userData, InviteUserRequest inviteUserRequest) {
    // TODO what to do here. A link with an invite to join.
    UsersTeamsRecord inviterUserTeamsRecord =
        db.selectFrom(USERS_TEAMS)
            .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
            .and(USERS_TEAMS.TEAM_ID.eq(inviteUserRequest.getTeamId()))
            .fetchOne();

    if (inviterUserTeamsRecord != null && inviterUserTeamsRecord.getTeamRole() == TeamRole.LEADER) {
      inviteUserRequest
          .getUsers()
          .forEach(
              (name, email) -> {
                // Only invite a user if they are not a part of the team already
                if (!db.fetchExists(
                    USERS_TEAMS
                        .USERS_TEAMS
                        .join(USERS)
                        .on(USERS.ID.eq(USERS_TEAMS.USER_ID))
                        .where(
                            USERS
                                .EMAIL
                                .eq(email)
                                .and(TEAMS.ID.eq(inviteUserRequest.getTeamId()))))) {
                  UsersRecord invitedUser =
                      db.selectFrom(USERS).where(USERS.EMAIL.eq(email)).fetchOne();
                  if (invitedUser != null) {
                    UsersTeamsRecord usersTeams = db.newRecord(USERS_TEAMS);
                    usersTeams.setTeamId(inviteUserRequest.getTeamId());
                    usersTeams.setUserId(invitedUser.getId());
                    usersTeams.setTeamRole(TeamRole.PENDING);
                    usersTeams.store();
                    // TODO SEND EMAIL
                  }
                }
              });
    } else {
      throw new WrongTeamRoleException(
          inviteUserRequest.getTeamId(), inviterUserTeamsRecord.getTeamRole());
    }
  }

  @Override
  public List<UsersTeamDataResponse> getApplicants(
      JWTData userData, int teamId) {
    List<UsersTeamsRecord> applicants =
        db.selectFrom(USERS_TEAMS).where(USERS_TEAMS.TEAM_ROLE.eq(TeamRole.PENDING)).fetch();
    List<UsersTeamDataResponse> responses = new ArrayList<>();
    applicants.forEach(
        app -> {
          UsersTeamDataResponse response =
              new UsersTeamDataResponse(app.getUserId(), teamId, app.getTeamRole());
          responses.add(response);
        });
    return responses;
  }

  @Override
  public void applyToTeam(JWTData userData, int teamId) {
    if (db.fetchExists(
        USERS_TEAMS.where(
            USERS_TEAMS
                .TEAM_ID
                .eq(teamId)
                .and(USERS_TEAMS.USER_ID.eq(userData.getUserId()))))) {
      throw new MemberApplicationException(
          teamId, userData.getUserId());
    } else {
      UsersRecord invitedUser =
          db.selectFrom(USERS).where(USERS.ID.eq(userData.getUserId())).fetchOne();
      if (invitedUser != null) {
        UsersTeamsRecord usersTeams = db.newRecord(USERS_TEAMS);
        usersTeams.setTeamId(teamId);
        usersTeams.setUserId(invitedUser.getId());
        usersTeams.setTeamRole(TeamRole.PENDING);
        usersTeams.store();
      } else {
        throw new UserDoesNotExistException(userData.getUserId());
      }
    }
  }

  @Override
  public void approveUser(JWTData userData, int teamId, int memberId) {
    UsersTeamsRecord leaderTeamsRecord =
        db.selectFrom(USERS_TEAMS)
            .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
            .and(USERS_TEAMS.TEAM_ID.eq(teamId))
            .fetchOne();
    if (leaderTeamsRecord != null && leaderTeamsRecord.getTeamRole() == TeamRole.LEADER) {
      UsersTeamsRecord approvedUserRecord =
          db.selectFrom(USERS_TEAMS.where(USERS_TEAMS.USER_ID.eq(memberId)))
              .fetchOne();
      if (approvedUserRecord.getTeamRole() == TeamRole.PENDING) {
        approvedUserRecord.setTeamRole(TeamRole.MEMBER);
        approvedUserRecord.update();
      } else {
        throw new MemberStatusException(
            memberId, teamId);
      }
    } else {
      throw new WrongTeamRoleException(
          leaderTeamsRecord.getTeamId(), leaderTeamsRecord.getTeamRole());
    }
  }

  @Override
  public void rejectUser(JWTData userData, int teamId, int memberId) {
    UsersTeamsRecord leaderTeamsRecord =
        db.selectFrom(USERS_TEAMS)
            .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
            .and(USERS_TEAMS.TEAM_ID.eq(teamId))
            .fetchOne();
    if (leaderTeamsRecord != null && leaderTeamsRecord.getTeamRole() == TeamRole.LEADER) {
      UsersTeamsRecord approvedUserRecord =
          db.selectFrom(USERS_TEAMS.where(USERS_TEAMS.TEAM_ID.eq(teamId).and(USERS_TEAMS.USER_ID.eq(memberId))))
              .fetchOne();
      if (approvedUserRecord.getTeamRole() == TeamRole.PENDING) {
        approvedUserRecord.setTeamRole(TeamRole.None);
        approvedUserRecord.update();
      } else {
        throw new MemberStatusException(
            userData.getUserId(), teamId);
      }
    } else {
      throw new WrongTeamRoleException(
          leaderTeamsRecord.getTeamId(), leaderTeamsRecord.getTeamRole());
    }
  }

  @Override
  public void kickUser(JWTData userData, int teamId, int memberId) {
    UsersTeamsRecord leaderTeamsRecord =
        db.selectFrom(USERS_TEAMS)
            .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
            .and(USERS_TEAMS.TEAM_ID.eq(teamId))
            .fetchOne();
    if (leaderTeamsRecord != null && leaderTeamsRecord.getTeamRole() == TeamRole.LEADER) {
      UsersTeamsRecord approvedUserRecord =
          db.selectFrom(USERS_TEAMS.where(USERS_TEAMS.USER_ID.eq(memberId)))
              .fetchOne();
      if (approvedUserRecord.getTeamRole() == TeamRole.MEMBER) {
        approvedUserRecord.setTeamRole(TeamRole.None);
        approvedUserRecord.update();
      } else {
        throw new MemberStatusException(memberId, teamId);
      }
    } else {
      throw new WrongTeamRoleException(
          leaderTeamsRecord.getTeamId(), leaderTeamsRecord.getTeamRole());
    }
  }

  @Override
  public void leaveTeam(JWTData userData, int teamId) {
    UsersTeamsRecord usersTeamsRecord =
        db.selectFrom(USERS_TEAMS)
            .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
            .and(USERS_TEAMS.TEAM_ID.eq(teamId))
            .fetchOne();
    if (usersTeamsRecord != null && usersTeamsRecord.getTeamRole() != TeamRole.LEADER) {
      usersTeamsRecord.setTeamRole(TeamRole.None);
      usersTeamsRecord.update();
    } else {
      throw new MemberStatusException(teamId, userData.getUserId());
    }
  }

  @Override
  public void transferOwnership(
      JWTData userData, TransferOwnershipRequest transferOwnershipRequest) {
    UsersTeamsRecord oldLeaderTeamsRecord =
        db.selectFrom(USERS_TEAMS)
            .where(USERS_TEAMS.USER_ID.eq(userData.getUserId()))
            .and(USERS_TEAMS.TEAM_ID.eq(transferOwnershipRequest.getTeamId()))
            .fetchOne();
    UsersTeamsRecord newLeaderTeamsRecord =
        db.selectFrom(USERS_TEAMS)
            .where(
                USERS_TEAMS
                    .USER_ID
                    .eq(transferOwnershipRequest.getNewLeaderId())
                    .and(USERS_TEAMS.TEAM_ID.eq(transferOwnershipRequest.getTeamId())))
            .fetchOne();
    if (oldLeaderTeamsRecord != null
        && newLeaderTeamsRecord != null
        && oldLeaderTeamsRecord.getTeamRole() == TeamRole.LEADER
        && oldLeaderTeamsRecord.getTeamRole() == TeamRole.MEMBER) {
      newLeaderTeamsRecord.setTeamRole(TeamRole.LEADER);
      oldLeaderTeamsRecord.setTeamRole(TeamRole.MEMBER);
      newLeaderTeamsRecord.update();
      oldLeaderTeamsRecord.update();
    } else {
      // TODO improve error handling for this method to be more specific.
      throw new WrongTeamRoleException(transferOwnershipRequest.getTeamId(), TeamRole.LEADER);
    }
  }
}
