package org.sunbird.migration;

import java.util.Map;

public class UserDeclareEntity {

  private String userId;
  private String orgId;
  private String persona;
  private Map<String, Object> userInfo;
  private String status;

  UserDeclareEntity(
      String userId, String orgId, String persona, Map<String, Object> userInfo, String status) {
    this.userId = userId;
    this.orgId = orgId;
    this.persona = persona;
    this.userInfo = userInfo;
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getOrgId() {
    return orgId;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public String getPersona() {
    return persona;
  }

  public void setPersona(String persona) {
    this.persona = persona;
  }

  public Map<String, Object> getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(Map<String, Object> userInfo) {
    this.userInfo = userInfo;
  }
}
