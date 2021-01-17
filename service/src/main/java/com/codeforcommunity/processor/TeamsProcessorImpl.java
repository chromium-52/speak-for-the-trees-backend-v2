package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.TEAMS;
import static org.jooq.generated.Tables.USERS_TEAMS;

import com.codeforcommunity.api.ITeamsProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.enums.TeamRole;
import com.codeforcommunity.exceptions.TeamLeaderOnlyRouteException;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.TeamsRecord;
import org.jooq.generated.tables.records.UsersTeamsRecord;

import java.sql.Timestamp;

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
      throw new TeamLeaderOnlyRouteException(teamId);
    }

    TeamsRecord team = db.selectFrom(TEAMS).where(TEAMS.ID.eq(teamId)).fetchOne();
    team.setDeletedAt(new Timestamp(System.currentTimeMillis()));
    team.store();
  }
}
