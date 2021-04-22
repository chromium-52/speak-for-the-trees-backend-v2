package com.codeforcommunity.dto.site;

import java.util.List;

public class StewardshipActivitiesResponse {
  public final List<StewardshipActivity> stewardshipActivities;

  public StewardshipActivitiesResponse(List<StewardshipActivity> stewardshipActivities) {
    this.stewardshipActivities = stewardshipActivities;
  }

  public List<StewardshipActivity> getStewardshipActivities() {
    return stewardshipActivities;
  }
}
