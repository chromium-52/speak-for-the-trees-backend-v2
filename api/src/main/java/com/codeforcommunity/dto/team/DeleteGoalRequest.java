package com.codeforcommunity.dto.team;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;

import java.util.List;

public class DeleteGoalRequest extends ApiDto {
    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        return null;
    }
}
