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

  @Override
  public void addTemplate(JWTData userData, AddTemplateRequest addTemplateRequest) {
    System.out.format("Uploading %s to S3 bucket %s...\n", file_path, bucket_name);
    final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
    try {
      s3.putObject(bucket_name, key_name, new File(file_path));
    } catch (AmazonServiceException e) {
      System.err.println(e.getErrorMessage());
      System.exit(1);
  }
}
