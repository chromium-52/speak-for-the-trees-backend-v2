package com.codeforcommunity.processor;

import com.codeforcommunity.api.IEmailerProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.emailer.AddTemplateRequest;
import com.codeforcommunity.requester.S3Requester;

public class EmailerProcessorImpl extends AbstractProcessor
        implements IEmailerProcessor {

  @Override
  public void addTemplate(JWTData userData, AddTemplateRequest addTemplateRequest) {
    assertAdminOrSuperAdmin(userData.getPrivilegeLevel());
    S3Requester.uploadHTML(
            userData.getUserId(),
            addTemplateRequest.getName(),
            addTemplateRequest.getTemplate());
  }
}
