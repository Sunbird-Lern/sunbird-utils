package org.sunbird.common.models.util;

/**
 * This enum will contains different operation
 *  for a learner {addCourse, getCourse, update , getContent}
 * @author Manzarul
 *
 */

public enum ActorOperations {
   
    ENROLL_COURSE("enrollCourse"), GET_COURSE("getCourse"), ADD_CONTENT("addContent"),
    GET_CONTENT("getContent"), CREATE_COURSE("createCourse"), UPDATE_COURSE("updateCourse"),
    PUBLISH_COURSE("publishCourse"), SEARCH_COURSE("searchCourse"), DELETE_COURSE("deleteCourse"),
    CREATE_USER("createUser"), UPDATE_USER("updateUser"), LOGIN("login"), LOGOUT("logout"),
    CHANGE_PASSWORD("changePassword"), USER_AUTH("userAuth"), GET_PROFILE("getUserProfile"),
    CREATE_ORG("createOrg"), UPDATE_ORG("updateOrg"), UPDATE_ORG_STATUS("updateOrgStatus"),
    GET_ORG_DETAILS("getOrgDetails"), APPROVE_ORG("approveOrg"), 
    CREATE_PAGE("createPage"),UPDATE_PAGE("updatePage"), DELETE_PAGE("deletePage"), 
    GET_PAGE_SETTINGS("getPageSettings"), GET_PAGE_SETTING("getPageSetting"), 
    GET_PAGE_DATA("getPageData"),
    CREATE_SECTION("createSection"), UPDATE_SECTION("updateSection"),
    GET_ALL_SECTION("getAllSection"), GET_SECTION("getSection"),
    GET_COURSE_BY_ID("getCourseById"), UPDATE_USER_COUNT("updateUserCount"),
    SAVE_ASSESSMENT("saveAssessment"), GET_ASSESSMENT("getAssessment"),
    GET_RECOMMENDED_COURSES("getRecommendedCourses"),
    UPDATE_USER_INFO_ELASTIC("updateUserInfoToElastic"),GET_ROLES("getRoles"),
	APPROVE_ORGANISATION("approveOrganisation"), JOIN_USER_ORGANISATION("joinUserOrganisation"),
	ADD_MEMBER_ORGANISATION("addMemberOrganisation"), REMOVE_MEMBER_ORGANISATION("removeMemberOrganisation"),
	APPROVE_USER_ORGANISATION("approveUserOrganisation"),COMPOSITE_SEARCH("compositeSearch"),
	GET_USER_DETAILS_BY_LOGINID("getUserDetailsByLoginId"),UPDATE_ORG_INFO_ELASTIC("updateOrgInfoToElastic"),
    REJECT_USER_ORGANISATION("rejectUserOrganisation"),INSERT_ORG_INFO_ELASTIC("insertOrgInfoToElastic"),
    DOWNLOAD_USERS("downloadUsersData"),DOWNLOAD_ORGS("downlaodOrg"),BLOCK_USER("blockUser")
    ,DELETE_BY_IDENTIFIER("deleteByIdentifier"),
    BULK_UPLOAD("bulkUpload"),PROCESS_BULK_UPLOAD("processBulkUpload"),ASSIGN_ROLES("assignRoles"), UNBLOCK_USER("unblockUser"),
    CREATE_BATCH("createBatch"),UPDATE_BATCH("updateBatch"),REMOVE_BATCH("removeBatch"),ADD_USER_TO_BATCH("addUserBatch"),
    REMOVE_USER_FROM_BATCH("removeUserFromBatch"),GET_BATCH("getBatch"),INSERT_COURSE_BATCH_ES("insertCourseBatchToEs"),
    UPDATE_COURSE_BATCH_ES("updateCourseBatchToEs"),GET_BULK_OP_STATUS("getBulkOpStatus"),
    ORG_CREATION_METRICS("orgCreationMetrics"),ORG_CONSUMPTION_METRICS("orgConsumptionMetrics"),
    COURSE_PROGRESS_METRICS("courseProgressMetrics"), COURSE_CREATION_METRICS("courseConsumptionMetrics"), 
    USER_CREATION_METRICS("userCreationMetrics"), USER_CONSUMPTION_METRICS("userConsumptionMetrics"),
    GET_COURSE_BATCH_DETAIL("getCourseBatchDetail"),SYNC_DATA("syncData");

    private String value;

    /**
     * constructor
     * @param value String
     */
    ActorOperations(String value){
        this.value=value;
    }

    /**
     * returns the enum value
     * @return String
     */
    public String getValue(){
        return this.value;
    }
}