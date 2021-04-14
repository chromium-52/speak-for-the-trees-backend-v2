package com.codeforcommunity.dto.site;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.ArrayList;
import java.util.List;

public class RecordStewardshipRequest extends ApiDto {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
  private java.sql.Date date;

  private Integer duration;
  private Boolean watered;
  private Boolean mulched;
  private Boolean cleaned;
  private Boolean weeded;

  public RecordStewardshipRequest(
      java.sql.Date date,
      Integer duration,
      Boolean watered,
      Boolean mulched,
      Boolean cleaned,
      Boolean weeded) {
    this.date = date;
    this.duration = duration;
    this.watered = watered;
    this.mulched = mulched;
    this.cleaned = cleaned;
    this.weeded = weeded;
  }

  private RecordStewardshipRequest() {}

  public java.sql.Date getDate() {
    return date;
  }

  public void setDate(java.sql.Date date) {
    this.date = date;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Boolean getWatered() {
    return watered;
  }

  public void setWatered(Boolean watered) {
    this.watered = watered;
  }

  public Boolean getMulched() {
    return mulched;
  }

  public void setMulched(Boolean mulched) {
    this.mulched = mulched;
  }

  public Boolean getCleaned() {
    return cleaned;
  }

  public void setCleaned(Boolean cleaned) {
    this.cleaned = cleaned;
  }

  public Boolean getWeeded() {
    return weeded;
  }

  public void setWeeded(Boolean weeded) {
    this.weeded = weeded;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "record_stewardship_request.";
    List<String> fields = new ArrayList<>();

    if (date == null) {
      fields.add(fieldName + "date");
    }
    if (!(watered || mulched || cleaned || weeded)) {
      fields.add(fieldName + "activities");
    }

    return fields;
  }
}
