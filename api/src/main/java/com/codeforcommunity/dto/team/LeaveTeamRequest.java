package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;

import java.util.ArrayList;
import java.util.List;

public class LeaveTeamRequest extends ApiDto {
    Integer teamId;

    public LeaveTeamRequest(Integer userId, Integer teamId) {
        this.teamId = teamId;
    }

    public LeaveTeamRequest() {
    }

    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        String fieldName = fieldPrefix + "leave_team_request.";
        List<String> fields = new ArrayList<>();
        if (teamId == null) {
            fields.add(fieldName + "team_id");
        }
        return fields;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
}
