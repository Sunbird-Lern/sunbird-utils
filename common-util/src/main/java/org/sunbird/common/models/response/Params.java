package org.sunbird.common.models.response;

import java.io.Serializable;

/**
 * Common response parameter bean
 * @author Manzarul
 *
 */
public class Params implements Serializable{
	private static final long serialVersionUID = -8786004970726124473L;
	private String resmsgid;
	private String msgid;
	private String err;
	private String status;
	private String errmsg;
	/**
	 * @return the resmsgid
	 */
	public String getResmsgid() {
		return resmsgid;
	}
	/**
	 * @param resmsgid the resmsgid to set
	 */
	public void setResmsgid(String resmsgid) {
		this.resmsgid = resmsgid;
	}
	/**
	 * @return the msgid
	 */
	public String getMsgid() {
		return msgid;
	}
	/**
	 * @param msgid the msgid to set
	 */
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	/**
	 * @return the err
	 */
	public String getErr() {
		return err;
	}
	/**
	 * @param err the err to set
	 */
	public void setErr(String err) {
		this.err = err;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the errmsg
	 */
	public String getErrmsg() {
		return errmsg;
	}
	/**
	 * @param errmsg the errmsg to set
	 */
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	
	
	
}
