package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.neighborhoods.SendEmailRequest;

public interface IProtectedNeighborhoodsProcessor {

  /**
   * Sends an email with the given message to users in the specified neighborhoods. If no
   * neighborhoods are specified, send the email to users in all neighborhoods.
   */
  void sendEmailToNeighborhoods(JWTData userData, SendEmailRequest sendEmailRequest);
}
