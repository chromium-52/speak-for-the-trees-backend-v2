package com.codeforcommunity.dto.site;

import java.util.List;

public class FavoriteSitesResponse {
  private final List<Integer> favorites;

  public FavoriteSitesResponse(List<Integer> favorites) {
    this.favorites = favorites;
  }

  public List<Integer> getFavorites() {
    return favorites;
  }
}
