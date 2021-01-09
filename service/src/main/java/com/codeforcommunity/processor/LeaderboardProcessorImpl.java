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
import org.jooq.SelectSeekStep2;

public class LeaderboardProcessorImpl implements ILeaderboardProcessor {
  private final DSLContext db;

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
            .orderBy(BLOCKS.ID, RESERVATIONS.PERFORMED_AT.desc());

    // select user_id, username and number of blocks completed
    List<LeaderboardEntry> users =
        db.select(USERS.ID, USERS.USERNAME, count())
            .from(USERS)
            .innerJoin(RESERVATIONS)
            .onKey()
            .where(RESERVATIONS.ACTION_TYPE.in(ReservationAction.COMPLETE, ReservationAction.QA))
            .and(
                RESERVATIONS.PERFORMED_AT.greaterThan(
                    new Timestamp(
                        System.currentTimeMillis()
                            - (getUsersLeaderboardRequest.getPreviousDays() * 86400))))
            .groupBy(USERS.ID)
            .having(count().greaterThan(0))
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
