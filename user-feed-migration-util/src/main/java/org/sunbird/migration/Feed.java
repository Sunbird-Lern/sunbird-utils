package org.sunbird.migration;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

public class Feed {
    private String id;
    private String userId;
    private String category;
    private int priority;
    private String createdBy;
    private String status;
    private String data;
    private String updatedBy;
    private Timestamp expireOn;
    private Timestamp updatedOn;
    private Timestamp createdOn;
    private String version;

    public Feed(String id, String category, String createdBy, Timestamp createdOn, String data,
                Timestamp expireOn, int priority, String status, String updatedBy, Timestamp updatedOn, String userId) {
      this.id=id;
      this.category=category;
      this.expireOn=expireOn;
      this.createdBy=createdBy;
      this.createdOn=createdOn;
      this.data=data;
      this.status=status;
      this.updatedBy=updatedBy;
      this.updatedOn=updatedOn;
      this.priority=priority;
      this.userId=userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Timestamp getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(Timestamp expireOn) {
        this.expireOn = expireOn;
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }
}