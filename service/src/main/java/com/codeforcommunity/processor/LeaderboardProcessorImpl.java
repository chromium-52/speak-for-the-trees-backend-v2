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
import com.codeforcommunity.enums.ReservationAction;
import java.sql.Timestamp;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Select;

public class LeaderboardProcessorImpl implements ILeaderboardProcessor {
  private final DSLContext db;

  public LeaderboardProcessorImpl(DSLContext db) {
    this.db = db;
  }

  @Override
  public GetLeaderboardResponse getUsersLeaderboard(
      GetLeaderboardRequest getUsersLeaderboardRequest) {

    // subquery needs to select block, last action, user_id and last performed at
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
                            - (getUsersLeaderboardRequest.getPreviousDays() * 86400000))))
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
            .groupBy(subquery.field(1), USERS.USERNAME)
            .orderBy(count().desc())
            .limit(100)
            .fetchInto(LeaderboardEntry.class);

    return new GetLeaderboardResponse(users);
  }

  @Override
  public GetLeaderboardResponse getTeamsLeaderboard(
      GetLeaderboardRequest getTeamsLeaderboardRequest) {
    List<LeaderboardEntry> teams =
        db.select(TEAMS.ID, TEAMS.TEAM_NAME, count())
            .from(TEAMS)
            .innerJoin(RESERVATIONS)
            .onKey()
            .where(RESERVATIONS.ACTION_TYPE.in(ReservationAction.COMPLETE, ReservationAction.QA))
            .and(
                RESERVATIONS.PERFORMED_AT.greaterThan(
                    new Timestamp(
                        System.currentTimeMillis()
                            - (getTeamsLeaderboardRequest.getPreviousDays() * 86400))))
            .groupBy(TEAMS.ID)
            .having(count().greaterThan(0))
            .orderBy(count().desc())
            .limit(100)
            .fetchInto(LeaderboardEntry.class);

    return new GetLeaderboardResponse(teams);
  }
}
