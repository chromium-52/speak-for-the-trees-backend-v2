
package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.neighborhoods.EditCanopyCoverageRequest;

public interface IProtectedNeighborhoodsProcessor {



  /** Edits the specified neighborhood's canopy_coverage. */
  void editCanopyCoverage(JWTData userData, int neighborhoodID, EditCanopyCoverageRequest editCanopyCoverageRequest);
}