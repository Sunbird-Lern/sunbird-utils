package org.sunbird.models.user.org;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserOrg implements Serializable {

  private static final long serialVersionUID = 1L;
  private String id;
  private String addedby;
  private String addedbyname;
  private String approvaldate;
  private String approvedby;
  private String hashtagid;
  private boolean isapproved;
  private boolean isdeleted;
  private boolean isrejected;
  private String organisationid;
  private String orgjoindate;
  private String orgleftdate;
  private String position;
  private List<String> roles;
  private String updatedby;
  private String updateddate;
  private String userid;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAddedby() {
    return addedby;
  }

  public void setAddedby(String addedby) {
    this.addedby = addedby;
  }

  public String getAddedbyname() {
    return addedbyname;
  }

  public void setAddedbyname(String addedbyname) {
    this.addedbyname = addedbyname;
  }

  public String getApprovaldate() {
    return approvaldate;
  }

  public void setApprovaldate(String approvaldate) {
    this.approvaldate = approvaldate;
  }

  public String getApprovedby() {
    return approvedby;
  }

  public void setApprovedby(String approvedby) {
    this.approvedby = approvedby;
  }

  public String getHashtagid() {
    return hashtagid;
  }

  public void setHashtagid(String hashtagid) {
    this.hashtagid = hashtagid;
  }

  public boolean isIsapproved() {
    return isapproved;
  }

  public void setIsapproved(boolean isapproved) {
    this.isapproved = isapproved;
  }

  public boolean isIsdeleted() {
    return isdeleted;
  }

  public void setIsdeleted(boolean isdeleted) {
    this.isdeleted = isdeleted;
  }

  public boolean isIsrejected() {
    return isrejected;
  }

  public void setIsrejected(boolean isrejected) {
    this.isrejected = isrejected;
  }

  public String getOrganisationid() {
    return organisationid;
  }

  public void setOrganisationid(String organisationid) {
    this.organisationid = organisationid;
  }

  public String getOrgjoindate() {
    return orgjoindate;
  }

  public void setOrgjoindate(String orgjoindate) {
    this.orgjoindate = orgjoindate;
  }

  public String getOrgleftdate() {
    return orgleftdate;
  }

  public void setOrgleftdate(String orgleftdate) {
    this.orgleftdate = orgleftdate;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public String getUpdatedby() {
    return updatedby;
  }

  public void setUpdatedby(String updatedby) {
    this.updatedby = updatedby;
  }

  public String getUpdateddate() {
    return updateddate;
  }

  public void setUpdateddate(String updateddate) {
    this.updateddate = updateddate;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }
}
