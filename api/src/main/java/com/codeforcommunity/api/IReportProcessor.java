package com.codeforcommunity.api;

import com.codeforcommunity.dto.report.GetCommunityStatsResponse;

public interface IReportProcessor {
  /**
   * Returns the number of current adopters, trees currently adopted, and all stewardship activities
   * ever performed.
   */
  GetCommunityStatsResponse getCommunityStats();
}
