package org.sunbird.migration;

import org.apache.commons.lang3.StringUtils;

public class GroupMember {

  private String userId;
  private String groupId;
  private String role;
  private String status;
  private Boolean visited;

  public GroupMember(String userId, String groupId, String status) {
    this.userId = userId;
    this.groupId = groupId;
    this.status = status;
  }

  public Boolean isVisited() {
    return visited;
  }

  public void setVisited(Boolean visited) {
    this.visited = visited;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    if (StringUtils.isNotBlank(role)) {
      this.role = role;
    }
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    if (StringUtils.isNotBlank(status)) {
      this.status = status;
    }
  }
}
