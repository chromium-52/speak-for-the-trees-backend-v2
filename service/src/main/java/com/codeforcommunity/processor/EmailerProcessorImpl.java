package com.codeforcommunity.processor;

import com.codeforcommunity.api.IEmailerProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.emailer.AddTemplateRequest;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.codeforcommunity.requester.S3Requester;

public class EmailerProcessorImpl extends AbstractProcessor
        implements IEmailerProcessor {

  private final S3Requester requester;

  public EmailerProcessorImpl(S3Requester requester) {
    this.requester = requester;
  }

  @Override
  public void addTemplate(JWTData userData, AddTemplateRequest addTemplateRequest) {
    return;
  }
}
