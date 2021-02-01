package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;

import java.util.ArrayList;
import java.util.List;

public class CreateTeamRequest extends ApiDto {
    private String name;
    private String bio;
    private List<String> inviteEmails;

    public CreateTeamRequest(String name, String bio, List<String> inviteEmails) {
        this.name = name;
        this.bio = bio;
        this.inviteEmails = inviteEmails;
    }

    private CreateTeamRequest(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getInviteEmails() {
        return inviteEmails;
    }

    public void setInviteEmails(List<String> inviteEmails) {
        this.inviteEmails = inviteEmails;
    }

    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        String fieldName = fieldPrefix + "create_team_request.";
        List<String> fields = new ArrayList<>();

        if (bio == null) {
            fields.add(fieldName + "bio");
        }
        if (name == null) {
            fields.add(fieldName + "name");
        }
        if (inviteEmails == null) {
            fields.add(fieldName + "inviteEmails");
        } else {
            for(String email : inviteEmails) {
                if(emailInvalid(email)) {
                    fields.add(fieldName + "inviteEmails." + email);
                }
            }
        }
        return fields;
    }
}
