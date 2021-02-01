package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;

import java.security.Timestamp;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class AddGoalRequest extends ApiDto {
    private Integer goal;
    private  Integer teamId;
    private Timestamp startAt;
    private Timestamp completeBy;

    public AddGoalRequest(Integer goal, Integer teamId, Timestamp startAt, Timestamp completeBy) {
        this.goal= goal;
        this.teamId = teamId;
        this.startAt = startAt;
        this.completeBy = completeBy;
    }

    private AddGoalRequest(){}

    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        String fieldName = fieldPrefix + "add_goal_request.";
        List<String> fields = new ArrayList<>();
        if (goal == null) {
            fields.add(fieldName + "goal");
        }
        if (teamId == null) {
            fields.add(fieldName + "teamId");
        }
        if (startAt == null) {
            fields.add(fieldName + "start_by");
        }
        if (completeBy == null) {
            fields.add(fieldName + "complete_by");
        }
        return fields;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Timestamp getStartAt() {
        return startAt;
    }

    public void setStartAt(Timestamp startAt) {
        this.startAt = startAt;
    }

    public Timestamp getCompleteBy() {
        return completeBy;
    }

    public void setCompleteBy(Timestamp completeBy) {
        this.completeBy = completeBy;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }
}
