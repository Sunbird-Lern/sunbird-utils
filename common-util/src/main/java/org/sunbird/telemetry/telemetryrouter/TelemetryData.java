package org.sunbird.telemetry.telemetryrouter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class TelemetryData {

  private String id;
  private String channel;
  private Timestamp ts;
  private String eventData;
  private String pdataId;
  private String pdataVer;
  private String eid;
  private String version;

  Map<String, Object> map = null;

  public TelemetryData() {}

  public TelemetryData(
      String id,
      String channel,
      Timestamp ts,
      String eventData,
      String pdataId,
      String pdataVer,
      String eid,
      String version) {
    map = new HashMap<>();
    this.id = id;
    this.channel = channel;
    this.ts = ts;
    this.eventData = eventData;
    this.pdataId = pdataId;
    this.pdataVer = pdataVer;
    this.eid = eid;
    this.version = version;
  }

  /** @return the pdataVer */
  public String getPdataVer() {
    return pdataVer;
  }

  /** @param pdataVer the pdataVer to set */
  public void setPdataVer(String pdataVer) {
    this.pdataVer = pdataVer;
  }

  /** @return the version */
  public String getVersion() {
    return version;
  }

  /** @param version the version to set */
  public void setVersion(String version) {
    this.version = version;
  }

  /** @return the id */
  public String getId() {
    return id;
  }

  /** @param id the id to set */
  public void setId(String id) {
    this.id = id;
  }

  /** @return the channel */
  public String getChannel() {
    return channel;
  }

  /** @param channel the channel to set */
  public void setChannel(String channel) {
    this.channel = channel;
  }

  /** @return the ts */
  public Timestamp getTs() {
    return ts;
  }

  /** @param ts the ts to set */
  public void setTs(Timestamp ts) {
    this.ts = ts;
  }

  /** @return the eventData */
  public String getEventData() {
    return eventData;
  }

  /** @param eventData the eventData to set */
  public void setEventData(String eventData) {
    this.eventData = eventData;
  }

  /** @return the pdataId */
  public String getPdataId() {
    return pdataId;
  }

  /** @param pdataId the pdataId to set */
  public void setPdataId(String pdataId) {
    this.pdataId = pdataId;
  }

  /** @return the eid */
  public String getEid() {
    return eid;
  }

  /** @param eid the eid to set */
  public void setEid(String eid) {
    this.eid = eid;
  }

  public Map<String, Object> asMap() {
    map.put("id", this.id);
    map.put("channel", this.channel);
    map.put("ts", this.ts);
    map.put("eventData", this.eventData);
    map.put("pdataId", this.pdataId);
    map.put("pdataVer", this.pdataVer);
    map.put("eid", this.eid);
    map.put("version", this.version);
    return map;
  }
}
