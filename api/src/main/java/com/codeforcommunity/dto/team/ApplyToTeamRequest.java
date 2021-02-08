package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;

import java.util.ArrayList;
import java.util.List;

public class ApplyToTeamRequest extends ApiDto {
    private Integer teamId;
    private Integer userId;
    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        String fieldName = fieldPrefix + "apply_to_team_request.";
        List<String> fields = new ArrayList<>();

        if (teamId == null) {
            fields.add(fieldName + "team_id");
        }
        if (userId == null) {
            fields.add(fieldName + "users");
        }
        return fields;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
