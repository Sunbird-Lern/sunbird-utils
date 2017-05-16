package org.sunbird.common.models.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.sunbird.common.responsecode.ResponseCode;


/**
 * This is a common response class for all the layer.
 * All layer will send same response object.
 * @author Manzarul
 *
 */
public class Response implements Serializable {

    private static final long serialVersionUID = -3773253896160786443L;

    private String id;
    private String ver;
    private String ts;
    private ResponseParams params;
    private ResponseCode responseCode = ResponseCode.OK;
    private Map<String, Object> result = new HashMap<String, Object>();
    
    /**
     * This will provide request unique id.
     * @return String
     */
    public String getId() {
        return id;
    }
    /**
     * set the unique id
     * @param id String
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * this will provide api version
     * @return String
     */
    public String getVer() {
        return ver;
    }
    /**
     * set the api version
     * @param ver String
     */
    public void setVer(String ver) {
        this.ver = ver;
    }
    /**
     * this will provide complete time value
     * @return String
     */
    public String getTs() {
        return ts;
    }
    /**
     * set the time value
     * @param ts String
     */
    public void setTs(String ts) {
        this.ts = ts;
    }

    /**
     * @return the responseValueObjects
     */
    public Map<String, Object> getResult() {
        return result;
    }
   /**
    * 
    * @param key
    * @return
    */
    public Object get(String key) {
        return result.get(key);
    }
   /**
    * 
    * @param key
    * @param vo
    */
    public void put(String key, Object vo) {
        result.put(key, vo);
    }
    /**
     * This will provide response parameter object.
     * @return ResponseParams
     */
    public ResponseParams getParams() {
        return params;
    }
   /**
    * set the response parameter object.
    * @param params
    */
    public void setParams(ResponseParams params) {
        this.params = params;
    }
    /**
     * Set the response code for header. 
     * @param code  ResponseCode
     */
    public void setResponseCode(ResponseCode code) {
        this.responseCode = code;
    }
    /**
     * get the response code
     * @return ResponseCode
     */
    public ResponseCode getResponseCode() {
        return this.responseCode;
    }

}
