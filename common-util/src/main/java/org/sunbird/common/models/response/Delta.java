package org.sunbird.common.models.response;

import java.io.Serializable;

/**
 * This will content details information for a course.
 * 
 * @author Manzarul
 *
 */
public class Delta implements Serializable {

  private static final long serialVersionUID = -1771624289282630137L;
  private String beforeContentId;
  private String afterContentId;
  private String contentId;
  private String date;
  private String name;
  private String description;

  /**
   * the beforeContentId
   * @return String 
   */
  public String getBeforeContentId() {
    return beforeContentId;
  }

  /**
   * @param beforeContentId String
   */
  public void setBeforeContentId(String beforeContentId) {
    this.beforeContentId = beforeContentId;
  }

  /**
   * the afterContnetId
   * @return  String
   */
  public String getAfterContnetId() {
    return afterContentId;
  }

  /**
   * the afterContnetId to set
   * @param afterContnetId Stirng
   */
  public void setAfterContnetId(String afterContnetId) {
    this.afterContentId = afterContnetId;
  }

  /**
   * the contentId
   * @return  String
   */
  public String getContentId() {
    return contentId;
  }

  /**
   * the contentId to set
   * @param contentId String
   */
  public void setContentId(String contentId) {
    this.contentId = contentId;
  }

  /**
   * the date
   * @return String
   */
  public String getDate() {
    return date;
  }

  /**
   * the date to set
   * @param date String
   */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   * the name
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   * the name to set
   * @param name String
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * the description
   * @return  String
   */
  public String getDescription() {
    return description;
  }

  /**
   * the description to set
   * @param description String
   */
  public void setDescription(String description) {
    this.description = description;
  }
}
