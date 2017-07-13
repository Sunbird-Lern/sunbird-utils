package org.sunbird.common.models.util;

import java.util.HashMap;
import java.util.Map;

public class LogEvent {
  
  private String eid;
  private long ets;
  private String mid;
  private String ver;
  private Map<String, Object> context;
  private Map<String, Object> edata;

  public String getEid() {
    return eid;
  }

  public void setEid(String eid) {
    this.eid = eid;
  }

  public long getEts() {
    return ets;
  }

  public void setEts(long ets) {
    this.ets = ets;
  }

  public String getMid() {
    return mid;
  }

  public void setMid(String mid) {
    this.mid = mid;
  }

  public String getVer() {
    return ver;
  }

  public void setVer(String ver) {
    this.ver = ver;
  }

  public Map<String, Object> getContext() {
    return context;
  }

  public void setContext(Map<String, Object> context) {
    this.context = context;
  }

  public Map<String, Object> getEdata() {
    return edata;
  }

  public void setEdata(Map<String, Object> eks) {
    this.edata = new HashMap<String, Object>();
    edata.put("eks", eks);
  }

  public void setContext(String id, String ver) {
    this.context = new HashMap<String, Object>();
    Map<String, String> pdata = new HashMap<String, String>();
    pdata.put("id", id);
    pdata.put("ver", ver);
    this.context.put("pdata", pdata);
  }

  public void setEdata(String level, String className, String method, Object data,
      Object stackTrace, Object exception) {
    this.edata = new HashMap<String, Object>();
    Map<String, Object> eks = new HashMap<String, Object>();
    eks.put("level", level);
    eks.put("class", className);
    eks.put("method", method);
    eks.put("data", data);
    eks.put("stacktrace", stackTrace);
    edata.put("eks", eks);
  }

}
