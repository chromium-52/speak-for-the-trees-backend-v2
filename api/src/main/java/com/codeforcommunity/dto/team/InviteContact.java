package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;

import java.util.ArrayList;
import java.util.List;

public class InviteContact extends ApiDto {
    private String name;
    private String email;

    public InviteContact(String name, String email) {
        this.name = name;
        this.email = email;
    }

    private InviteContact() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> validateFields(String fieldPrefix) throws HandledException {
        String fieldName = fieldPrefix + "invite_contact";
        List<String> fields = new ArrayList<>();
        if (name == null) {
            fields.add(fieldName + "name");
        }
        if (email == null) {
            fields.add(fieldName + "email");
        }
        return fields;
    }
}
