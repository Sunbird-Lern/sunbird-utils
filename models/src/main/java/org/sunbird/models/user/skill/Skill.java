package org.sunbird.models.user.skill;

import java.util.HashMap;
import java.util.List;

public class Skill {
  private String skillName;
  private String addedAt;
  private List<HashMap<String, String>> endorsersList = null;
  private String addedBy;
  private Integer endorsementCount;
  private String id;
  private String skillNameToLowercase;
  private String userId;

  public String getSkillName() {
    return skillName;
  }

  public void setSkillName(String skillName) {
    this.skillName = skillName;
  }

  public String getAddedAt() {
    return addedAt;
  }

  public void setAddedAt(String addedAt) {
    this.addedAt = addedAt;
  }

  public List<HashMap<String, String>> getEndorsersList() {
    return endorsersList;
  }

  public void setEndorsersList(List<HashMap<String, String>> endorsersList) {
    this.endorsersList = endorsersList;
  }

  public String getAddedBy() {
    return addedBy;
  }

  public void setAddedBy(String addedBy) {
    this.addedBy = addedBy;
  }

  public Integer getEndorsementcount() {
    return endorsementCount;
  }

  public void setEndorsementcount(Integer endorsementCount) {
    this.endorsementCount = endorsementCount;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSkillNameToLowercase() {
    return skillNameToLowercase;
  }

  public void setSkillNameToLowercase(String skillNameToLowercase) {
    this.skillNameToLowercase = skillNameToLowercase;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
