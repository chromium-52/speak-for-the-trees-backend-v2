package com.codeforcommunity.processor;

import com.codeforcommunity.api.ILeaderboardProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.leaderboard.GetLeaderboardRequest;
import com.codeforcommunity.dto.leaderboard.GetTeamsLeaderboardResponse;
import com.codeforcommunity.dto.leaderboard.GetUsersLeaderboardResponse;
import org.jooq.DSLContext;

public class LeaderboardProcessorImpl implements ILeaderboardProcessor {
    private final DSLContext db;

    public LeaderboardProcessorImpl(DSLContext db) {
        this.db = db;
    }

    @Override
    public GetUsersLeaderboardResponse getUsersLeaderboard(JWTData userData, GetLeaderboardRequest getUsersLeaderboardRequest) {
        return null;
    }

    @Override
    public GetTeamsLeaderboardResponse getTeamsLeaderboard(JWTData userData, GetLeaderboardRequest getTeamsLeaderboardRequest) {
        return null;
    }
}
