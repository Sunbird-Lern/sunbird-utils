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
		public static final String CONTENT_ID_MISSING_ERROR = "Please provide at least one content id.";
		public static final String API_KEY_MISSING_ERROR = "APi key is mandatory.";
		public static final String API_KEY_INVALID_ERROR = "APi key is invalid.";
		public static final String INTERNAL_ERROR = "Process failed,please try after some time.";
		public static final String COURSE_NAME_MISSING = "Please provide the course name.";
		public static final String SUCCESS_MESSAGE = "Success";
		public static final String SESSION_ID_MISSING = "Session id is mandatory.";
		public static final String COURSE_ID_MISSING = "Course id is mandatory.";
		public static final String CONTENT_ID_MISSING = "Content id is mandatory.";
		public static final String COURSE_DESCRIPTION_MISSING = "Description is mandatory.";
		public static final String COURSE_TOCURL_MISSING = "Course tocurl is mandatory.";
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
		public static final String API_KEY_MISSING_ERROR = "API_KEY_REQUIRED_ERROR";
		public static final String API_KEY_INVALID_ERROR = "API_KEY_INVALID_ERROR";
		public static final String INTERNAL_ERROR = "API_KEY_INVALID_ERROR";
		public static final String COURSE_NAME_MISSING = "COURSE_NAME_REQUIRED_ERROR";
		public static final String SUCCESS_MESSAGE = "SUCCESS";
		public static final String SESSION_ID_MISSING = "SESSION_ID_REQUIRED_ERROR";
		public static final String COURSE_ID_MISSING = "COURSE_ID_REQUIRED_ERROR";
		public static final String CONTENT_ID_MISSING ="CONTENT_ID_REQUIRED_ERROR";
		public static final String COURSE_DESCRIPTION_MISSING = "COURSE_DESCRIPTION_REQUIRED_ERROR";
		public static final String COURSE_TOCURL_MISSING = "COURSE_TOCURL_REQUIRED_ERROR";
	}
}

