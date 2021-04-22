package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.site.AdoptedSitesResponse;
import com.codeforcommunity.dto.site.RecordStewardshipRequest;
import com.codeforcommunity.dto.site.StewardshipActivitiesResponse;

public interface ISiteProcessor {

  /** Creates a record in the favorite sites table linking the user and the site */
  void adoptSite(JWTData userData, int siteId);

  /** Removes the record in the favorite sites table linking the user and the site */
  void unadoptSite(JWTData userData, int siteId);

  /** Get users favorite sites */
  AdoptedSitesResponse getAdoptedSites(JWTData userData);

  /** Records a new stewardship activity in the stewardship table linked to the given site */
  void recordStewardship(
      JWTData userData, int siteId, RecordStewardshipRequest recordStewardshipRequest);

  /** Removes the given stewardship activity */
  void deleteStewardship(JWTData userData, int activityId);

  /** Returns all stewardship activities for the given site */
  StewardshipActivitiesResponse getStewardshipActivities(int siteId);
}
