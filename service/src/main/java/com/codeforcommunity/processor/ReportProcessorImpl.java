package com.codeforcommunity.processor;

import static org.jooq.generated.tables.AdoptedSites.ADOPTED_SITES;
import static org.jooq.generated.tables.Stewardship.STEWARDSHIP;
import static org.jooq.impl.DSL.countDistinct;

import com.codeforcommunity.api.IReportProcessor;
import com.codeforcommunity.dto.report.CommunityStats;
import com.codeforcommunity.dto.report.GetCommunityStatsResponse;
import org.jooq.DSLContext;

public class ReportProcessorImpl implements IReportProcessor {

  private final DSLContext db;

  public ReportProcessorImpl(DSLContext db) {
    this.db = db;
  }

  @Override
  public GetCommunityStatsResponse getCommunityStats() {
    CommunityStats communityStats =
            db.select(
                            countDistinct(ADOPTED_SITES.USER_ID),
                            countDistinct(ADOPTED_SITES.SITE_ID),
                            countDistinct(STEWARDSHIP.ID))
                    .from(ADOPTED_SITES)
                    .fullJoin(STEWARDSHIP)
                    .on(ADOPTED_SITES.SITE_ID.eq(STEWARDSHIP.SITE_ID))
                    .fetchInto(CommunityStats.class)
                    .get(0);

    return new GetCommunityStatsResponse(communityStats);
  }
}