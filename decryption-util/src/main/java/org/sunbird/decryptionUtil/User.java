package org.sunbird.decryptionUtil;

import java.util.Date;

/**
 * this class is used to map the csv value into User class object below are the fields used in the
 * table (usr_external_Identity) provider, idtype, externalid, createdby, createdon, lastupdatedby,
 * lastupdatedon, originalexternalid, originalidtype, originalprovider, userid
 *
 * @count 11
 * @author anmolgupta
 */
public class User {

  private String provider;
  private String idType;
  private String externalId;
  private String createdBy;
  private Date createdOn;
  private String lastUpdatedBy;
  private Date lastUpdatedOn;
  private String originalExternalId;
  private String originalIdType;
  private String originalProvider;
  private String userId;

  public User(
      String provider,
      String idType,
      String externalId,
      String createdBy,
      Date createdOn,
      String lastUpdatedBy,
      Date lastUpdatedOn,
      String originalExternalId,
      String originalIdType,
      String originalProvider,
      String userId) {
    this.provider = provider;
    this.idType = idType;
    this.externalId = externalId;
    this.createdBy = createdBy;
    this.createdOn = createdOn;
    this.lastUpdatedBy = lastUpdatedBy;
    this.lastUpdatedOn = lastUpdatedOn;
    this.originalExternalId = originalExternalId;
    this.originalIdType = originalIdType;
    this.originalProvider = originalProvider;
    this.userId = userId;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getIdType() {
    return idType;
  }

  public void setIdType(String idType) {
    this.idType = idType;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getLastUpdatedBy() {
    return lastUpdatedBy;
  }

  public void setLastUpdatedBy(String lastUpdatedBy) {
    this.lastUpdatedBy = lastUpdatedBy;
  }

  public String getOriginalExternalId() {
    return originalExternalId;
  }

  public void setOriginalExternalId(String originalExternalId) {
    this.originalExternalId = originalExternalId;
  }

  public String getOriginalIdType() {
    return originalIdType;
  }

  public void setOriginalIdType(String originalIdType) {
    this.originalIdType = originalIdType;
  }

  public String getOriginalProvider() {
    return originalProvider;
  }

  public void setOriginalProvider(String originalProvider) {
    this.originalProvider = originalProvider;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  public Date getLastUpdatedOn() {
    return lastUpdatedOn;
  }

  public void setLastUpdatedOn(Date lastUpdatedOn) {
    this.lastUpdatedOn = lastUpdatedOn;
  }
}
