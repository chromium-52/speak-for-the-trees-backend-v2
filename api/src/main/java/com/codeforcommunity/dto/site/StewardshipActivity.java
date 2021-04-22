package com.codeforcommunity.dto.site;

import java.util.Date;

public class StewardshipActivity {
  private final int id;
  private final int user_id;
  private final Date date;
  private final Boolean watered;
  private final Boolean mulched;
  private final Boolean cleaned;
  private final Boolean weeded;

  public StewardshipActivity(
      int id,
      int user_id,
      Date date,
      Boolean watered,
      Boolean mulched,
      Boolean cleaned,
      Boolean weeded) {
    this.id = id;
    this.user_id = user_id;
    this.date = date;
    this.watered = watered;
    this.mulched = mulched;
    this.cleaned = cleaned;
    this.weeded = weeded;
  }

  public int getId() {
    return id;
  }

  public int getUser_id() {
    return user_id;
  }

  public Date getDate() {
    return date;
  }

  public Boolean getWatered() {
    return watered;
  }

  public Boolean getMulched() {
    return mulched;
  }

  public Boolean getCleaned() {
    return cleaned;
  }

  public Boolean getWeeded() {
    return weeded;
  }
}
