package org.sunbird.common.responsecode;

/**
 * This interface will hold all the response key and message
 * @author Manzarul
 *
 */
public interface ResponseMessage {
	public interface Message {
		public static final String UNAUTHORISE_USER = "You are not authorise.";
		public static final String INVALID_USER_CREDENTIALS = "Please check your credentials";
		public static final String INVALID_OPERATION_NAME = "OPeration name is invalid.Please provide a valid operation name";
		public static final String INVALID_REQUESTED_DATA = "Requested data for this operation is not valid.";
		public static final String COMSUMER_ID_MISSING_ERROR = "Consumer id is mandatory.";
		public static final String COMSUMER_ID_INVALID_ERROR = "Consumer id is invalid.";
		public static final String DEVICE_ID_MISSING_ERROR = "Device id is mandatory.";
		public static final String CONTENT_ID_INVALID_ERROR = "Please provide a valid content id.";
		public static final String CONTENT_ID_MISSING_ERROR = "Please provide content id.";
		public static final String COURSE_ID_MISSING_ERROR = "Please provide course id.";
		public static final String API_KEY_MISSING_ERROR = "APi key is mandatory.";
		public static final String API_KEY_INVALID_ERROR = "APi key is invalid.";
		public static final String INTERNAL_ERROR = "Process failed,please try after some time.";
		public static final String COURSE_NAME_MISSING = "Please provide the course name.";
		public static final String SUCCESS_MESSAGE = "Success";
		public static final String SESSION_ID_MISSING = "Session id is mandatory.";
		public static final String COURSE_ID_MISSING = "Course id is mandatory.";
		public static final String CONTENT_ID_MISSING = "Content id is mandatory.";
		public static final String VERSION_MISSING = "version is mandatory.";
		public static final String COURSE_VERSION_MISSING = "course version is mandatory.";
		public static final String CONTENT_VERSION_MISSING = "content version is mandatory.";
		public static final String COURSE_DESCRIPTION_MISSING = "Description is mandatory.";
		public static final String COURSE_TOCURL_MISSING = "Course tocurl is mandatory.";
		public static final String EMAIL_MISSING = "Email is mandatory.";
		public static final String EMAIL_FORMAT = "Email is invalid.";
		public static final String FIRST_NAME_MISSING = "First name is mandatory.";
		public static final String LANGUAGE_MISSING = "Language is mandatory.";
		public static final String PASSWORD_MISSING = "Password is mandatory.";
		public static final String PASSWORD_MIN_LENGHT = "Password should have at least 8 character.";
		public static final String PASSWORD_MAX_LENGHT = "Password should not be more than 12 character.";
		public static final String ORGANISATION_ID_MISSING = "organisation id is required";
		public static final String ORGANISATION_NAME_MISSING = "organisation name is missing";
		public static final String ENROLLMENT_START_DATE_MISSING = "enrollment start date is missing";
		public static final String COURSE_DURATION_MISSING = "course duration is missing";
		public static final String LOGIN_TYPE_MISSING = "login type is required.";
		public static final String EMAIL_IN_USE = "Email already exists.";
	}

	public interface Key {
		public static final String UNAUTHORISE_USER = "UNAUTHORISE_USER";
		public static final String INVALID_USER_CREDENTIALS = "INVALID_USER_CREDENTIALS";
		public static final String INVALID_OPERATION_NAME = "INVALID_OPERATION_NAME";
		public static final String INVALID_REQUESTED_DATA = "INVALID_REQUESTED_DATA";
		public static final String COMSUMER_ID_MISSING_ERROR = "COMSUMER_ID_REQUIRED_ERROR";
		public static final String COMSUMER_ID_INVALID_ERROR = "COMSUMER_ID_INVALID_ERROR";
		public static final String DEVICE_ID_MISSING_ERROR = "DEVICE_ID_REQUIRED_ERROR";
		public static final String CONTENT_ID_INVALID_ERROR = "CONTENT_ID_INVALID_ERROR";
		public static final String CONTENT_ID_MISSING_ERROR = "CONTENT_ID_REQUIRED_ERROR";
		public static final String COURSE_ID_MISSING_ERROR = "COURSE_ID_REQUIRED_ERROR";
		public static final String API_KEY_MISSING_ERROR = "API_KEY_REQUIRED_ERROR";
		public static final String API_KEY_INVALID_ERROR = "API_KEY_INVALID_ERROR";
		public static final String INTERNAL_ERROR = "API_KEY_INVALID_ERROR";
		public static final String COURSE_NAME_MISSING = "COURSE_NAME_REQUIRED_ERROR";
		public static final String SUCCESS_MESSAGE = "SUCCESS";
		public static final String SESSION_ID_MISSING = "SESSION_ID_REQUIRED_ERROR";
		public static final String COURSE_ID_MISSING = "COURSE_ID_REQUIRED_ERROR";
		public static final String CONTENT_ID_MISSING ="CONTENT_ID_REQUIRED_ERROR";
		public static final String VERSION_MISSING ="VERSION_REQUIRED_ERROR";
		public static final String COURSE_VERSION_MISSING ="COURSE_VERSION_REQUIRED_ERROR";
		public static final String CONTENT_VERSION_MISSING ="CONTENT_VERSION_REQUIRED_ERROR";
		public static final String COURSE_DESCRIPTION_MISSING = "COURSE_DESCRIPTION_REQUIRED_ERROR";
		public static final String COURSE_TOCURL_MISSING = "COURSE_TOCURL_REQUIRED_ERROR";
		public static final String EMAIL_MISSING = "EMAIL_ID_REQUIRED_ERROR";
		public static final String EMAIL_FORMAT = "EMAIL_FORMAT_ERROR";
		public static final String FIRST_NAME_MISSING = "FIRST_NAME_REQUIRED_ERROR";
		public static final String LANGUAGE_MISSING = "LANGUAGE_REQUIRED_ERROR";
		public static final String PASSWORD_MISSING = "PASSWORD_REQUIRED_ERROR";
		public static final String PASSWORD_MIN_LENGHT = "PASSWORD_MIN_LENGHT_ERROR";
		public static final String PASSWORD_MAX_LENGHT = "PASSWORD_MAX_LENGHT_ERROR";
		public static final String ORGANISATION_ID_MISSING = "ORGANISATION_ID_MISSING";
		public static final String ORGANISATION_NAME_MISSING = "ORGANISATION_NAME_MISSING";
		public static final String ENROLLMENT_START_DATE_MISSING = "ENROLLMENT_START_DATE_MISSING";
		public static final String COURSE_DURATION_MISSING = "COURSE_DURATION_MISSING";
		public static final String LOGIN_TYPE_MISSING = "LOGIN_TYPE_MISSING";
		public static final String EMAIL_IN_USE = "EMAIL_IN_USE";
	}
}

