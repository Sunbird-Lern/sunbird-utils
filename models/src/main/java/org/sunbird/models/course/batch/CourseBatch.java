package org.sunbird.models.course.batch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;

public class CourseBatch implements Serializable {

  private static final long serialVersionUID = 1L;
  private String id;
  private String countDecrementDate;
  private boolean countDecrementStatus;
  private String countIncrementDate;
  private boolean countIncrementStatus;
  private Map<String, String> courseAdditionalInfo;
  private String courseCreator;
  private String courseId;
  private String createdBy;
  private String createdDate;
  private List<String> createdFor;
  private String description;
  private String endDate;
  private String enrollmentType;
  private String hashTagId;
  private List<String> mentors;
  private String name;
  private Map<String, Boolean> participant;
  private String startDate;
  private int status;
  private String updatedDate;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCountDecrementDate() {
    return countDecrementDate;
  }

  public void setCountDecrementDate(String countDecrementDate) {
    this.countDecrementDate = countDecrementDate;
  }

  public boolean isCountDecrementStatus() {
    return countDecrementStatus;
  }

  public void setCountDecrementStatus(boolean countDecrementStatus) {
    this.countDecrementStatus = countDecrementStatus;
  }

  public String getCountIncrementDate() {
    return countIncrementDate;
  }

  public void setCountIncrementDate(String countIncrementDate) {
    this.countIncrementDate = countIncrementDate;
  }

  public boolean isCountIncrementStatus() {
    return countIncrementStatus;
  }

  public void setCountIncrementStatus(boolean countIncrementStatus) {
    this.countIncrementStatus = countIncrementStatus;
  }

  public Map<String, String> getCourseAdditionalInfo() {
    return courseAdditionalInfo;
  }

  public void setCourseAdditionalInfo(Map<String, String> courseAdditionalInfo) {
    this.courseAdditionalInfo = courseAdditionalInfo;
  }

  public String getCourseCreator() {
    return courseCreator;
  }

  public void setCourseCreator(String courseCreator) {
    this.courseCreator = courseCreator;
  }

  public String getCourseId() {
    return courseId;
  }

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public List<String> getCreatedFor() {
    return createdFor;
  }

  public void setCreatedFor(List<String> createdFor) {
    this.createdFor = createdFor;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getEnrollmentType() {
    return enrollmentType;
  }

  public void setEnrollmentType(String enrollmentType) {
    this.enrollmentType = enrollmentType;
  }

  public String getHashTagId() {
    return hashTagId;
  }

  public void setHashTagId(String hashTagId) {
    this.hashTagId = hashTagId;
  }

  public List<String> getMentors() {
    return mentors;
  }

  public void setMentors(List<String> mentors) {
    this.mentors = mentors;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Boolean> getParticipant() {
    return participant;
  }

  public void setParticipant(Map<String, Boolean> participant) {
    this.participant = participant;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(String updatedDate) {
    this.updatedDate = updatedDate;
  }

  public CourseBatch(Map<String, Object> request, String createdBy) {
    this.setCountDecrementStatus(false);
    this.setCountIncrementStatus(false);
    this.setCourseId((String) request.get(JsonKey.COURSE_ID));
    this.setCreatedBy(createdBy);
    this.setCreatedDate(ProjectUtil.getFormattedDate());
    this.setCreatedFor((List<String>) request.get(JsonKey.COURSE_CREATED_FOR));
    this.setMentors((List<String>) request.get(JsonKey.MENTORS));
  }

  public void setContentDetails(Map<String, Object> contentDetails) {
    this.setCourseCreator((String) contentDetails.get(JsonKey.CREATED_BY));
    this.setCourseAdditionalInfo(getAdditionalCourseInfo(contentDetails));
  }

  private Map<String, String> getAdditionalCourseInfo(Map<String, Object> contentDetails) {

    Map<String, String> courseMap = new HashMap<>();
    courseMap.put(
        JsonKey.COURSE_LOGO_URL,
        contentDetails.getOrDefault(JsonKey.APP_ICON, "") != null
            ? (String) contentDetails.getOrDefault(JsonKey.APP_ICON, "")
            : "");
    courseMap.put(
        JsonKey.COURSE_NAME,
        contentDetails.getOrDefault(JsonKey.NAME, "") != null
            ? (String) contentDetails.getOrDefault(JsonKey.NAME, "")
            : "");
    courseMap.put(
        JsonKey.DESCRIPTION,
        contentDetails.getOrDefault(JsonKey.DESCRIPTION, "") != null
            ? (String) contentDetails.getOrDefault(JsonKey.DESCRIPTION, "")
            : "");
    courseMap.put(
        JsonKey.TOC_URL,
        contentDetails.getOrDefault("toc_url", "") != null
            ? (String) contentDetails.getOrDefault("toc_url", "")
            : "");
    if (contentDetails.get(JsonKey.LEAF_NODE_COUNT) != null) {
      courseMap.put(
          JsonKey.LEAF_NODE_COUNT, (contentDetails.get(JsonKey.LEAF_NODE_COUNT)).toString());
    }
    courseMap.put(JsonKey.STATUS, (String) contentDetails.getOrDefault(JsonKey.STATUS, ""));
    return courseMap;
  }
}
