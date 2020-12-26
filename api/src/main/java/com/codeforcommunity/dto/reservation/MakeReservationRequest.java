package com.codeforcommunity.dto.reservation;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class MakeReservationRequest extends ApiDto {
  private Integer blockID;
  private Integer teamID;

  public MakeReservationRequest(Integer blockID, Integer teamID) {
    this.blockID = blockID;
    this.teamID = teamID;
  }

  private MakeReservationRequest() {}

  public Integer getBlockID() {
    return blockID;
  }

  public void setBlockID(Integer blockID) {
    this.blockID = blockID;
  }

  public Integer getTeamID() {
    return teamID;
  }

  public void setTeamID() {
    this.teamID = teamID;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "make_reservation_request";
    List<String> fields = new ArrayList<>();

    if (blockID == null) {
      fields.add(fieldName + "blockID");
    }
    if (teamID == null) {
      fields.add(fieldName + "teamID");
    }

    return fields;
  }
}
