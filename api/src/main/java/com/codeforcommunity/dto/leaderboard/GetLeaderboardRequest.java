package com.codeforcommunity.dto.leaderboard;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class GetLeaderboardRequest extends ApiDto {
  private Integer timePeriod;

  public GetLeaderboardRequest(Integer timePeriod) {
    this.timePeriod = timePeriod;
  }

  private GetLeaderboardRequest() {}

  public Integer getTimePeriod() {
    return timePeriod;
  }

  public void setTimePeriod(Integer timePeriod) {
    this.timePeriod = timePeriod;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "leaderboard_request";
    List<String> fields = new ArrayList<>();

    if (timePeriod == null) {
      fields.add(fieldName + "timePeriod");
    }

    return fields;
  }
}
