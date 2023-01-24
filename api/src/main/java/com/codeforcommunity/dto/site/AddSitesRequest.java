package com.codeforcommunity.dto.site;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AddSitesRequest extends ApiDto {
  private List<AddSiteRequest> sites;

  public AddSitesRequest(List<AddSiteRequest> sites) {
    this.sites = sites;
  }

  private AddSitesRequest() {}

  public List<AddSiteRequest> getSites() {
    return sites;
  }

  public void setSites(List<AddSiteRequest> sites) {
    this.sites = sites;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String newFieldPrefix = fieldPrefix + "addSitesRequest.";

    if (sites == null) {
      return Collections.singletonList(newFieldPrefix + "sites");
    }

    return sites.stream()
        .flatMap(ni -> ni.validateFields(newFieldPrefix).stream())
        .collect(Collectors.toList());
  }
}
