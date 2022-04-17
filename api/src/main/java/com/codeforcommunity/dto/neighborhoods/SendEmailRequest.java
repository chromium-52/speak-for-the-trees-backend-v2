package com.codeforcommunity.dto.neighborhoods;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class SendEmailRequest extends ApiDto {
  private List<Integer> neighborhoodIDs;
  private String emailBody;

  public SendEmailRequest(List<Integer> neighborhoodIDs, String emailBody) {
    this.neighborhoodIDs = neighborhoodIDs;
    this.emailBody = emailBody;
  }

  private SendEmailRequest() {}

  public List<Integer> getNeighborhoodIDs() {
    return this.neighborhoodIDs;
  }

  public void setNeighborhoodIDs(List<Integer> neighborhoodIDs) {
    this.neighborhoodIDs = neighborhoodIDs;
  }

  public String getEmailBody() {
    return this.emailBody;
  }

  public void setSites(String emailBody) {
    this.emailBody = emailBody;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "send_email";
    List<String> fields = new ArrayList<>();

    if (neighborhoodIDs == null) {
      fields.add(fieldName + "neighborhoodIDs");
    }
    if (emailBody == null) {
      fields.add(fieldName + "emailBody");
    }

    return fields;
  }
}