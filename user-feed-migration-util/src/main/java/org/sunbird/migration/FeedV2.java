package org.sunbird.migration;

import java.sql.Timestamp;
import java.util.Date;

public class FeedV2 {

    private String id;
    private String userid;
    private String category;
    private int priority;
    private String createdby;
    private String status;
    private String action;
    private String updatedby;
    private Timestamp expireon;
    private Timestamp updatedon;
    private Timestamp createdon;
    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUpdatedby() {
        return updatedby;
    }

    public void setUpdatedby(String updatedby) {
        this.updatedby = updatedby;
    }

    public Timestamp getExpireon() {
        return expireon;
    }

    public void setExpireon(Timestamp expireon) {
        this.expireon = expireon;
    }

    public Timestamp getUpdatedon() {
        return updatedon;
    }

    public void setUpdatedon(Timestamp updatedon) {
        this.updatedon = updatedon;
    }

    public Timestamp getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Timestamp createdon) {
        this.createdon = createdon;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
