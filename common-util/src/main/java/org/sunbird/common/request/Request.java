package org.sunbird.common.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/** @author Manzarul */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request implements Serializable {

  private static final long serialVersionUID = -2362783406031347676L;

  protected Map<String, Object> context;
  private String id;
  private String ver;
  private String ts;
  private RequestParams params;

  private Map<String, Object> request = new HashMap<>();

  private String managerName;
  private String operation;
  private String requestId;
  private int env;

  public Request() {
    this.params = new RequestParams();
    this.params.setMsgid(requestId);
    init();
  }

  private void init() {
    // Set the context here.
    Map<String, Object> currContext = ExecutionContext.getCurrent().getContextValues();
    context = currContext == null ? new HashMap<>() : new HashMap<>(currContext);
    if (ExecutionContext.getCurrent()
        .getGlobalContext()
        .containsKey(HeaderParam.CURRENT_INVOCATION_PATH.getParamName())) {
      context.put(
          HeaderParam.REQUEST_PATH.getParamName(),
          ExecutionContext.getCurrent()
              .getGlobalContext()
              .get(HeaderParam.CURRENT_INVOCATION_PATH.getParamName()));
    }

    // set request_id
    requestId =
        (String)
            ExecutionContext.getCurrent()
                .getGlobalContext()
                .get(HeaderParam.REQUEST_ID.getParamName());
  }

  public Request(Request request) {
    this.params = request.getParams();
    if (null == this.params) this.params = new RequestParams();
    else if (!StringUtils.isBlank(this.params.getMsgid())) {
      ExecutionContext.setRequestId(this.params.getMsgid());
      this.requestId = this.params.getMsgid();
    }
    if (StringUtils.isBlank(this.params.getMsgid()) && !StringUtils.isBlank(requestId))
      this.params.setMsgid(requestId);
    this.context.putAll(request.getContext());
  }

  public String getRequestId() {
    if (null != this.params) return this.params.getMsgid();
    return requestId;
  }

  public Map<String, Object> getContext() {
    return context;
  }

  public void setContext(Map<String, Object> context) {
    this.context = context;
  }

  /** @return the requestValueObjects */
  public Map<String, Object> getRequest() {
    return request;
  }

  public void setRequest(Map<String, Object> request) {
    this.request = request;
  }

  public Object get(String key) {
    return request.get(key);
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public void put(String key, Object vo) {
    request.put(key, vo);
  }

  public String getManagerName() {
    return managerName;
  }

  public void setManagerName(String managerName) {
    this.managerName = managerName;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public void copyRequestValueObjects(Map<String, Object> map) {
    if (null != map && map.size() > 0) {
      this.request.putAll(map);
    }
  }

  @Override
  public String toString() {
    return "Request ["
        + (context != null ? "context=" + context + ", " : "")
        + (request != null ? "requestValueObjects=" + request : "")
        + "]";
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getVer() {
    return ver;
  }

  public void setVer(String ver) {
    this.ver = ver;
  }

  public String getTs() {
    return ts;
  }

  public void setTs(String ts) {
    this.ts = ts;
  }

  public RequestParams getParams() {
    return params;
  }

  public void setParams(RequestParams params) {
    this.params = params;
    if (this.params.getMsgid() == null && requestId != null) this.params.setMsgid(requestId);
  }

  /** @return the env */
  public int getEnv() {
    return env;
  }

  /** @param env the env to set */
  public void setEnv(int env) {
    this.env = env;
  }
}
