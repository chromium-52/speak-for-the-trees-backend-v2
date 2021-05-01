package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.site.AddSiteRequest;
import com.codeforcommunity.dto.site.AdoptedSitesResponse;
import com.codeforcommunity.dto.site.GetSiteResponse;
import com.codeforcommunity.dto.site.RecordStewardshipRequest;
import com.codeforcommunity.dto.site.StewardshipActivitiesResponse;
import com.codeforcommunity.dto.site.UpdateSiteRequest;

public interface ISiteProcessor {

  /** Creates a record in the adopted sites table linking the user and the site */
  void adoptSite(JWTData userData, int siteId);

  /** Removes the record in the adopted sites table linking the user and the site */
  void unadoptSite(JWTData userData, int siteId);

  /** Get users adopted sites */
  AdoptedSitesResponse getAdoptedSites(JWTData userData);

  /** Records a new stewardship activity in the stewardship table linked to the given site */
  void recordStewardship(
      JWTData userData, int siteId, RecordStewardshipRequest recordStewardshipRequest);

  /** Creates a new site with entries in the sites and site_entries tables */
  void addSite(JWTData userData, AddSiteRequest addSiteRequest);

  /** Returns all the info about a specific site, including all site entries */
  GetSiteResponse getSite(int siteId);

  /** Updates the latest state of the site and creates a new entry in the site_entries table */
  void updateSite(JWTData userData, int siteId, UpdateSiteRequest updateSiteRequest);

  /** Removes the site */
  void deleteSite(JWTData userData, int siteId);

  /** Removes the given stewardship activity */
  void deleteStewardship(JWTData userData, int activityId);

  /** Returns all stewardship activities for the given site */
  StewardshipActivitiesResponse getStewardshipActivities(int siteId);
}
