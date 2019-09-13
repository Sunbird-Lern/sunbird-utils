package org.sunbird.statevalidateutil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @desc POJO class for User
 * @author Hari
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class User implements Serializable {

  private static final long serialVersionUID = 7529802960267784945L;

  private String id;
  private Boolean emailVerified;
  private Boolean phoneVerified;
  private String rootOrgId;
  private String userId;
  private int flagsValue;

  public User(Boolean emailVerified, Boolean phoneVerified, String userId, String rootOrgId) {
    this.emailVerified = emailVerified;
    this.phoneVerified = phoneVerified;
    this.userId = userId;
    this.rootOrgId = rootOrgId;
  }

  public Boolean getEmailVerified() {
    return emailVerified;
  }

  public String getRootOrgId() {
    return rootOrgId;
  }

  public String getUserId() {
    return userId;
  }

  public Boolean getPhoneVerified() {
    return phoneVerified;
  }

  public int getFlagsValue() {
    return flagsValue;
  }

  public void setFlagsValue(int flagsValue) {
    this.flagsValue = flagsValue;
  }
}
