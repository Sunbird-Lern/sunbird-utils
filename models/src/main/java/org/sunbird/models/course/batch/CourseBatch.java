package org.sunbird.models.course.batch;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CourseBatch implements Serializable {

  private static final long serialVersionUID = 1L;
  private String id;
  private String countdecrementdate;
  private boolean countdecrementstatus;
  private String countincrementdate;
  private boolean countincrementstatus;
  private Map<String, String> courseadditionalinfo;
  private String coursecreator;
  private String courseid;
  private String createdby;
  private String createddate;
  private List<String> createdfor;
  private String description;
  private String enddate;
  private String enrollmenttype;
  private String hashtagid;
  private List<String> mentors;
  private String name;
  private Map<String, Boolean> participant;
  private String startdate;
  private int status;
  private String updateddate;

  public CourseBatch() {}

  public CourseBatch(
      String id,
      String countdecrementdate,
      boolean countdecrementstatus,
      String countincrementdate,
      boolean countincrementstatus,
      Map<String, String> courseadditionalinfo,
      String coursecreator,
      String courseid,
      String createdby,
      String createddate,
      List<String> createdfor,
      String description,
      String enddate,
      String enrollmenttype,
      String hashtagid,
      List<String> mentors,
      String name,
      Map<String, Boolean> participant,
      String startdate,
      int status,
      String updateddate) {
    super();
    this.id = id;
    this.countdecrementdate = countdecrementdate;
    this.countdecrementstatus = countdecrementstatus;
    this.countincrementdate = countincrementdate;
    this.countincrementstatus = countincrementstatus;
    this.courseadditionalinfo = courseadditionalinfo;
    this.coursecreator = coursecreator;
    this.courseid = courseid;
    this.createdby = createdby;
    this.createddate = createddate;
    this.createdfor = createdfor;
    this.description = description;
    this.enddate = enddate;
    this.enrollmenttype = enrollmenttype;
    this.hashtagid = hashtagid;
    this.mentors = mentors;
    this.name = name;
    this.participant = participant;
    this.startdate = startdate;
    this.status = status;
    this.updateddate = updateddate;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCountdecrementdate() {
    return countdecrementdate;
  }

  public void setCountdecrementdate(String countdecrementdate) {
    this.countdecrementdate = countdecrementdate;
  }

  public boolean isCountdecrementstatus() {
    return countdecrementstatus;
  }

  public void setCountdecrementstatus(boolean countdecrementstatus) {
    this.countdecrementstatus = countdecrementstatus;
  }

  public String getCountincrementdate() {
    return countincrementdate;
  }

  public void setCountincrementdate(String countincrementdate) {
    this.countincrementdate = countincrementdate;
  }

  public boolean isCountincrementstatus() {
    return countincrementstatus;
  }

  public void setCountincrementstatus(boolean countincrementstatus) {
    this.countincrementstatus = countincrementstatus;
  }

  public Map<String, String> getCourseadditionalinfo() {
    return courseadditionalinfo;
  }

  public void setCourseadditionalinfo(Map<String, String> courseadditionalinfo) {
    this.courseadditionalinfo = courseadditionalinfo;
  }

  public String getCoursecreator() {
    return coursecreator;
  }

  public void setCoursecreator(String coursecreator) {
    this.coursecreator = coursecreator;
  }

  public String getCourseid() {
    return courseid;
  }

  public void setCourseid(String courseid) {
    this.courseid = courseid;
  }

  public String getCreatedby() {
    return createdby;
  }

  public void setCreatedby(String createdby) {
    this.createdby = createdby;
  }

  public String getCreateddate() {
    return createddate;
  }

  public void setCreateddate(String createddate) {
    this.createddate = createddate;
  }

  public List<String> getCreatedfor() {
    return createdfor;
  }

  public void setCreatedfor(List<String> createdfor) {
    this.createdfor = createdfor;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getEnddate() {
    return enddate;
  }

  public void setEnddate(String enddate) {
    this.enddate = enddate;
  }

  public String getEnrollmenttype() {
    return enrollmenttype;
  }

  public void setEnrollmenttype(String enrollmenttype) {
    this.enrollmenttype = enrollmenttype;
  }

  public String getHashtagid() {
    return hashtagid;
  }

  public void setHashtagid(String hashtagid) {
    this.hashtagid = hashtagid;
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

  public String getStartdate() {
    return startdate;
  }

  public void setStartdate(String startdate) {
    this.startdate = startdate;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getUpdateddate() {
    return updateddate;
  }

  public void setUpdateddate(String updateddate) {
    this.updateddate = updateddate;
  }
}
