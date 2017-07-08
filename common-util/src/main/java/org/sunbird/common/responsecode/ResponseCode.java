package org.sunbird.common.responsecode;


/**
 * 
 * @author Manzarul
 *
 */
public enum ResponseCode {
    unAuthorised(ResponseMessage.Key.UNAUTHORISE_USER, ResponseMessage.Message.UNAUTHORISE_USER),
    invalidUserCredentials(ResponseMessage.Key.INVALID_USER_CREDENTIALS, ResponseMessage.Message.INVALID_USER_CREDENTIALS),
    invalidOperationName(ResponseMessage.Key.INVALID_OPERATION_NAME, ResponseMessage.Message.INVALID_OPERATION_NAME),
    invalidRequestData(ResponseMessage.Key.INVALID_REQUESTED_DATA, ResponseMessage.Message.INVALID_REQUESTED_DATA),
    invalidCustomerId(ResponseMessage.Key.COMSUMER_ID_MISSING_ERROR, ResponseMessage.Message.COMSUMER_ID_MISSING_ERROR),
    customerIdRequired(ResponseMessage.Key.COMSUMER_ID_INVALID_ERROR, ResponseMessage.Message.COMSUMER_ID_INVALID_ERROR),
    deviceIdRequired(ResponseMessage.Key.DEVICE_ID_MISSING_ERROR, ResponseMessage.Message.DEVICE_ID_MISSING_ERROR),
    invalidContentId(ResponseMessage.Key.CONTENT_ID_INVALID_ERROR, ResponseMessage.Message.CONTENT_ID_INVALID_ERROR),
    courseIdRequired(ResponseMessage.Key.COURSE_ID_MISSING_ERROR, ResponseMessage.Message.COURSE_ID_MISSING_ERROR),
    contentIdRequired(ResponseMessage.Key.CONTENT_ID_MISSING_ERROR, ResponseMessage.Message.CONTENT_ID_MISSING_ERROR),
    apiKeyRequired(ResponseMessage.Key.API_KEY_MISSING_ERROR, ResponseMessage.Message.API_KEY_MISSING_ERROR),
    invalidApiKey(ResponseMessage.Key.API_KEY_INVALID_ERROR, ResponseMessage.Message.API_KEY_INVALID_ERROR),
	internalError(ResponseMessage.Key.INTERNAL_ERROR, ResponseMessage.Message.INTERNAL_ERROR),
	dbInsertionError(ResponseMessage.Key.DB_INSERTION_FAIL, ResponseMessage.Message.DB_INSERTION_FAIL),
	dbUpdateError(ResponseMessage.Key.DB_UPDATE_FAIL, ResponseMessage.Message.DB_UPDATE_FAIL),
	courseNameRequired(ResponseMessage.Key.COURSE_NAME_MISSING, ResponseMessage.Message.COURSE_NAME_MISSING),
	success(ResponseMessage.Key.SUCCESS_MESSAGE, ResponseMessage.Message.SUCCESS_MESSAGE),
	sessionIdRequiredError (ResponseMessage.Key.SESSION_ID_MISSING, ResponseMessage.Message.SESSION_ID_MISSING),
	courseIdRequiredError (ResponseMessage.Key.COURSE_ID_MISSING, ResponseMessage.Message.COURSE_ID_MISSING),
	contentIdRequiredError (ResponseMessage.Key.CONTENT_ID_MISSING, ResponseMessage.Message.CONTENT_ID_MISSING),
	versionRequiredError (ResponseMessage.Key.VERSION_MISSING, ResponseMessage.Message.VERSION_MISSING),
	courseVersionRequiredError (ResponseMessage.Key.COURSE_VERSION_MISSING, ResponseMessage.Message.COURSE_VERSION_MISSING),
	contentVersionRequiredError (ResponseMessage.Key.CONTENT_VERSION_MISSING, ResponseMessage.Message.CONTENT_VERSION_MISSING),
	courseDescriptionError(ResponseMessage.Key.COURSE_DESCRIPTION_MISSING, ResponseMessage.Message.COURSE_DESCRIPTION_MISSING),
	courseTocUrlError(ResponseMessage.Key.COURSE_TOCURL_MISSING, ResponseMessage.Message.COURSE_TOCURL_MISSING),
	emailRequired(ResponseMessage.Key.EMAIL_MISSING, ResponseMessage.Message.EMAIL_MISSING),
	emailFormatError(ResponseMessage.Key.EMAIL_FORMAT, ResponseMessage.Message.EMAIL_FORMAT),
	firstNameRequired(ResponseMessage.Key.FIRST_NAME_MISSING, ResponseMessage.Message.FIRST_NAME_MISSING),
	languageRequired(ResponseMessage.Key.LANGUAGE_MISSING, ResponseMessage.Message.LANGUAGE_MISSING),
	passwordRequired(ResponseMessage.Key.PASSWORD_MISSING, ResponseMessage.Message.PASSWORD_MISSING),
	passwordMinLengthError(ResponseMessage.Key.PASSWORD_MIN_LENGHT, ResponseMessage.Message.PASSWORD_MIN_LENGHT),
	passwordMaxLengthError(ResponseMessage.Key.PASSWORD_MAX_LENGHT, ResponseMessage.Message.PASSWORD_MAX_LENGHT),
	organisationIdRequiredError (ResponseMessage.Key.ORGANISATION_ID_MISSING, ResponseMessage.Message.ORGANISATION_ID_MISSING),
	organisationNameRequired(ResponseMessage.Key.ORGANISATION_NAME_MISSING, ResponseMessage.Message.ORGANISATION_NAME_MISSING),
	enrollmentStartDateRequiredError(ResponseMessage.Key.ENROLLMENT_START_DATE_MISSING, ResponseMessage.Message.ENROLLMENT_START_DATE_MISSING),
	courseDurationRequiredError(ResponseMessage.Key.COURSE_DURATION_MISSING, ResponseMessage.Message.COURSE_DURATION_MISSING),
	loginTypeRequired(ResponseMessage.Key.LOGIN_TYPE_MISSING, ResponseMessage.Message.LOGIN_TYPE_MISSING),
	emailAlreadyExistError(ResponseMessage.Key.EMAIL_IN_USE, ResponseMessage.Message.EMAIL_IN_USE),
	invalidCredentials(ResponseMessage.Key.INVALID_CREDENTIAL, ResponseMessage.Message.INVALID_CREDENTIAL),
	userNameRequired(ResponseMessage.Key.USERNAME_MISSING, ResponseMessage.Message.USERNAME_MISSING),
	userNameAlreadyExistError(ResponseMessage.Key.USERNAME_IN_USE, ResponseMessage.Message.USERNAME_IN_USE),
	userIdRequired(ResponseMessage.Key.USERID_MISSING, ResponseMessage.Message.USERID_MISSING),
	msgIdRequiredError (ResponseMessage.Key.MESSAGE_ID_MISSING, ResponseMessage.Message.MESSAGE_ID_MISSING),
	userNameCanntBeUpdated(ResponseMessage.Key.USERNAME_CANNOT_BE_UPDATED, ResponseMessage.Message.USERNAME_CANNOT_BE_UPDATED),
	authTokenRequired(ResponseMessage.Key.AUTH_TOKEN_MISSING, ResponseMessage.Message.AUTH_TOKEN_MISSING),
	invalidAuthToken(ResponseMessage.Key.INVALID_AUTH_TOKEN, ResponseMessage.Message.INVALID_AUTH_TOKEN),
	timeStampRequired(ResponseMessage.Key.TIMESTAMP_REQUIRED, ResponseMessage.Message.TIMESTAMP_REQUIRED),
	publishedCourseCanNotBeUpdated(ResponseMessage.Key.PUBLISHED_COURSE_CAN_NOT_UPDATED, ResponseMessage.Message.PUBLISHED_COURSE_CAN_NOT_UPDATED),
	sourceRequired(ResponseMessage.Key.SOURCE_MISSING, ResponseMessage.Message.SOURCE_MISSING),
	sectionNameRequired(ResponseMessage.Key.SECTION_NAME_MISSING, ResponseMessage.Message.SECTION_NAME_MISSING),
	sectionDataTypeRequired(ResponseMessage.Key.SECTION_DATA_TYPE_MISSING, ResponseMessage.Message.SECTION_DATA_TYPE_MISSING),
	sectionIdRequired(ResponseMessage.Key.SECTION_ID_REQUIRED, ResponseMessage.Message.SECTION_ID_REQUIRED),
	pageNameRequired(ResponseMessage.Key.PAGE_NAME_REQUIRED, ResponseMessage.Message.PAGE_NAME_REQUIRED),
	pageIdRequired(ResponseMessage.Key.PAGE_ID_REQUIRED, ResponseMessage.Message.PAGE_ID_REQUIRED),
	invaidConfiguration(ResponseMessage.Key.INVALID_CONFIGURATION, ResponseMessage.Message.INVALID_CONFIGURATION),
	assessmentItemIdRequired(ResponseMessage.Key.ASSESSMENT_ITEM_ID_REQUIRED, ResponseMessage.Message.ASSESSMENT_ITEM_ID_REQUIRED),
	assessmentTypeRequired (ResponseMessage.Key.ASSESSMENT_TYPE_REQUIRED, ResponseMessage.Message.ASSESSMENT_TYPE_REQUIRED),
	assessmentAttemptDateRequired(ResponseMessage.Key.ATTEMPTED_DATE_REQUIRED, ResponseMessage.Message.ATTEMPTED_DATE_REQUIRED),
	assessmentAnswersRequired(ResponseMessage.Key.ATTEMPTED_ANSWERS_REQUIRED, ResponseMessage.Message.ATTEMPTED_ANSWERS_REQUIRED),
	assessmentmaxScoreRequired(ResponseMessage.Key.MAX_SCORE_REQUIRED, ResponseMessage.Message.MAX_SCORE_REQUIRED),
	statusCanntBeUpdated(ResponseMessage.Key.STATUS_CANNOT_BE_UPDATED, ResponseMessage.Message.STATUS_CANNOT_BE_UPDATED),
	attemptIdRequired(ResponseMessage.Key.ATTEMPT_ID_MISSING_ERROR, ResponseMessage.Message.ATTEMPT_ID_MISSING_ERROR),
	emailANDUserNameAlreadyExistError(ResponseMessage.Key.USERNAME_EMAIL_IN_USE, ResponseMessage.Message.USERNAME_EMAIL_IN_USE),
	keyCloakDefaultError(ResponseMessage.Key.KEY_CLOAK_DEFAULT_ERROR, ResponseMessage.Message.KEY_CLOAK_DEFAULT_ERROR),
	userRegUnSuccessfull(ResponseMessage.Key.USER_REG_UNSUCCESSFULL, ResponseMessage.Message.USER_REG_UNSUCCESSFULL),
	userUpdationUnSuccessfull(ResponseMessage.Key.USER_UPDATE_UNSUCCESSFULL, ResponseMessage.Message.USER_UPDATE_UNSUCCESSFULL),
	loginTypeError(ResponseMessage.Key.LOGIN_TYPE_ERROR, ResponseMessage.Message.LOGIN_TYPE_ERROR),
	invalidOrgId(ResponseMessage.Key.INVALID_ORG_ID , ResponseMessage.Key.INVALID_ORG_ID),
	invalidOrgStatus(ResponseMessage.Key.INVALID_ORG_STATUS ,ResponseMessage.Key.INVALID_ORG_STATUS ),
	invalidOrgStatusTransition(ResponseMessage.Key.INVALID_ORG_STATUS_TRANSITION , ResponseMessage.Key.INVALID_ORG_STATUS_TRANSITION),
	addressRequired(ResponseMessage.Key.ADDRESS_REQUIRED_ERROR, ResponseMessage.Message.ADDRESS_REQUIRED_ERROR),
	educationRequired(ResponseMessage.Key.EDUCATION_REQUIRED_ERROR, ResponseMessage.Message.EDUCATION_REQUIRED_ERROR),
	jobDetailsRequired(ResponseMessage.Key.JOBDETAILS_REQUIRED_ERROR, ResponseMessage.Message.JOBDETAILS_REQUIRED_ERROR),
	dataAlreadyExist(ResponseMessage.Key.DATA_ALREADY_EXIST, ResponseMessage.Message.DATA_ALREADY_EXIST),
	invalidData(ResponseMessage.Key.INVALID_DATA, ResponseMessage.Message.INVALID_DATA),
	invalidCourseId(ResponseMessage.Key.INVALID_COURSE_ID, ResponseMessage.Message.INVALID_OPERATION_NAME),
	orgIdRequired(ResponseMessage.Key.ORG_ID_MISSING, ResponseMessage.Message.ORG_ID_MISSING),
	OK(200), CLIENT_ERROR(400), SERVER_ERROR(500), RESOURCE_NOT_FOUND(404);
	
	private int responseCode;
	/**
     * error code contains String value
     */
    private String errorCode;
    /**
     * errorMessage contains proper error message.
     */
    private String errorMessage;

    /**
     * @param errorCode String
     * @param errorMessage String
     */
    private ResponseCode(String errorCode, String errorMessage) {
	this.errorCode = errorCode;
	this.errorMessage = errorMessage;
    }

    /**
     * 
     * @param errorCode
     * @return
     */
    public String getMessage(int errorCode) {
	return "";
    }

    /**
     * @return
     */
    public String getErrorCode() {
	return errorCode;
    }

    /**
     * @param errorCode
     */
    public void setErrorCode(String errorCode) {
	this.errorCode = errorCode;
    }

    /**
     * @return
     */
    public String getErrorMessage() {
	return errorMessage;
    }

    /**
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    /**
     * This method will provide status message based on code
     * 
     * @param code
     * @return String
     */
    public static String getResponseMessage(String code) {
	String value = "";
	ResponseCode responseCodes[] = ResponseCode.values();
	for (ResponseCode actionState : responseCodes) {
	    if (actionState.getErrorCode().equals(code)) {
		value = actionState.getErrorMessage();
	    }
	}
	return value;
    }
    
    private ResponseCode(int responseCode) {
    	this.responseCode = responseCode;
    }

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
    
	/**
	 * This method will take header response code as int value and 
	 * it provide matched enum value, if code is not matched or exception occurs
	 * then it will provide SERVER_ERROR
	 * @param code int
	 * @return HeaderResponseCode
	 */
	public static ResponseCode getHeaderResponseCode(int code) {
		if (code > 0) {
			try {
				ResponseCode[] arr = ResponseCode.values();
				if (null != arr) {
					for (ResponseCode rc : arr) {
						if (rc.getResponseCode() == code)
							return rc;
					}
				}
			} catch (Exception e) {
				return ResponseCode.SERVER_ERROR;
			}
		}
		return ResponseCode.SERVER_ERROR;
	}
	
	
	/**
     * This method will provide ResponseCode enum based on error code
     * 
     * @param code
     * @return String
     */
    public static ResponseCode getResponse(String errorCode) {
    	ResponseCode value =null;
	ResponseCode responseCodes[] = ResponseCode.values();
	for (ResponseCode response : responseCodes) {
	    if (response.getErrorCode().equals(errorCode)) {
		   return  value = response ;
		  
	    }
	}
	return value;
    }
}
