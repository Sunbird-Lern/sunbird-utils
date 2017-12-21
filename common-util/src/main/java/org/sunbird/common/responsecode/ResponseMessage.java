package org.sunbird.common.responsecode;

/**
 * This interface will hold all the response key and message
 * @author Manzarul
 *
 */
public interface ResponseMessage {
  
    interface Message {
      
        String UNAUTHORISE_USER = "You are not authorized.";
        String INVALID_USER_CREDENTIALS = "Please check your credentials";
        String INVALID_OPERATION_NAME = "Operation name is invalid.Please provide a valid operation name";
        String INVALID_REQUESTED_DATA = "Requested data for this operation is not valid.";
        String CONSUMER_ID_MISSING_ERROR = "Consumer id is mandatory.";
        String CONSUMER_ID_INVALID_ERROR = "Consumer id is invalid.";
        String DEVICE_ID_MISSING_ERROR = "Device id is mandatory.";
        String CONTENT_ID_INVALID_ERROR = "Please provide a valid content id.";
        String CONTENT_ID_MISSING_ERROR = "Please provide content id.";
        String COURSE_ID_MISSING_ERROR = "Please provide course id.";
        String API_KEY_MISSING_ERROR = "APi key is mandatory.";
        String API_KEY_INVALID_ERROR = "APi key is invalid.";
        String INTERNAL_ERROR = "Process failed,please try again later.";
        String COURSE_NAME_MISSING = "Please provide the course name.";
        String SUCCESS_MESSAGE = "Success";
        String SESSION_ID_MISSING = "Session id is mandatory.";
        String COURSE_ID_MISSING = "Course id is mandatory.";
        String CONTENT_ID_MISSING = "Content id is mandatory.";
        String VERSION_MISSING = "Version is mandatory.";
        String COURSE_VERSION_MISSING = "Course version is mandatory.";
        String CONTENT_VERSION_MISSING = "Content version is mandatory.";
        String COURSE_DESCRIPTION_MISSING = "Description is mandatory.";
        String COURSE_TOCURL_MISSING = "Course tocurl is mandatory.";
        String EMAIL_MISSING = "Email is mandatory.";
        String EMAIL_FORMAT = "Email is invalid.";
        String FIRST_NAME_MISSING = "First name is mandatory.";
        String LANGUAGE_MISSING = "Language is mandatory.";
        String PASSWORD_MISSING = "Password is mandatory.";
        String PASSWORD_MIN_LENGHT = "Password should have at least 8 character.";
        String PASSWORD_MAX_LENGHT = "Password should not be more than 12 character.";
        String ORGANISATION_ID_MISSING = "Organization id is mandatory.";
        String REQUIRED_DATA_ORG_MISSING = "Organization Id or Provider with External Id values are required for the operation";
        String ORGANISATION_NAME_MISSING = "organization name is mandatory.";
        String CHANNEL_SHOULD_BE_UNIQUE = "Channel value already used by another organization. Provide different value for channel";
        String INVALID_ORG_DATA = "Given Organization Data doesn't exist in our records. Please provide a valid one";
        String INVALID_USR_DATA = "Given User Data doesn't exist in our records. Please provide a valid one";
        String USR_DATA_VALIDATION_ERROR = "Please provide valid userId or userName";
        String INVALID_ROOT_ORGANIZATION = "Root organization id is invalid";
        String INVALID_PARENT_ORGANIZATION_ID = "Parent organization id is invalid";
        String CYCLIC_VALIDATION_FAILURE = "The relation cannot be created as it is cyclic";
        String CHANNEL_MISSING = "Channel is mandatory for root organization";
        String ENROLLMENT_START_DATE_MISSING = "Enrollment start date is mandatory.";
        String COURSE_DURATION_MISSING = "Course duration is mandatory.";
        String LOGIN_TYPE_MISSING = "Login type is required.";
        String EMAIL_IN_USE = "Email already exists.";
        String USERNAME_EMAIL_IN_USE = "Username or Email is already in used. Please try with a different Username or Email.";
        String KEY_CLOAK_DEFAULT_ERROR = "server error at sso.";
        String USER_REG_UNSUCCESSFULL = "User Registration unsuccessfull.";
        String USER_UPDATE_UNSUCCESSFULL = "User update operation is unsuccessfull.";
        String INVALID_CREDENTIAL = "Invalid credential.";
        String USERNAME_MISSING = "Username is mandatory.";
        String USERNAME_IN_USE = "Username already exists.";
        String USERID_MISSING = "UserId is mandatory.";
        String ROLE_MISSING = "Role of the user is required";
        String MESSAGE_ID_MISSING = "Message id is mandatory.";
        String USERNAME_CANNOT_BE_UPDATED = "UserName cann't be updated.";
        String AUTH_TOKEN_MISSING = "Auth token is mandatory.";
        String INVALID_AUTH_TOKEN = "Auth token is invalid.Please login again.";
        String TIMESTAMP_REQUIRED = "TimeStamp is required.";
        String PUBLISHED_COURSE_CAN_NOT_UPDATED ="Published course can't be updated.";
        String SOURCE_MISSING = "Source is required.";
        String SECTION_NAME_MISSING = "Section name is required.";
        String SECTION_DATA_TYPE_MISSING = "Section data type missing.";
        String SECTION_ID_REQUIRED = "Section id is required.";
        String PAGE_NAME_REQUIRED = "Page name is required.";
        String PAGE_ID_REQUIRED = "Page id is required.";
        String INVALID_CONFIGURATION ="Invalid configuration data.";
        String ASSESSMENT_ITEM_ID_REQUIRED = "Assessment item id is required.";
        String ASSESSMENT_TYPE_REQUIRED = "Assessment type is required.";
        String ATTEMPTED_DATE_REQUIRED = "Attempted data is required.";
        String ATTEMPTED_ANSWERS_REQUIRED = "Attempted answers is required.";
        String MAX_SCORE_REQUIRED = "Max score is required.";
        String STATUS_CANNOT_BE_UPDATED = "status cann't be updated.";
        String ATTEMPT_ID_MISSING_ERROR = "Please provide attempt id.";
        String LOGIN_TYPE_ERROR = "provide login type as null.";
		String INVALID_ORG_ID ="Org id does not exist .";
		String INVALID_ORG_STATUS = "Invalid org status for approve .";
		String INVALID_ORG_STATUS_TRANSITION = "Can not change state of Org to requeted state .";
        String ADDRESS_REQUIRED_ERROR = "Please provide address.";
        String EDUCATION_REQUIRED_ERROR = "Please provide education details.";
        String JOBDETAILS_REQUIRED_ERROR = "Please provide job details.";
		String DB_INSERTION_FAIL = "DB insert operation failed.";
		String DB_UPDATE_FAIL = "Db update operation failed.";
		String DATA_ALREADY_EXIST = "data already exist.";
		String INVALID_DATA = "Incorrect data.";
		String INVALID_COURSE_ID = "Course doesnot exist. Please provide a valid course identifier";
		String PHONE_NO_REQUIRED_ERROR = "Phone number is required.";
		String ORG_ID_MISSING ="Organization Id required.";
		String ACTOR_CONNECTION_ERROR = "Service is not able to connect with actor.";
		String USER_ALREADY_EXIST = "user already exist.";
		String PAGE_ALREADY_EXIST = "page already exist with this Page Name and Org Code.";
		String INVALID_USER_ID = "User Id does not exists in our records";
		String LOGIN_ID_MISSING = "loginId is required.";
		String CONTENT_STATUS_MISSING_ERROR = "content status is required .";
		String ES_ERROR = "Something went wrong when processing data for search";
		String INVALID_PERIOD = "Time Period is invalid";
		String USER_NOT_FOUND = "user not found.";
		String ID_REQUIRED_ERROR = "For deleting a record, Id is required.";
		String DATA_TYPE_ERROR = "{0} data type should be of {1}.";
		String ADDRESS_ERROR = "In {0}, {1} is mandatory.";
		String ADDRESS_TYPE_ERROR = "Please provide correct address Type.";
		String NAME_OF_INSTITUTION_ERROR = "Please provide name of Institution.";
		String EDUCATION_DEGREE_ERROR = "Education degree is required.";
		String JOB_NAME_ERROR = "Job Name is required.";
		String NAME_OF_ORGANISATION_ERROR = "Organization Name is required.";
		String ROLES_MISSING = "user role is required.";
        String INVALID_DATE_FORMAT = "Invalid Date format . Date format should be : yyyy-MM-dd hh:mm:ss:SSSZ";
        String SRC_EXTERNAL_ID_ALREADY_EXIST = "PROVIDER WITH EXTERNAL ID ALREADY EXIST .";
        String ALREADY_ENROLLED_COURSE = "User has already Enrolled this course .";
        String EXISTING_ORG_MEMBER ="You already have a membership of this organization.";
        String CONTENT_TYPE_ERROR = "Please add Content-Type header with value application/json";
        String INVALID_PROPERTY_ERROR = "invalid property {0}.";
        String USER_NAME_OR_ID_ERROR = "Please provide either username or userId.";
        String USER_ACCOUNT_BLOCKED = "User account has been blocked .";
        String EMAIL_VERIFY_ERROR = "Please provide a verified email in order to create user.";
        String PHONE_VERIFY_ERROR = "Please provide a verified phone number in order to create user.";
        String BULK_USER_UPLOAD_ERROR = "Please provide either organization Id or external Id & provider value.";
        String DATA_SIZE_EXCEEDED = "Maximum upload data size should be {0}";
        String INVALID_COLUMN_NAME = "Invalid column name.";
        String USER_ALREADY_ACTIVE = "User is already active";
        String ENROLMENT_TYPE_REQUIRED = "Enrolment type is mandatory.";
        String ENROLMENT_TYPE_VALUE_ERROR = "EnrolmentType value must be either open or invite-only.";
        String COURSE_BATCH_START_DATE_REQUIRED = "batch start date is mandatory.";
        String COURSE_BATCH_START_DATE_INVALID = "batch start date should be either today or future date.";
        String DATE_FORMAT_ERRROR = "Date format error.";
        String END_DATE_ERROR = "end date should be greater than start date.";
        String INVALID_CSV_FILE = "Please provide valid csv file.";
      String INVALID_COURSE_BATCH_ID = "Invalid course batch id ";
      String COURSE_BATCH_ID_MISSING="Course batch Id required";
      String ENROLLMENT_TYPE_VALIDATION = "Enrollment type should be invite-only.";
      String USER_NOT_BELONGS_TO_ANY_ORG ="User does not belongs to any org .";
      String INVALID_OBJECT_TYPE ="Invalid Object Type.";
      String INVALID_PROGRESS_STATUS = "Progress status value should be NOT_STARTED(0), STARTED(1), COMPLETED(2).";
      String COURSE_CREATED_FOR_NULL = "Batch does not belong to any organization .";
      String COURSE_BATCH_START_PASSED_DATE_INVALID = "This Batch already started.";
      String UNABLE_TO_CONNECT_TO_EKSTEP = "Unable to connect to Ekstep Server";
      String UNABLE_TO_CONNECT_TO_ES = "Unable to connect to Elastic Search";
      String UNABLE_TO_PARSE_DATA = "Unable to parse the data";
      String INVALID_JSON = "Unable to process object to JSON/ JSON to Object";
      String EMPTY_CSV_FILE = "CSV file is Empty.";
      String INVALID_ROOT_ORG_DATA = "Root org doesn't exist for this Organization Id and channel {0}";
      String NO_DATA = "No sufficient data for fetching the results";
      String INVALID_CHANNEL = "Channel value is invalid.";
      String INVALID_PROCESS_ID = "Invalid Process Id.";
      String EMAIL_SUBJECT_ERROR = "Email Subject is mandatory.";
      String EMAIL_BODY_ERROR = "Email Body is mandatory.";
      String RECIPIENT_ADDRESS_ERROR = "Please send recipientEmails or recipientUserIds.";
      String STORAGE_CONTAINER_NAME_MANDATORY = " Container name can not be null or empty.";
      String CLOUD_SERVICE_ERROR = "Cloud storage service error.";
      String BADGE_TYPE_ID_ERROR = "Badge type id is mandatory.";
      String RECEIVER_ID_ERROR = "Receiver id is mandatory.";
      String INVALID_RECEIVER_ID = "Receiver id is invalid.";
      String INVALID_BADGE_ID = "Invalid badge type id.";
      String USER_REG_ORG_ERROR = "this user belongs to some other organization.";
      String INVALID_ROLE = "Invalid role value provided in request.";
      String INVALID_SALT = "Please provide salt value.";
      String ORG_TYPE_MANDATORY = "Org Type name is mandatory.";
      String ORG_TYPE_ALREADY_EXIST = "Org type with this name already exist.Please provide some other name.";
      String ORG_TYPE_ID_REQUIRED_ERROR = "Org Type Id is required.";
      String TITLE_REQUIRED = "Title is required";
      String NOTE_REQUIRED = "No data to store for notes";
      String CONTENT_ID_ERROR = "Please provide content id or course id";
      String INVALID_TAGS = "Invalid data for tags";
      String NOTE_ID_INVALID = "Invalid note id";
      String USER_DATA_ENCRYPTION_ERROR = "Exception Occurred while encrypting user data.";
      String INVALID_PHONE_NO_FORMAT = "Please provide a valid phone number.";
      String INVALID_WEBPAGE_DATA = "Invalid webPage data";
      String INVALID_MEDIA_TYPE = "Invalid media type for webPage";
      String INVALID_WEBPAGE_URL = "Invalid URL for {0}";
      String INVALID_DATE_RANGE = "Date range should be between 3 Month.";
      String INVALID_BATCH_END_DATE_ERROR = "Please provide valid End Date.";
      String INVALID_BATCH_START_DATE_ERROR = "Please provide valid Start Date.";
      String COURSE_BATCH_END_DATE_ERROR = "Batch has been closed.";
      String COURSE_BATCH_IS_CLOSED_ERROR = "Batch has been closed.";
      String CONFIIRM_PASSWORD_MISSING = "Confirm password is mandatory.";
      String CONFIIRM_PASSWORD_EMPTY = "Confirm password can not be empty.";
      String SAME_PASSWORD_ERROR = "new password can't be same as old password.";
      String ENDORSED_USER_ID_REQUIRED =" Endorsed user id required .";
      String CAN_NOT_ENDORSE = "Can not endorse since both belong to different orgs .";
      String INVALID_ORG_TYPE_ID_ERROR = "Please provide valid orgTypeId.";
      String INVALID_ORG_TYPE_ERROR = "Please provide valid orgType.";
      String TABLE_OR_DOC_NAME_ERROR = "Please provide valid table or documentName.";
      String EMAIL_OR_PHONE_MISSING = "Please provide either email or phone.";
      String PHONE_ALREADY_IN_USE = "Phone already in use. Please provide different phone number.";
      String INVALID_CLIENT_NAME = "Please provide unique valid client name";
      String INVALID_CLIENT_ID = "Please provide valid client id";
      String USER_PHONE_UPDATE_FAILED = "user phone update is failed.";
      String ES_UPDATE_FAILED = "Data insertion to ES failed.";
      String UPDATE_FAILED = "Data updation failed due to invalid Request";
      String INVALID_TYPE_VALUE = "Type value should be organisation OR location .";
      String INVALID_LOCATION_ID = "Please provide valid location id.";
      String INVALID_HASHTAG_ID = "Please provide different hashTagId.This HashTagId is associated with some other organization.";
      String INVALID_USR_ORG_DATA = "Given User Data doesn't belongs to this organization. Please provide a valid one.";
      String INVALID_VISIBILITY_REQUEST = "Private and Public fields cannot be same.";
      String INVALID_TOPIC_NAME = "Please provide a valid toipc.";
      String INVALID_TOPIC_DATA = "Please provide valid notification data.";
      String INVALID_NOTIFICATION_TYPE = "Please provide a valid notification type.";
      String INVALID_NOTIFICATION_TYPE_SUPPORT = "Only notification type FCM is supported.";
      String INVALID_PHONE_NUMBER = "Please send Phone and country code seprately.";
      String INVALID_COUNTRY_CODE = "Please provide a valid country code.";
      String DUPLICATE_PHONE_DATA = "System contains duplicate entry for {0}.";
      String DUPLICATE_EMAIL_DATA = "System contains duplicate entry for {0}.";
      String LOCATION_ID_REQUIRED = "Please provide Location Id.";
      String NOT_SUPPORTED = "Not Supported.";
      String USERNAME_USERID_MISSING = "Please provide either userName or userId.";
    }
    
    interface Key {
      
        String UNAUTHORISE_USER = "UNAUTHORIZE_USER";
        String INVALID_USER_CREDENTIALS = "INVALID_USER_CREDENTIALS";
        String INVALID_OPERATION_NAME = "INVALID_OPERATION_NAME";
        String INVALID_REQUESTED_DATA = "INVALID_REQUESTED_DATA";
        String CONSUMER_ID_MISSING_ERROR = "CONSUMER_ID_REQUIRED_ERROR";
        String CONSUMER_ID_INVALID_ERROR = "CONSUMER_ID_INVALID_ERROR";
        String DEVICE_ID_MISSING_ERROR = "DEVICE_ID_REQUIRED_ERROR";
        String CONTENT_ID_INVALID_ERROR = "CONTENT_ID_INVALID_ERROR";
        String CONTENT_ID_MISSING_ERROR = "CONTENT_ID_REQUIRED_ERROR";
        String COURSE_ID_MISSING_ERROR = "COURSE_ID_REQUIRED_ERROR";
        String API_KEY_MISSING_ERROR = "API_KEY_REQUIRED_ERROR";
        String API_KEY_INVALID_ERROR = "API_KEY_INVALID_ERROR";
        String INTERNAL_ERROR = "INTERNAL_ERROR";
        String COURSE_NAME_MISSING = "COURSE_NAME_REQUIRED_ERROR";
        String SUCCESS_MESSAGE = "SUCCESS";
        String SESSION_ID_MISSING = "SESSION_ID_REQUIRED_ERROR";
        String COURSE_ID_MISSING = "COURSE_ID_REQUIRED_ERROR";
        String CONTENT_ID_MISSING ="CONTENT_ID_REQUIRED_ERROR";
        String VERSION_MISSING ="VERSION_REQUIRED_ERROR";
        String COURSE_VERSION_MISSING ="COURSE_VERSION_REQUIRED_ERROR";
        String CONTENT_VERSION_MISSING ="CONTENT_VERSION_REQUIRED_ERROR";
        String COURSE_DESCRIPTION_MISSING = "COURSE_DESCRIPTION_REQUIRED_ERROR";
        String COURSE_TOCURL_MISSING = "COURSE_TOCURL_REQUIRED_ERROR";
        String EMAIL_MISSING = "EMAIL_ID_REQUIRED_ERROR";
        String EMAIL_FORMAT = "EMAIL_FORMAT_ERROR";
        String FIRST_NAME_MISSING = "FIRST_NAME_REQUIRED_ERROR";
        String LANGUAGE_MISSING = "LANGUAGE_REQUIRED_ERROR";
        String PASSWORD_MISSING = "PASSWORD_REQUIRED_ERROR";
        String PASSWORD_MIN_LENGHT = "PASSWORD_MIN_LENGHT_ERROR";
        String PASSWORD_MAX_LENGHT = "PASSWORD_MAX_LENGHT_ERROR";
        String ORGANISATION_ID_MISSING = "ORGANIZATION_ID_MISSING";
        String REQUIRED_DATA_ORG_MISSING = "REQUIRED_DATA_MISSING";
        String ORGANISATION_NAME_MISSING = "ORGANIZATION_NAME_MISSING";
        String CHANNEL_SHOULD_BE_UNIQUE = "CHANNEL_SHOULD_BE_UNIQUE";
        String INVALID_ORG_DATA = "INVALID_ORGANIZATION_DATA";
        String INVALID_USR_DATA = "INVALID_USER_DATA";
        String USR_DATA_VALIDATION_ERROR = "USER_DATA_VALIDATION_ERROR";
        String INVALID_ROOT_ORGANIZATION = "INVALID ROOT ORGANIZATION";
        String INVALID_PARENT_ORGANIZATION_ID = "INVALID_PARENT_ORGANIZATION_ID";
        String CYCLIC_VALIDATION_FAILURE = "CYCLIC_VALIDATION_FAILURE";
        String CHANNEL_MISSING = "CHANNEL_MISSING";
        String ENROLLMENT_START_DATE_MISSING = "ENROLLMENT_START_DATE_MISSING";
        String COURSE_DURATION_MISSING = "COURSE_DURATION_MISSING";
        String LOGIN_TYPE_MISSING = "LOGIN_TYPE_MISSING";
        String EMAIL_IN_USE = "EMAIL_IN_USE";
        String USERNAME_EMAIL_IN_USE = "USERNAME_EMAIL_IN_USE";
        String KEY_CLOAK_DEFAULT_ERROR = "KEY_CLOAK_DEFAULT_ERROR";
        String USER_REG_UNSUCCESSFULL = "USER_REG_UNSUCCESSFULL";
        String USER_UPDATE_UNSUCCESSFULL = "USER_UPDATE_UNSUCCESSFULL";
        String INVALID_CREDENTIAL = "INVALID_CREDENTIAL";
        String USERNAME_MISSING = "USERNAME_MISSING";
        String USERNAME_IN_USE = "USERNAME_IN_USE";
        String USERID_MISSING = "USERID_MISSING";
        String ROLE_MISSING = "ROLE_MISSING";
        String MESSAGE_ID_MISSING = "MESSAGE_ID_MISSING";
        String USERNAME_CANNOT_BE_UPDATED = "USERNAME_CANNOT_BE_UPDATED";
        String AUTH_TOKEN_MISSING = "X_Authenticated_Userid_MISSING";
        String INVALID_AUTH_TOKEN = "INVALID_AUTH_TOKEN";
        String TIMESTAMP_REQUIRED = "TIMESTAMP_REQUIRED";
        String PUBLISHED_COURSE_CAN_NOT_UPDATED = "PUBLISHED_COURSE_CAN_NOT_UPDATED";
        String SOURCE_MISSING = "SOURCE_MISSING";
        String SECTION_NAME_MISSING = "SECTION_NAME_MISSING";
        String SECTION_DATA_TYPE_MISSING = "SECTION_DATA_TYPE_MISSING";
        String SECTION_ID_REQUIRED = "SECTION_ID_REQUIRED";
        String PAGE_NAME_REQUIRED = "PAGE_NAME_REQUIRED";
        String PAGE_ID_REQUIRED = "PAGE_ID_REQUIRED";
        String INVALID_CONFIGURATION ="INVALID_CONFIGURATION";
        String ASSESSMENT_ITEM_ID_REQUIRED = "ASSESSMENT_ITEM_ID_REQUIRED";
        String ASSESSMENT_TYPE_REQUIRED = "ASSESSMENT_TYPE_REQUIRED";
        String ATTEMPTED_DATE_REQUIRED = "ATTEMPTED_DATE_REQUIRED";
        String ATTEMPTED_ANSWERS_REQUIRED = "ATTEMPTED_ANSWERS_REQUIRED";
        String MAX_SCORE_REQUIRED = "MAX_SCORE_REQUIRED";
        String STATUS_CANNOT_BE_UPDATED = "STATUS_CANNOT_BE_UPDATED";
        String ATTEMPT_ID_MISSING_ERROR = "ATTEMPT_ID_REQUIRED_ERROR";
        String LOGIN_TYPE_ERROR = "LOGIN_TYPE_ERROR";
		String INVALID_ORG_ID = "INVALID_ORG_ID";
		String INVALID_ORG_STATUS = "INVALID_ORG_STATUS";
		String INVALID_ORG_STATUS_TRANSITION = "INVALID_ORG_STATUS_TRANSITION";
		String ADDRESS_REQUIRED_ERROR = "ADDRESS_REQUIRED_ERROR";
		String EDUCATION_REQUIRED_ERROR = "EDUCATION_REQUIRED_ERROR";
		String JOBDETAILS_REQUIRED_ERROR = "JOBDETAILS_REQUIRED_ERROR";
		String DB_INSERTION_FAIL = "DB_INSERTION_FAIL";
		String DB_UPDATE_FAIL = "DB_UPDATE_FAIL";
		String DATA_ALREADY_EXIST = "DATA_ALREADY_EXIST";
		String INVALID_DATA = "INVALID_DATA";
		String INVALID_COURSE_ID = "INVALID_COURSE_ID";
		String PHONE_NO_REQUIRED_ERROR = "PHONE_NO_REQUIRED_ERROR";
		String ORG_ID_MISSING = "ORG_ID_MISSING";
		String ACTOR_CONNECTION_ERROR = "ACTOR_CONNECTION_ERROR";
		String USER_ALREADY_EXIST = "USER_ALREADY_EXIST";
		String PAGE_ALREADY_EXIST = "PAGE_ALREADY_EXIST";
		String INVALID_USER_ID = "INVALID_USER_ID";
		String LOGIN_ID_MISSING = "LOGIN_ID_MISSING";
		String CONTENT_STATUS_MISSING_ERROR = "CONTENT_STATUS_MISSING_ERROR";
		String ES_ERROR = "ELASTICSEARCH_ERROR";
		String INVALID_PERIOD = "INVALID_PERIOD";
		String USER_NOT_FOUND = "USER_NOT_FOUND";
		String ID_REQUIRED_ERROR = "ID_REQUIRED_ERROR";
		String DATA_TYPE_ERROR = "DATA_TYPE_ERROR";
		String ADDRESS_ERROR = "ADDRESS_ERROR";
		String ADDRESS_TYPE_ERROR = "ADDRESS_TYPE_ERROR";
		String NAME_OF_INSTITUTION_ERROR = "NAME_OF_INSTITUTION_ERROR";
		String EDUCATION_DEGREE_ERROR = "EDUCATION_DEGREE_ERROR";
		String JOB_NAME_ERROR = "JOB_NAME_ERROR";
		String NAME_OF_ORGANISATION_ERROR = "NAME_OF_ORGANIZATION_ERROR";
		String ROLES_MISSING = "ROLES_REQUIRED_ERROR";
		String INVALID_DATE_FORMAT = "INVALID_DATE_FORMAT";
		String SRC_EXTERNAL_ID_ALREADY_EXIST = "SRC_EXTERNAL_ID_ALREADY_EXIST";
		String ALREADY_ENROLLED_COURSE = "ALREADY_ENROLLED_COURSE";
		String CONTENT_TYPE_ERROR = "CONTENT_TYPE_ERROR";
		String INVALID_PROPERTY_ERROR = "INVALID_PROPERTY_ERROR";
		String USER_NAME_OR_ID_ERROR = "USER_NAME_OR_ID_ERROR";
		String USER_ACCOUNT_BLOCKED = "USER_ACCOUNT_BLOCKED";
		String EMAIL_VERIFY_ERROR = "EMAIL_VERIFY_ERROR";
		String PHONE_VERIFY_ERROR = "PHONE_VERIFY_ERROR";
		String BULK_USER_UPLOAD_ERROR = "BULK_USER_UPLOAD_ERROR";
		String DATA_SIZE_EXCEEDED = "DATA_SIZE_EXCEEDED";
		String INVALID_COLUMN_NAME = "INVALID_COLUMN_NAME";
		String USER_ALREADY_ACTIVE = "USER_ALREADY_ACTIVE";
		String ENROLMENT_TYPE_REQUIRED = "ENROLMENT_TYPE_REQUIRED";
		String ENROLMENT_TYPE_VALUE_ERROR = "ENROLMENT_TYPE_VALUE_ERROR";
		String COURSE_BATCH_START_DATE_REQUIRED = "COURSE_BATCH_START_DATE_REQUIRED";
		String COURSE_BATCH_START_DATE_INVALID = "COURSE_BATCH_START_DATE_INVALID";
		String DATE_FORMAT_ERRROR = "DATE_FORMAT_ERRROR";
		String END_DATE_ERROR = "END_DATE_ERROR";
		String INVALID_CSV_FILE = "INVALID_CSV_FILE";
		String INVALID_COURSE_BATCH_ID = "INVALID_COURSE_BATCH_ID";
		String COURSE_BATCH_ID_MISSING = "COURSE_BATCH_ID_MISSING";
		String ENROLLMENT_TYPE_VALIDATION = "ENROLLMENT_TYPE_VALIDATION";
		String COURSE_CREATED_FOR_NULL = "COURSE_CREATED_FOR_NULL";
		String USER_NOT_BELONGS_TO_ANY_ORG ="USER_NOT_BELONGS_TO_ANY_ORG";
		String INVALID_OBJECT_TYPE ="INVALID_OBJECT_TYPE";
		String INVALID_PROGRESS_STATUS = "INVALID_PROGRESS_STATUS";
		String COURSE_BATCH_START_PASSED_DATE_INVALID = "COURSE_BATCH_START_PASSED_DATE_INVALID";
		String UNABLE_TO_CONNECT_TO_EKSTEP = "UNABLE_TO_CONNECT_TO_EKSTEP";
		String UNABLE_TO_CONNECT_TO_ES = "UNABLE_TO_CONNECT_TO_ES";
		String UNABLE_TO_PARSE_DATA = "UNABLE_TO_PARSE_DATA";
		String INVALID_JSON = "INVALID_JSON";
		String EMPTY_CSV_FILE = "EMPTY_CSV_FILE";
		String INVALID_ROOT_ORG_DATA = "INVALID_ROOT_ORG_DATA";
		String NO_DATA = "NO_DATA";
		String INVALID_CHANNEL = "INVALID_CHANNEL";
		String INVALID_PROCESS_ID = "INVALID_PROCESS_ID";
		String EMAIL_SUBJECT_ERROR = "EMAIL_SUBJECT_ERROR";
		String EMAIL_BODY_ERROR = "EMAIL_BODY_ERROR";
		String RECIPIENT_ADDRESS_ERROR = "RECIPIENT_ADDRESS_ERROR";
        String STORAGE_CONTAINER_NAME_MANDATORY = "STORAGE_CONTAINER_NAME_MANDATORY";
        String USER_REG_ORG_ERROR = "USER_REG_ORG_ERROR";
        String CLOUD_SERVICE_ERROR = "CLOUD_SERVICE_ERROR";
        String BADGE_TYPE_ID_ERROR = "BADGE_TYPE_ID_ERROR";
        String RECEIVER_ID_ERROR = "RECEIVER_ID_ERROR";
        String INVALID_RECEIVER_ID = "INVALID_RECEIVER_ID";
        String INVALID_BADGE_ID = "INVALID_BADGE_ID";
        String INVALID_ROLE = "INVALID_ROLE";
        String INVALID_SALT = "INVALID_SALT";
        String ORG_TYPE_MANDATORY = "ORG_TYPE_MANDATORY";
        String ORG_TYPE_ALREADY_EXIST = "ORG_TYPE_ALREADY_EXIST";
        String ORG_TYPE_ID_REQUIRED_ERROR = "ORG_TYPE_ID_REQUIRED_ERROR";
        String TITLE_REQUIRED = "TITLE_REQUIRED";
        String NOTE_REQUIRED = "NOTE_REQUIRED";
        String CONTENT_ID_ERROR = "CONTENT_ID_OR_COURSE_ID_REQUIRED";
        String INVALID_TAGS = "INVALID_TAGS";
        String NOTE_ID_INVALID = "NOTE_ID_INVALID";
        String USER_DATA_ENCRYPTION_ERROR = "USER_DATA_ENCRYPTION_ERROR";
        String INVALID_PHONE_NO_FORMAT = "INVALID_PHONE_NO_FORMAT";
        String INVALID_WEBPAGE_DATA = "INVALID_WEBPAGE_DATA";
        String INVALID_MEDIA_TYPE = "INVALID_MEDIA_TYPE";
        String INVALID_WEBPAGE_URL = "INVALID_WEBPAGE_URL";
        String INVALID_DATE_RANGE = "INVALID_DATE_RANGE";
        String INVALID_BATCH_END_DATE_ERROR = "INVALID_BATCH_END_DATE_ERROR";
        String INVALID_BATCH_START_DATE_ERROR = "INVALID_BATCH_START_DATE_ERROR";
        String COURSE_BATCH_END_DATE_ERROR = "COURSE_BATCH_END_DATE_ERROR";
        String COURSE_BATCH_IS_CLOSED_ERROR = "COURSE_BATCH_IS_CLOSED_ERROR";
        String CONFIIRM_PASSWORD_MISSING = "CONFIIRM_PASSWORD_MISSING";
        String CONFIIRM_PASSWORD_EMPTY = "CONFIIRM_PASSWORD_EMPTY";
        String SAME_PASSWORD_ERROR = "SAME_PASSWORD_ERROR";
        String ENDORSED_USER_ID_REQUIRED="ENDORSED_USER_ID_REQUIRED";
        String CAN_NOT_ENDORSE = "CAN_NOT_ENDORSE";
        String INVALID_ORG_TYPE_ID_ERROR = "INVALID_ORG_TYPE_ID_ERROR";
        String INVALID_ORG_TYPE_ERROR = "INVALID_ORG_TYPE_ERROR";
        String TABLE_OR_DOC_NAME_ERROR = "TABLE_OR_DOC_NAME_ERROR";
        String EMAIL_OR_PHONE_MISSING = "EMAIL_OR_PHONE_MISSING";
        String PHONE_ALREADY_IN_USE = "PHONE_ALREADY_IN_USE";
        String INVALID_CLIENT_NAME = "INVALID_CLIENT_NAME";
        String INVALID_CLIENT_ID = "INVALID_CLIENT_ID";
        String USER_PHONE_UPDATE_FAILED = "USER_PHONE_UPDATE_FAILED";
        String ES_UPDATE_FAILED = "ES_UPDATE_FAILED";
        String UPDATE_FAILED = "UPDATE_FAILED";
        String INVALID_TYPE_VALUE = "INVALID_TYPE_VALUE";
        String INVALID_LOCATION_ID = "INVALID_LOCATION_ID";
        String INVALID_HASHTAG_ID = "INVALID_HASHTAG_ID";
        String INVALID_USR_ORG_DATA = "INVALID_USR_ORG_DATA";
        String INVALID_VISIBILITY_REQUEST = "INVALID_VISIBILITY_REQUEST";
        String INVALID_TOPIC_NAME = "INVALID_TOPIC_NAME";
        String INVALID_TOPIC_DATA = "INVALID_TOPIC_DATA";
        String INVALID_NOTIFICATION_TYPE = "INVALID_NOTIFICATION_TYPE";
        String INVALID_NOTIFICATION_TYPE_SUPPORT = "INVALID_NOTIFICATION_TYPE_SUPPORT";
        String INVALID_PHONE_NUMBER = "INVALID_PHONE_NUMBER";
        String INVALID_COUNTRY_CODE = "INVALID_COUNTRY_CODE";
        String DUPLICATE_PHONE_DATA = "DUPLICATE_PHONE_DATA";
        String DUPLICATE_EMAIL_DATA = "DUPLICATE_EMAIL_DATA";
        String LOCATION_ID_REQUIRED = "LOCATION_ID_REQUIRED";
        String NOT_SUPPORTED = "NOT_SUPPORTED";
        String USERNAME_USERID_MISSING = "USERNAME_USERID_MISSING";
    }
}
