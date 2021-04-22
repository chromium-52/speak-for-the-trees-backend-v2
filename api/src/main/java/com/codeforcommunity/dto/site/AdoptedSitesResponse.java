package com.codeforcommunity.dto.site;

import java.util.List;

public class AdoptedSitesResponse {
  private final List<Integer> adoptedSites;

  public AdoptedSitesResponse(List<Integer> adoptedSites) {
    this.adoptedSites = adoptedSites;
  }

  public List<Integer> getAdoptedSites() {
    return adoptedSites;
  }
}
