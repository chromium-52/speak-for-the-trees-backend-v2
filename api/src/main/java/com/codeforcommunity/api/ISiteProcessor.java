package com.codeforcommunity.api;

import com.codeforcommunity.dto.site.GetSiteResponse;
import com.codeforcommunity.dto.site.StewardshipActivitiesResponse;

public interface ISiteProcessor {

  /** Returns all the info about a specific site, including all site entries */
  GetSiteResponse getSite(int siteId);

  /** Returns all stewardship activities for the given site */
  StewardshipActivitiesResponse getStewardshipActivities(int siteId);
}
