
package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.neighborhoods.EditCanopyCoverageRequest;
import com.codeforcommunity.dto.neighborhoods.SendEmailRequest;

public interface IProtectedNeighborhoodsProcessor {

  /** Sends an email with the given message to users in the specified neighborhoods. */
  void sendEmail(JWTData userData, SendEmailRequest sendEmailRequest);

  /** Edits the specified neighborhood's canopy_coverage. */
  void editCanopyCoverage(JWTData userData, int neighborhoodID, EditCanopyCoverageRequest editCanopyCoverageRequest);
}