package com.codeforcommunity.dto.leaderboard;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class GetLeaderboardRequest extends ApiDto {
  private Integer previousDays;

  public GetLeaderboardRequest(Integer previousDays) {
    this.previousDays = previousDays;
  }

  private GetLeaderboardRequest() {}

  public Integer getPreviousDays() {
    return previousDays;
  }

  public void setPreviousDays(Integer previousDays) {
    this.previousDays = previousDays;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "leaderboard_request";
    List<String> fields = new ArrayList<>();

    if (previousDays == null) {
      fields.add(fieldName + "previousDays");
    }

    return fields;
  }
}
