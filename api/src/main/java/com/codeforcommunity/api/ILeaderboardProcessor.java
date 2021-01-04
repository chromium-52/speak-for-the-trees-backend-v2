package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.leaderboard.GetLeaderboardRequest;
import com.codeforcommunity.dto.leaderboard.GetTeamsLeaderboardResponse;
import com.codeforcommunity.dto.leaderboard.GetUsersLeaderboardResponse;

public interface ILeaderboardProcessor {

    /** TODO: Explanation */
    GetUsersLeaderboardResponse getUsersLeaderboard(JWTData userData, GetLeaderboardRequest getUsersLeaderboardRequest);

    /** TODO: Explanation */
    GetTeamsLeaderboardResponse getTeamsLeaderboard(JWTData userData, GetLeaderboardRequest getTeamsLeaderboardRequest);

}
