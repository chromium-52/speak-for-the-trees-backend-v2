package com.codeforcommunity.dto.imports;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;

import java.util.List;

public class ImportNeighborhoodsRequest extends ApiDto {
    @Override
    public List<String> validateFields(String fieldPrefix) throws HandledException {
        return null;
    }
}
