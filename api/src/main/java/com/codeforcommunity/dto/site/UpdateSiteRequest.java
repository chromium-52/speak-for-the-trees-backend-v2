package com.codeforcommunity.dto.site;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class UpdateSiteRequest extends ApiDto {
  private Boolean treePresent;
  private String status;
  private String genus;
  private String species;
  private String commonName;
  private String confidence;
  private Double diameter;
  private Double circumference;
  private Boolean multistem;
  private String coverage;
  private String pruning;
  private String condition;
  private Boolean discoloring;
  private Boolean leaning;
  private Boolean constrictingGate;
  private Boolean wounds;
  private Boolean pooling;
  private Boolean stakesWithWires;
  private Boolean stakesWithoutWires;
  private Boolean light;
  private Boolean bicycle;
  private Boolean bagEmpty;
  private Boolean bagFilled;
  private Boolean tape;
  private Boolean suckerGrowth;
  private String siteType;
  private String sidewalkWidth;
  private Double siteWidth;
  private Double siteLength;
  private String material;
  private Boolean raisedBed;
  private Boolean fence;
  private Boolean trash;
  private Boolean wires;
  private Boolean grate;
  private Boolean stump;
  private String treeNotes;
  private String siteNotes;

  public UpdateSiteRequest(
      Boolean treePresent,
      String status,
      String genus,
      String species,
      String commonName,
      String confidence,
      Double diameter,
      Double circumference,
      Boolean multistem,
      String coverage,
      String pruning,
      String condition,
      Boolean discoloring,
      Boolean leaning,
      Boolean constrictingGate,
      Boolean wounds,
      Boolean pooling,
      Boolean stakesWithWires,
      Boolean stakesWithoutWires,
      Boolean light,
      Boolean bicycle,
      Boolean bagEmpty,
      Boolean bagFilled,
      Boolean tape,
      Boolean suckerGrowth,
      String siteType,
      String sidewalkWidth,
      Double siteWidth,
      Double siteLength,
      String material,
      Boolean raisedBed,
      Boolean fence,
      Boolean trash,
      Boolean wires,
      Boolean grate,
      Boolean stump,
      String treeNotes,
      String siteNotes) {
    this.treePresent = treePresent;
    this.status = status;
    this.genus = genus;
    this.species = species;
    this.commonName = commonName;
    this.confidence = confidence;
    this.diameter = diameter;
    this.circumference = circumference;
    this.multistem = multistem;
    this.coverage = coverage;
    this.pruning = pruning;
    this.condition = condition;
    this.discoloring = discoloring;
    this.leaning = leaning;
    this.constrictingGate = constrictingGate;
    this.wounds = wounds;
    this.pooling = pooling;
    this.stakesWithWires = stakesWithWires;
    this.stakesWithoutWires = stakesWithoutWires;
    this.light = light;
    this.bicycle = bicycle;
    this.bagEmpty = bagEmpty;
    this.bagFilled = bagFilled;
    this.tape = tape;
    this.suckerGrowth = suckerGrowth;
    this.siteType = siteType;
    this.sidewalkWidth = sidewalkWidth;
    this.siteWidth = siteWidth;
    this.siteLength = siteLength;
    this.material = material;
    this.raisedBed = raisedBed;
    this.fence = fence;
    this.trash = trash;
    this.wires = wires;
    this.grate = grate;
    this.stump = stump;
    this.treeNotes = treeNotes;
    this.siteNotes = siteNotes;
  }

  public Boolean isTreePresent() {
    return treePresent;
  }

  public void setTreePresent(Boolean treePresent) {
    this.treePresent = treePresent;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getGenus() {
    return genus;
  }

  public void setGenus(String genus) {
    this.genus = genus;
  }

  public String getSpecies() {
    return species;
  }

  public void setSpecies(String species) {
    this.species = species;
  }

  public String getCommonName() {
    return commonName;
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  public String getConfidence() {
    return confidence;
  }

  public void setConfidence(String confidence) {
    this.confidence = confidence;
  }

  public Double getDiameter() {
    return diameter;
  }

  public void setDiameter(Double diameter) {
    this.diameter = diameter;
  }

  public Double getCircumference() {
    return circumference;
  }

  public void setCircumference(Double circumference) {
    this.circumference = circumference;
  }

  public Boolean isMultistem() {
    return multistem;
  }

  public void setMultistem(Boolean multistem) {
    this.multistem = multistem;
  }

  public String getCoverage() {
    return coverage;
  }

  public void setCoverage(String coverage) {
    this.coverage = coverage;
  }

  public String getPruning() {
    return pruning;
  }

  public void setPruning(String pruning) {
    this.pruning = pruning;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public Boolean isDiscoloring() {
    return discoloring;
  }

  public void setDiscoloring(Boolean discoloring) {
    this.discoloring = discoloring;
  }

  public Boolean isLeaning() {
    return leaning;
  }

  public void setLeaning(Boolean leaning) {
    this.leaning = leaning;
  }

  public Boolean isConstrictingGate() {
    return constrictingGate;
  }

  public void setConstrictingGate(Boolean constrictingGate) {
    this.constrictingGate = constrictingGate;
  }

  public Boolean isWounds() {
    return wounds;
  }

  public void setWounds(Boolean wounds) {
    this.wounds = wounds;
  }

  public Boolean isPooling() {
    return pooling;
  }

  public void setPooling(Boolean pooling) {
    this.pooling = pooling;
  }

  public Boolean isStakesWithWires() {
    return stakesWithWires;
  }

  public void setStakesWithWires(Boolean stakesWithWires) {
    this.stakesWithWires = stakesWithWires;
  }

  public Boolean isStakesWithoutWires() {
    return stakesWithoutWires;
  }

  public void setStakesWithoutWires(Boolean stakesWithoutWires) {
    this.stakesWithoutWires = stakesWithoutWires;
  }

  public Boolean isLight() {
    return light;
  }

  public void setLight(Boolean light) {
    this.light = light;
  }

  public Boolean isBicycle() {
    return bicycle;
  }

  public void setBicycle(Boolean bicycle) {
    this.bicycle = bicycle;
  }

  public Boolean isBagEmpty() {
    return bagEmpty;
  }

  public void setBagEmpty(Boolean bagEmpty) {
    this.bagEmpty = bagEmpty;
  }

  public Boolean isBagFilled() {
    return bagFilled;
  }

  public void setBagFilled(Boolean bagFilled) {
    this.bagFilled = bagFilled;
  }

  public Boolean isTape() {
    return tape;
  }

  public void setTape(Boolean tape) {
    this.tape = tape;
  }

  public Boolean isSuckerGrowth() {
    return suckerGrowth;
  }

  public void setSuckerGrowth(Boolean suckerGrowth) {
    this.suckerGrowth = suckerGrowth;
  }

  public String getSiteType() {
    return siteType;
  }

  public void setSiteType(String siteType) {
    this.siteType = siteType;
  }

  public String getSidewalkWidth() {
    return sidewalkWidth;
  }

  public void setSidewalkWidth(String sidewalkWidth) {
    this.sidewalkWidth = sidewalkWidth;
  }

  public Double getSiteWidth() {
    return siteWidth;
  }

  public void setSiteWidth(Double siteWidth) {
    this.siteWidth = siteWidth;
  }

  public Double getSiteLength() {
    return siteLength;
  }

  public void setSiteLength(Double siteLength) {
    this.siteLength = siteLength;
  }

  public String getMaterial() {
    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  public Boolean isRaisedBed() {
    return raisedBed;
  }

  public void setRaisedBed(Boolean raisedBed) {
    this.raisedBed = raisedBed;
  }

  public Boolean isFence() {
    return fence;
  }

  public void setFence(Boolean fence) {
    this.fence = fence;
  }

  public Boolean isTrash() {
    return trash;
  }

  public void setTrash(Boolean trash) {
    this.trash = trash;
  }

  public Boolean isWires() {
    return wires;
  }

  public void setWires(Boolean wires) {
    this.wires = wires;
  }

  public Boolean isGrate() {
    return grate;
  }

  public void setGrate(Boolean grate) {
    this.grate = grate;
  }

  public Boolean isStump() {
    return stump;
  }

  public void setStump(Boolean stump) {
    this.stump = stump;
  }

  public String getTreeNotes() {
    return treeNotes;
  }

  public void setTreeNotes(String treeNotes) {
    this.treeNotes = treeNotes;
  }

  public String getSiteNotes() {
    return siteNotes;
  }

  public void setSiteNotes(String siteNotes) {
    this.siteNotes = siteNotes;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    List<String> fields = new ArrayList<>();

    // All fields are optional
    return fields;
  }
}
