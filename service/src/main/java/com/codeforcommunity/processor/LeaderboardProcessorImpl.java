package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.*;
import static org.jooq.generated.tables.Users.USERS;
import static org.jooq.impl.DSL.count;

import com.codeforcommunity.api.ILeaderboardProcessor;
import com.codeforcommunity.dto.leaderboard.GetLeaderboardRequest;
import com.codeforcommunity.dto.leaderboard.GetLeaderboardResponse;
import com.codeforcommunity.dto.leaderboard.LeaderboardEntry;
import com.codeforcommunity.enums.ReservationAction;
import java.util.List;
import org.jooq.DSLContext;

public class LeaderboardProcessorImpl implements ILeaderboardProcessor {
  private final DSLContext db;

  public LeaderboardProcessorImpl(DSLContext db) {
    this.db = db;
  }

  @Override
  public GetLeaderboardResponse getUsersLeaderboard(
      GetLeaderboardRequest getUsersLeaderboardRequest) {
    // select user_id, username and number of blocks completed
    List<LeaderboardEntry> users =
        db.select(USERS.ID, USERS.USERNAME, count())
            .from(USERS)
            .leftJoin(RESERVATIONS)
            .on(USERS.ID.eq(RESERVATIONS.USER_ID))
            .where(RESERVATIONS.ACTION_TYPE.eq(ReservationAction.COMPLETE))
            .or(RESERVATIONS.ACTION_TYPE.eq(ReservationAction.QA))
            //                .and(RESERVATIONS.PERFORMED_AT.) check if happened within given
            // timeframe
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
            .leftJoin(RESERVATIONS)
            .on(TEAMS.ID.eq(RESERVATIONS.TEAM_ID))
            .where(RESERVATIONS.ACTION_TYPE.eq(ReservationAction.COMPLETE))
            .or(RESERVATIONS.ACTION_TYPE.eq(ReservationAction.QA))
            //                .and(RESERVATIONS.PERFORMED_AT.) check if happened within given
            // timeframe
            .groupBy(TEAMS.ID)
            .having(count().greaterThan(0))
            .orderBy(count().desc())
            .limit(100)
            .fetchInto(LeaderboardEntry.class);

    return new GetLeaderboardResponse(teams);
  }
}
