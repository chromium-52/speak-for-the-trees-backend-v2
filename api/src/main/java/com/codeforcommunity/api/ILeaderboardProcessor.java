package com.codeforcommunity.api;

import com.codeforcommunity.dto.leaderboard.GetLeaderboardRequest;
import com.codeforcommunity.dto.leaderboard.GetLeaderboardResponse;

public interface ILeaderboardProcessor {

  /** Returns a list of the top 100 users in order of blocks counted */
  GetLeaderboardResponse getUsersLeaderboard(GetLeaderboardRequest getUsersLeaderboardRequest);

  /** Returns a list of the top 100 teams in order of blocks counted */
  GetLeaderboardResponse getTeamsLeaderboard(GetLeaderboardRequest getTeamsLeaderboardRequest);
}
