package com.codeforcommunity.processor;

import com.codeforcommunity.api.ITeamsProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.team.*;
import com.codeforcommunity.enums.TeamRole;
import com.codeforcommunity.exceptions.TeamDoesNotExistException;
import com.codeforcommunity.exceptions.WrongTeamRoleException;
import java.sql.Timestamp;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.GoalsRecord;
import org.jooq.generated.tables.records.TeamsRecord;
import org.jooq.generated.tables.records.UsersTeamsRecord;

import static org.jooq.generated.Tables.*;

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
    TeamsRecord team = db.selectFrom(TEAMS).where(TEAMS.ID.eq(getTeamRequest.getTeamId())).fetchOne();
    if (team == null) {
      throw new TeamDoesNotExistException(getTeamRequest.getTeamId());
    }

    return new TeamDataResponse(team.getId(), team.getTeamName(), team.getBio(), team.getFinished(),
            team.getCreatedAt(), team.getDeletedAt());

  }

  @Override
  public void addGoal(JWTData userData, AddGoalRequest addGoalRequest) {
    //TODO if user is not leader. thror error and send 401 unauthorized
    GoalsRecord goal = db.newRecord(GOALS);
    goal.setGoal(addGoalRequest.getGoal());
    goal.getStartAt(addGoalRequest.getCompleteBy());
    goal.getCompleteBy(addGoalRequest.getCompleteBy());
  }

  @Override
  public void deleteGoal(JWTData userData, DeleteGoalRequest deleteGoalRequest) {

  }
}
