package com.codeforcommunity.processor;

import com.codeforcommunity.api.ISiteProcessor;
import org.jooq.DSLContext;

public class SiteProcessorImpl implements ISiteProcessor {

  private final DSLContext db;

  public SiteProcessorImpl(DSLContext db) {
    this.db = db;
  }
}
