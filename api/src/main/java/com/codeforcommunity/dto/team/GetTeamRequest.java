package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;

import java.util.ArrayList;
import java.util.List;


public class GetTeamRequest extends ApiDto {
    private Integer teamId;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public GetTeamRequest(int teamId) {
        this.teamId = teamId;
    }

    private GetTeamRequest() {}

    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        String fieldName = fieldPrefix + "create_team_request.";
        List<String> fields = new ArrayList<>();

        if (teamId == null) {
            fields.add(fieldName + "bio");
        }
        return fields;
    }
}
