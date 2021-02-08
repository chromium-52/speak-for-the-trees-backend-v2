package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InviteUserRequest extends ApiDto {

    private Integer teamId;
    private Map<String, String> users;

    public InviteUserRequest(Integer teamId, Map<String, String> users) {
        this.teamId = teamId;
        this.users = users;
    }

    public InviteUserRequest() {}

    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        String fieldName = fieldPrefix + "invite_user_request.";
        List<String> fields = new ArrayList<>();

        if (teamId == null) {
            fields.add(fieldName + "team_id");
        }
        if (users == null) {
            fields.add(fieldName + "users");
        } else {
            users.forEach((email, name) -> {
                if (emailInvalid(email)) {
                    fields.add(fieldName + "inviteEmails." + email);
                }
            });
        }
        return fields;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public void setUsers(Map<String, String> users) {
        this.users = users;
    }
}
