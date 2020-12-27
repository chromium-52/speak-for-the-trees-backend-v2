package com.codeforcommunity.dto.user;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.HandledException;

import java.util.ArrayList;
import java.util.List;

public class ChangePrivilegeLevelRequest extends ApiDto {
    private String targetUserEmail;
    private String newLevel;
    private String password;

    public ChangePrivilegeLevelRequest(String targetUserEmail, String newLevel, String password) {
        this.targetUserEmail = targetUserEmail;
        this.newLevel = newLevel;
        this.password = password;
    }

    private ChangePrivilegeLevelRequest() {}

    public String getTargetUserEmail() { return targetUserEmail; }

    public void setTargetUserEmail(String targetUserEmail) { this.targetUserEmail = targetUserEmail; }

    public PrivilegeLevel getNewLevel() { return PrivilegeLevel.valueOf(newLevel); }

    public void setNewLevel(String newLevel) { this.newLevel = newLevel; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        String fieldName = fieldPrefix + "change_privilege_level_request.";
        List<String> fields = new ArrayList<>();

        if (emailInvalid(targetUserEmail)) {
            fields.add(fieldName + "email");
        }
        if (privilegeLevelInvalid(newLevel)) {
            fields.add(fieldName + "privilege_level");
        }
        if (password == null) {
            fields.add(fieldName + "password");
        }

        return fields;
    }
}
