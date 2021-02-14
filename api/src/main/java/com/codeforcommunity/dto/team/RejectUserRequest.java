package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;

import java.util.List;

public class RejectUserRequest extends ApiDto {
    private Integer userId;
    private Integer teamId;

    public RejectUserRequest(Integer userId, Integer teamId) {
        this.userId = userId;
        this.teamId = teamId;
    }

    private RejectUserRequest(){};

    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        return null;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
}
