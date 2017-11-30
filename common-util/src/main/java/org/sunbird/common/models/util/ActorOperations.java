package org.sunbird.common.models.util;

/**
 * This enum will contains different operation
 *  for a learner {addCourse, getCourse, update , getContent}
 * @author Manzarul
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
    ORG_CREATION_METRICS_DATA("orgCreationMetricsData"), 
    ORG_CONSUMPTION_METRICS_DATA("orgConsumptionMetricsData"),
    COURSE_PROGRESS_METRICS("courseProgressMetrics"), COURSE_CREATION_METRICS("courseConsumptionMetrics"), 
    USER_CREATION_METRICS("userCreationMetrics"), USER_CONSUMPTION_METRICS("userConsumptionMetrics"),
    GET_COURSE_BATCH_DETAIL("getCourseBatchDetail"),UPDATE_USER_ORG_ES("updateUserOrgES"),
    REMOVE_USER_ORG_ES("removeUserOrgES"),UPDATE_USER_ROLES_ES("updateUserRoles"),
    SYNC("sync"),INSERT_USR_COURSES_INFO_ELASTIC("insertUserCoursesInfoToElastic"),
    UPDATE_USR_COURSES_INFO_ELASTIC("updateUserCoursesInfoToElastic"),SCHEDULE_BULK_UPLOAD("scheduleBulkUpload"),
    COURSE_PROGRESS_METRICS_REPORT("courseProgressMetricsReport"), COURSE_CREATION_METRICS_REPORT("courseConsumptionMetricsReport"),
    ORG_CREATION_METRICS_REPORT("orgCreationMetricsReport"),ORG_CONSUMPTION_METRICS_REPORT("orgConsumptionMetricsReport"),
    EMAIL_SERVICE("emailService"),FILE_STORAGE_SERVICE("fileStorageService"),GET_ALL_BADGE("getAllBadge"),ADD_USER_BADGE("addUserBadge"),
    ADD_USER_BADGE_BKG("addUserBadgebackground"),FILE_GENERATION_AND_UPLOAD("fileGenerationAndUpload"),HEALTH_CHECK("healthCheck"),
    SEND_MAIL("sendMail"), PROCESS_DATA("processData"), ACTOR("actor"),CASSANDRA("cassandra"),ES("es"),EKSTEP("ekstep"),
    COURSE_PROGRESS_METRICS_DATA("courseProgressMetricsData"),GET_ORG_TYPE_LIST("getOrgTypeList"),CREATE_ORG_TYPE("createOrgType"),
    UPDATE_ORG_TYPE("updateOrgType"), CREATE_NOTE("createNote"), UPDATE_NOTE("updateNote"),
    SEARCH_NOTE("searchNote"), GET_NOTE("getNote"), DELETE_NOTE("deleteNote"),
    INSERT_USER_NOTES_ES("insertUserNotesToElastic"),ENCRYPT_USER_DATA("encryptUserData"),DECRYPT_USER_DATA("decryptUserData"), 
    UPDATE_USER_NOTES_ES("updateUserNotesToElastic"),USER_CURRENT_LOGIN("userCurrentLogin"), GET_MEDIA_TYPES("getMediaTypes"),
    SEARCH_AUDIT_LOG("searchAuditLog"), PROCESS_AUDIT_LOG("processAuditLog"),FORGOT_PASSWORD("forgotpassword"),
    ADD_SKILL("addSkill"), GET_SKILL("getSkill") , GET_SKILLS_LIST("getSkillsList"),PROFILE_VISIBILITY("profileVisibility"),CREATE_TENANT_PREFERENCE("createTanentPreference"),
    UPDATE_TENANT_PREFERENCE("updateTenantPreference"),GET_TENANT_PREFERENCE("getTenantPreference"),
    UPDATE_TC_STATUS_OF_USER("updateTCStatusOfUser"), REGISTER_CLIENT("registerClient"), UPDATE_CLIENT_KEY("updateClientKey"), GET_CLIENT_KEY("getClientKey"),CREATE_GEO_LOCATION("createGeoLocation"),
    GET_GEO_LOCATION("getGeoLocation"),
    UPDATE_GEO_LOCATION("updateGeoLocation"),
    DELETE_GEO_LOCATION("deleteGeoLocation"),
    SEND_NOTIFICATION("sendNotification"), SYNC_KEYCLOAK("syncKeycloak"), UPDATE_SYSTEM_SETTINGS("updateSystemSettings");

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