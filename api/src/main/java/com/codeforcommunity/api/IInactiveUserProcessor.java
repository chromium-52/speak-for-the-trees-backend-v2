package com.codeforcommunity.api;

public interface IInactiveUserProcessor {
  /**
   * Sends an email to users who did not record activity in the last 21 days.
   */
  void emailInactiveUsers();
}
