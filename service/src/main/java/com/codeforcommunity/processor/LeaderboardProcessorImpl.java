package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.BLOCKS;
import static org.jooq.generated.Tables.RESERVATIONS;
import static org.jooq.generated.Tables.TEAMS;
import static org.jooq.generated.tables.Users.USERS;
import static org.jooq.impl.DSL.count;

import com.codeforcommunity.api.ILeaderboardProcessor;
import com.codeforcommunity.dto.leaderboard.GetLeaderboardRequest;
import com.codeforcommunity.dto.leaderboard.GetLeaderboardResponse;
import com.codeforcommunity.dto.leaderboard.LeaderboardEntry;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.enums.ReservationAction;
import java.sql.Timestamp;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Select;

public class LeaderboardProcessorImpl implements ILeaderboardProcessor {
  private final DSLContext db;
  private static final int dayInMs = 86400000;
  private static final int leaderboardLimit = 100;

  public LeaderboardProcessorImpl(DSLContext db) {
    this.db = db;
  }

  @Override
  public GetLeaderboardResponse getUsersLeaderboard(
      GetLeaderboardRequest getUsersLeaderboardRequest) {

    Select<Record3<Integer, Integer, ReservationAction>> subquery =
        db.select(BLOCKS.ID, RESERVATIONS.USER_ID, RESERVATIONS.ACTION_TYPE)
            .distinctOn(BLOCKS.ID)
            .from(BLOCKS)
            .join(RESERVATIONS)
            .onKey()
            .where(
                RESERVATIONS.PERFORMED_AT.greaterThan(
                    new Timestamp(
                        System.currentTimeMillis()
                            - (getUsersLeaderboardRequest.getPreviousDays() * dayInMs))))
            .orderBy(BLOCKS.ID, RESERVATIONS.PERFORMED_AT.desc());

    List<LeaderboardEntry> users =
        db.select(subquery.field(1).as("user_id"), USERS.USERNAME, count())
            .from(subquery)
            .innerJoin(USERS)
            .on(USERS.ID.eq(subquery.field(1).cast(Integer.class)))
            .where(
                subquery
                    .field(2)
                    .cast(ReservationAction.class)
                    .in(ReservationAction.COMPLETE, ReservationAction.QA))
            .and(USERS.PRIVILEGE_LEVEL.notEqual(PrivilegeLevel.SUPER_ADMIN))
            .groupBy(subquery.field(1), USERS.USERNAME)
            .orderBy(count().desc())
            .limit(leaderboardLimit)
            .fetchInto(LeaderboardEntry.class);

    return new GetLeaderboardResponse(users);
  }

  @Override
  public GetLeaderboardResponse getTeamsLeaderboard(
      GetLeaderboardRequest getTeamsLeaderboardRequest) {

    Select<Record3<Integer, Integer, ReservationAction>> subquery =
        db.select(BLOCKS.ID, RESERVATIONS.TEAM_ID, RESERVATIONS.ACTION_TYPE)
            .distinctOn(BLOCKS.ID)
            .from(BLOCKS)
            .join(RESERVATIONS)
            .onKey()
            .where(
                RESERVATIONS.PERFORMED_AT.greaterThan(
                    new Timestamp(
                        System.currentTimeMillis()
                            - (getTeamsLeaderboardRequest.getPreviousDays() * dayInMs))))
            .orderBy(BLOCKS.ID, RESERVATIONS.PERFORMED_AT.desc());

    List<LeaderboardEntry> teams =
        db.select(subquery.field(1).as("team_id"), TEAMS.TEAM_NAME, count())
            .from(subquery)
            .innerJoin(TEAMS)
            .on(TEAMS.ID.eq(subquery.field(1).cast(Integer.class)))
            .where(
                subquery
                    .field(2)
                    .cast(ReservationAction.class)
                    .in(ReservationAction.COMPLETE, ReservationAction.QA))
            .groupBy(subquery.field(1), TEAMS.TEAM_NAME)
            .orderBy(count().desc())
            .limit(leaderboardLimit)
            .fetchInto(LeaderboardEntry.class);

    return new GetLeaderboardResponse(teams);
  }
}
