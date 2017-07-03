package org.sunbird.common.models.response;

import java.io.Serializable;

/**
 * This will content details information for 
 * a course.
 * @author Manzarul
 *
 */
public class Delta implements Serializable{
	private static final long serialVersionUID = -1771624289282630137L;
	private String beforeContentId;
	private String afterContnetId;
	private String contentId;
	private String date;
	private String name;
	private String description;
	/**
	 * @return the beforeContentId
	 */
	public String getBeforeContentId() {
		return beforeContentId;
	}
	/**
	 * @param beforeContentId the beforeContentId to set
	 */
	public void setBeforeContentId(String beforeContentId) {
		this.beforeContentId = beforeContentId;
	}
	/**
	 * @return the afterContnetId
	 */
	public String getAfterContnetId() {
		return afterContnetId;
	}
	/**
	 * @param afterContnetId the afterContnetId to set
	 */
	public void setAfterContnetId(String afterContnetId) {
		this.afterContnetId = afterContnetId;
	}
	/**
	 * @return the contentId
	 */
	public String getContentId() {
		return contentId;
	}
	/**
	 * @param contentId the contentId to set
	 */
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
