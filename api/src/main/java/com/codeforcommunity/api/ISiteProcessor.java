package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.site.RecordStewardshipRequest;

public interface ISiteProcessor {

  /** Creates a record in the favorite sites table linking the user and the site */
  void favoriteSite(JWTData userData, int siteId);

  /** Removes the record in the favorite sites table linking the user and the site */
  void unfavoriteSite(JWTData userData, int siteId);

  /** Records a new stewardship activity in the stewardship table linked to the given site */
  void recordStewardship(
      JWTData userData, int siteId, RecordStewardshipRequest recordStewardshipRequest);
}
