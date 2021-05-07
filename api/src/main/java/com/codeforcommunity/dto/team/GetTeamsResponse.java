package com.codeforcommunity.dto.team;

import java.util.List;

public class GetTeamsResponse {
    private final List<TeamDataResponse> teams;

    public GetTeamsResponse(List<TeamDataResponse> teams) {
        this.teams = teams;
    }

    public List<TeamDataResponse> getTeams() {
        return teams;
    }
}
