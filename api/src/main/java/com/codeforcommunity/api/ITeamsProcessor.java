package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;

public interface ITeamsProcessor {
  void disbandTeam(JWTData userData, int teamId);
}
