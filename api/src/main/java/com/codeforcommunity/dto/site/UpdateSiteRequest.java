package com.codeforcommunity.dto.site;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class UpdateSiteRequest extends ApiDto {
  private Boolean tree_present;
  private String status;
  private String genus;
  private String species;
  private String common_name;
  private String confidence;
  private Double diameter;
  private Double circumference;
  private Boolean multistem;
  private String coverage;
  private String pruning;
  private String condition;
  private Boolean discoloring;
  private Boolean leaning;
  private Boolean constricting_gate;
  private Boolean wounds;
  private Boolean pooling;
  private Boolean stakes_with_wires;
  private Boolean stakes_without_wires;
  private Boolean light;
  private Boolean bicycle;
  private Boolean bag_empty;
  private Boolean bag_filled;
  private Boolean tape;
  private Boolean sucker_growth;
  private String site_type;
  private String sidewalk_width;
  private Double site_width;
  private Double site_length;
  private String material;
  private Boolean raised_bed;
  private Boolean fence;
  private Boolean trash;
  private Boolean wires;
  private Boolean grate;
  private Boolean stump;
  private String tree_notes;
  private String site_notes;

  public UpdateSiteRequest(
      Boolean tree_present,
      String status,
      String genus,
      String species,
      String common_name,
      String confidence,
      Double diameter,
      Double circumference,
      Boolean multistem,
      String coverage,
      String pruning,
      String condition,
      Boolean discoloring,
      Boolean leaning,
      Boolean constricting_gate,
      Boolean wounds,
      Boolean pooling,
      Boolean stakes_with_wires,
      Boolean stakes_without_wires,
      Boolean light,
      Boolean bicycle,
      Boolean bag_empty,
      Boolean bag_filled,
      Boolean tape,
      Boolean sucker_growth,
      String site_type,
      String sidewalk_width,
      Double site_width,
      Double site_length,
      String material,
      Boolean raised_bed,
      Boolean fence,
      Boolean trash,
      Boolean wires,
      Boolean grate,
      Boolean stump,
      String tree_notes,
      String site_notes) {
    this.tree_present = tree_present;
    this.status = status;
    this.genus = genus;
    this.species = species;
    this.common_name = common_name;
    this.confidence = confidence;
    this.diameter = diameter;
    this.circumference = circumference;
    this.multistem = multistem;
    this.coverage = coverage;
    this.pruning = pruning;
    this.condition = condition;
    this.discoloring = discoloring;
    this.leaning = leaning;
    this.constricting_gate = constricting_gate;
    this.wounds = wounds;
    this.pooling = pooling;
    this.stakes_with_wires = stakes_with_wires;
    this.stakes_without_wires = stakes_without_wires;
    this.light = light;
    this.bicycle = bicycle;
    this.bag_empty = bag_empty;
    this.bag_filled = bag_filled;
    this.tape = tape;
    this.sucker_growth = sucker_growth;
    this.site_type = site_type;
    this.sidewalk_width = sidewalk_width;
    this.site_width = site_width;
    this.site_length = site_length;
    this.material = material;
    this.raised_bed = raised_bed;
    this.fence = fence;
    this.trash = trash;
    this.wires = wires;
    this.grate = grate;
    this.stump = stump;
    this.tree_notes = tree_notes;
    this.site_notes = site_notes;
  }

  private UpdateSiteRequest() {}

  public Boolean isTree_present() {
    return tree_present;
  }

  public void setTree_present(Boolean tree_present) {
    this.tree_present = tree_present;
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

  public String getCommon_name() {
    return common_name;
  }

  public void setCommon_name(String common_name) {
    this.common_name = common_name;
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

  public Boolean isConstricting_gate() {
    return constricting_gate;
  }

  public void setConstricting_gate(Boolean constricting_gate) {
    this.constricting_gate = constricting_gate;
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

  public Boolean isStakes_with_wires() {
    return stakes_with_wires;
  }

  public void setStakes_with_wires(Boolean stakes_with_wires) {
    this.stakes_with_wires = stakes_with_wires;
  }

  public Boolean isStakes_without_wires() {
    return stakes_without_wires;
  }

  public void setStakes_without_wires(Boolean stakes_without_wires) {
    this.stakes_without_wires = stakes_without_wires;
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

  public Boolean isBag_empty() {
    return bag_empty;
  }

  public void setBag_empty(Boolean bag_empty) {
    this.bag_empty = bag_empty;
  }

  public Boolean isBag_filled() {
    return bag_filled;
  }

  public void setBag_filled(Boolean bag_filled) {
    this.bag_filled = bag_filled;
  }

  public Boolean isTape() {
    return tape;
  }

  public void setTape(Boolean tape) {
    this.tape = tape;
  }

  public Boolean isSucker_growth() {
    return sucker_growth;
  }

  public void setSucker_growth(Boolean sucker_growth) {
    this.sucker_growth = sucker_growth;
  }

  public String getSite_type() {
    return site_type;
  }

  public void setSite_type(String site_type) {
    this.site_type = site_type;
  }

  public String getSidewalk_width() {
    return sidewalk_width;
  }

  public void setSidewalk_width(String sidewalk_width) {
    this.sidewalk_width = sidewalk_width;
  }

  public Double getSite_width() {
    return site_width;
  }

  public void setSite_width(Double site_width) {
    this.site_width = site_width;
  }

  public Double getSite_length() {
    return site_length;
  }

  public void setSite_length(Double site_length) {
    this.site_length = site_length;
  }

  public String getMaterial() {
    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  public Boolean isRaised_bed() {
    return raised_bed;
  }

  public void setRaised_bed(Boolean raised_bed) {
    this.raised_bed = raised_bed;
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

  public String getTree_notes() {
    return tree_notes;
  }

  public void setTree_notes(String tree_notes) {
    this.tree_notes = tree_notes;
  }

  public String getSite_notes() {
    return site_notes;
  }

  public void setSite_notes(String site_notes) {
    this.site_notes = site_notes;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    List<String> fields = new ArrayList<>();

    // All fields are optional
    return fields;
  }
}
