package com.codeforcommunity.dto.imports;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.enums.ReservationAction;
import com.codeforcommunity.exceptions.HandledException;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReservationImport extends ApiDto {
  private Integer blockId;
  private Integer userId;
  private Integer teamId;
  private ReservationAction actionType;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
  private Timestamp performedAt;

  public ReservationImport(
      Integer blockId,
      Integer userId,
      Integer teamId,
      ReservationAction actionType,
      Timestamp performedAt) {
    this.blockId = blockId;
    this.userId = userId;
    this.teamId = teamId;
    this.actionType = actionType;
    this.performedAt = performedAt;
  }

  private ReservationImport() {}

  public Integer getBlockId() {
    return blockId;
  }

  public void setBlockId(Integer blockId) {
    this.blockId = blockId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getTeamId() {
    return teamId;
  }

  public void setTeamId(Integer teamId) {
    this.teamId = teamId;
  }

  public ReservationAction getActionType() {
    return actionType;
  }

  public void setActionType(ReservationAction actionType) {
    this.actionType = actionType;
  }

  public Timestamp getPerformedAt() {
    return performedAt;
  }

  public void setPerformedAt(Timestamp performedAt) {
    this.performedAt = performedAt;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "reservations.";
    List<String> fields = new ArrayList<>();

    if (blockId == null) {
      fields.add(fieldName + "blockId");
    }
    if (actionType == null) {
      fields.add(fieldName + "actionType");
    }
    if (performedAt == null) {
      fields.add(fieldName + "performedAt");
    }

    return fields;
  }
}
