package org.sunbird.migration;

import java.util.Set;

public class UserGroup {

  private String userId;
  private Set<String> groupIds;

  UserGroup(String userId, Set<String> groupIds) {
    this.userId = userId;
    this.groupIds = groupIds;
  }

  public Set<String> getGroupIds() {
    return groupIds;
  }

  public void setGroupIds(Set<String> groupIds) {
    this.groupIds = groupIds;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
