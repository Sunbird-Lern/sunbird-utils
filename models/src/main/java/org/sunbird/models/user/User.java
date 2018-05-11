package org.sunbird.models.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @desc POJO class for User
 * @author Amit Kumar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class User implements Serializable {

  private static final long serialVersionUID = 7529802960267784945L;

  private String id;
  private String avatar;
  private String countryCode;
  private String createdBy;
  private String createdDate;
  private String dob;
  private String email;
  private Boolean emailVerified;
  private String firstName;
  private String gender;
  private List<String> grade;
  private Boolean isDeleted;
  private List<String> language;
  private String lastLoginTime;
  private String lastName;
  private String location;
  private String password;
  private String phone;
  private String profileSummary;
  private Map<String,String> profileVisibility;
  private String provider;
  private List<String> roles;
  private String rootOrgId;
  private Integer status;
  private List<String> subject;
  private String tcStatus;
  private String tcUpdatedAt;
  private String tempPassword;
  private String thumbnail;
  private String updatedBy;
  private String updatedDate;
  private String userId;
  private String userName;
  private List<Map<String,Object>> webPages;
  private String externalId;
  private String channel;
  private String loginId;
  
  
  
  /**
   * @return the loginId
   */
  public String getLoginId() {
    return loginId;
  }
  /**
   * @param loginId the loginId to set
   */
  public void setLoginId(String loginId) {
    this.loginId = loginId;
  }
  /**
   * @return the channel
   */
  public String getChannel() {
    return channel;
  }
  /**
   * @param channel the channel to set
   */
  public void setChannel(String channel) {
    this.channel = channel;
  }
  /**
   * @return the id
   */
  public String getId() {
    return id;
  }
  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }
  /**
   * @return the avatar
   */
  public String getAvatar() {
    return avatar;
  }
  /**
   * @param avatar the avatar to set
   */
  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
  /**
   * @return the countryCode
   */
  public String getCountryCode() {
    return countryCode;
  }
  /**
   * @param countryCode the countryCode to set
   */
  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }
  /**
   * @return the createdBy
   */
  public String getCreatedBy() {
    return createdBy;
  }
  /**
   * @param createdBy the createdBy to set
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }
  /**
   * @return the createdDate
   */
  public String getCreatedDate() {
    return createdDate;
  }
  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }
  /**
   * @return the dob
   */
  public String getDob() {
    return dob;
  }
  /**
   * @param dob the dob to set
   */
  public void setDob(String dob) {
    this.dob = dob;
  }
  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }
  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }
  /**
   * @return the emailVerified
   */
  public Boolean getEmailVerified() {
    return emailVerified;
  }
  /**
   * @param emailVerified the emailVerified to set
   */
  public void setEmailVerified(Boolean emailVerified) {
    this.emailVerified = emailVerified;
  }
  /**
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }
  /**
   * @param firstName the firstName to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  /**
   * @return the gender
   */
  public String getGender() {
    return gender;
  }
  /**
   * @param gender the gender to set
   */
  public void setGender(String gender) {
    this.gender = gender;
  }
  /**
   * @return the grade
   */
  public List<String> getGrade() {
    return grade;
  }
  /**
   * @param grade the grade to set
   */
  public void setGrade(List<String> grade) {
    this.grade = grade;
  }
  /**
   * @return the isDeleted
   */
  @JsonProperty(value = "IsDeleted")
  public Boolean getIsDeleted() {
    return isDeleted;
  }
  /**
   * @param isDeleted the isDeleted to set
   */
  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
  }
  /**
   * @return the language
   */
  public List<String> getLanguage() {
    return language;
  }
  /**
   * @param language the language to set
   */
  public void setLanguage(List<String> language) {
    this.language = language;
  }
  /**
   * @return the lastLoginTime
   */
  public String getLastLoginTime() {
    return lastLoginTime;
  }
  /**
   * @param lastLoginTime the lastLoginTime to set
   */
  public void setLastLoginTime(String lastLoginTime) {
    this.lastLoginTime = lastLoginTime;
  }
  /**
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }
  /**
   * @param lastName the lastName to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  /**
   * @return the location
   */
  public String getLocation() {
    return location;
  }
  /**
   * @param location the location to set
   */
  public void setLocation(String location) {
    this.location = location;
  }
  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }
  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }
  /**
   * @return the phone
   */
  public String getPhone() {
    return phone;
  }
  /**
   * @param phone the phone to set
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }
  /**
   * @return the profileSummary
   */
  public String getProfileSummary() {
    return profileSummary;
  }
  /**
   * @param profileSummary the profileSummary to set
   */
  public void setProfileSummary(String profileSummary) {
    this.profileSummary = profileSummary;
  }
  /**
   * @return the profileVisibility
   */
  public Map<String, String> getProfileVisibility() {
    return profileVisibility;
  }
  /**
   * @param profileVisibility the profileVisibility to set
   */
  public void setProfileVisibility(Map<String, String> profileVisibility) {
    this.profileVisibility = profileVisibility;
  }
  /**
   * @return the provider
   */
  public String getProvider() {
    return provider;
  }
  /**
   * @param provider the provider to set
   */
  public void setProvider(String provider) {
    this.provider = provider;
  }
  /**
   * @return the roles
   */
  public List<String> getRoles() {
    return roles;
  }
  /**
   * @param roles the roles to set
   */
  public void setRoles(List<String> roles) {
    this.roles = roles;
  }
  /**
   * @return the rootOrgId
   */
  public String getRootOrgId() {
    return rootOrgId;
  }
  /**
   * @param rootOrgId the rootOrgId to set
   */
  public void setRootOrgId(String rootOrgId) {
    this.rootOrgId = rootOrgId;
  }
  /**
   * @return the status
   */
  public Integer getStatus() {
    return status;
  }
  /**
   * @param status the status to set
   */
  public void setStatus(Integer status) {
    this.status = status;
  }
  /**
   * @return the subject
   */
  public List<String> getSubject() {
    return subject;
  }
  /**
   * @param subject the subject to set
   */
  public void setSubject(List<String> subject) {
    this.subject = subject;
  }
  /**
   * @return the tcStatus
   */
  public String getTcStatus() {
    return tcStatus;
  }
  /**
   * @param tcStatus the tcStatus to set
   */
  public void setTcStatus(String tcStatus) {
    this.tcStatus = tcStatus;
  }
  /**
   * @return the tcUpdatedAt
   */
  public String getTcUpdatedAt() {
    return tcUpdatedAt;
  }
  /**
   * @param tcUpdatedAt the tcUpdatedAt to set
   */
  public void setTcUpdatedAt(String tcUpdatedAt) {
    this.tcUpdatedAt = tcUpdatedAt;
  }
  /**
   * @return the tempPassword
   */
  public String getTempPassword() {
    return tempPassword;
  }
  /**
   * @param tempPassword the tempPassword to set
   */
  public void setTempPassword(String tempPassword) {
    this.tempPassword = tempPassword;
  }
  /**
   * @return the thumbnail
   */
  public String getThumbnail() {
    return thumbnail;
  }
  /**
   * @param thumbnail the thumbnail to set
   */
  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }
  /**
   * @return the updatedBy
   */
  public String getUpdatedBy() {
    return updatedBy;
  }
  /**
   * @param updatedBy the updatedBy to set
   */
  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }
  /**
   * @return the updatedDate
   */
  public String getUpdatedDate() {
    return updatedDate;
  }
  /**
   * @param updatedDate the updatedDate to set
   */
  public void setUpdatedDate(String updatedDate) {
    this.updatedDate = updatedDate;
  }
  /**
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }
  /**
   * @param userId the userId to set
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }
  /**
   * @return the userName
   */
  public String getUserName() {
    return userName;
  }
  /**
   * @param userName the userName to set
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }
  /**
   * @return the webPages
   */
  public List<Map<String, Object>> getWebPages() {
    return webPages;
  }
  /**
   * @param webPages the webPages to set
   */
  public void setWebPages(List<Map<String, Object>> webPages) {
    this.webPages = webPages;
  }
  /**
   * @return the externalId
   */
  public String getExternalId() {
    return externalId;
  }
  /**
   * @param externalId the externalId to set
   */
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  
}
