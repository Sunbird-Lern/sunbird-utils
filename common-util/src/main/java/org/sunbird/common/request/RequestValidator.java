package org.sunbird.common.request;

import java.util.List;
import java.util.Map;

import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * This call will do validation
 * for all incoming request data.
 * @author Manzarul
 * @author Amit Kumar
 */
public final class RequestValidator {
    
	/**
	 * This method will do course enrollment request data
	 * validation. if all mandatory data is coming then it won't do any thing
	 * if any mandatory data is missing then it will throw exception.
	 * @param courseRequestDto CourseRequestDto
	 */
	public static void validateEnrollCourse(Request courseRequestDto) {
		if (courseRequestDto.getRequest().get(JsonKey.COURSE_ID) == null) {
			ProjectCommonException dataException = new ProjectCommonException(
					ResponseCode.courseIdRequiredError.getErrorCode(),
					ResponseCode.courseIdRequiredError.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
	}
	
	/**
	 * This method will do content state request data
	 * validation. if all mandatory data is coming then it won't do any thing
	 * if any mandatory data is missing then it will throw exception.
	 * @param Request contentRequestDto
	 */
	@SuppressWarnings("unchecked")
	public static void validateUpdateContent(Request contentRequestDto) {
			if (((List<Map<String,Object>>)(contentRequestDto.getRequest().get(JsonKey.CONTENTS))).size()== 0 ) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
					ResponseCode.contentIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
			}else{
				List<Map<String,Object>> list= (List<Map<String,Object>>)(contentRequestDto.getRequest().get(JsonKey.CONTENTS));
				for(Map<String,Object> map :list){
					if(map.containsKey(JsonKey.CONTENT_ID) && map.containsKey(JsonKey.CONTENT_VERSION)){
						
						if(null == map.get(JsonKey.CONTENT_ID)){
							ProjectCommonException dataException = new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
									ResponseCode.contentIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
							throw dataException;
						}
						if(null == map.get(JsonKey.CONTENT_VERSION)){
							ProjectCommonException dataException = new ProjectCommonException(ResponseCode.contentVersionRequiredError.getErrorCode(),
									ResponseCode.contentVersionRequiredError.getErrorMessage(),ResponseCode.contentVersionRequiredError.getResponseCode());
							throw dataException;
						}
						
					}else{
						ProjectCommonException dataException = new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
								ResponseCode.contentIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
						throw dataException;
					}
				}
			}
	}
	
	/**
	 * This method will do get course request data
	 * validation. if all mandatory data is coming then it won't do any thing
	 * if any mandatory data is missing then it will throw exception.
	 * @param Request contentRequestDto
	 */
	public static void validateGetData(Request request) {
	}
	
	/**
	 * This method will validate create user data.
	 * @param userRequest Request
	 */
	public static void validateCreateUser(Request userRequest) {
		if (userRequest.getRequest().get(JsonKey.EMAIL) == null) {
			throw new ProjectCommonException(ResponseCode.emailRequired.getErrorCode(),
					ResponseCode.emailRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} else if (!ProjectUtil.isEmailvalid((String) userRequest.getRequest().get(JsonKey.EMAIL))) {
			throw new ProjectCommonException(ResponseCode.emailFormatError.getErrorCode(),
					ResponseCode.emailFormatError.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} else if (userRequest.getRequest().get(JsonKey.FIRST_NAME) == null
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.FIRST_NAME)))) {
			throw new ProjectCommonException(ResponseCode.firstNameRequired.getErrorCode(),
					ResponseCode.firstNameRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} else if (userRequest.getRequest().get(JsonKey.PASSWORD) == null
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PASSWORD)))) {
			throw new ProjectCommonException(ResponseCode.passwordRequired.getErrorCode(),
					ResponseCode.passwordRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} else if (userRequest.getRequest().get(JsonKey.LANGUAGE) == null
				|| ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.LANGUAGE))) {
			throw new ProjectCommonException(ResponseCode.languageRequired.getErrorCode(),
					ResponseCode.languageRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}if (userRequest.getRequest().get(JsonKey.USERNAME) == null) {
			throw new ProjectCommonException(ResponseCode.userNameRequired.getErrorCode(),
					ResponseCode.userNameRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}else if (ProjectUtil.isStringNullOREmpty(userRequest.getParams().getMsgid())) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.msgIdRequiredError.getErrorCode(),
					ResponseCode.msgIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
	}
	
	
	
	/**
	 * This method will validate update user data.
	 * @param userRequest Request
	 */
	public static void validateUpdateUser(Request userRequest) {
   }

	/**
	 * This method will validate user login data.
	 * @param userRequest Request
	 */
	public static void validateUserLogin(Request userRequest) {
		if (userRequest.getRequest().get(JsonKey.USERNAME) == null) {
			throw new ProjectCommonException(ResponseCode.userNameRequired.getErrorCode(),
					ResponseCode.userNameRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} else if (userRequest.getRequest().get(JsonKey.PASSWORD) == null
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PASSWORD)))) {
			throw new ProjectCommonException(ResponseCode.passwordRequired.getErrorCode(),
					ResponseCode.passwordRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
	}
	
	/**
	 * This method will validate change password requested data.
	 * @param userRequest Request
	 */
	public static void validateChangePassword(Request userRequest) {
	    if (userRequest.getRequest().get(JsonKey.PASSWORD) == null
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PASSWORD)))) {
			throw new ProjectCommonException(ResponseCode.passwordRequired.getErrorCode(),
					ResponseCode.passwordRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} else if (userRequest.getRequest().get(JsonKey.NEW_PASSWORD) == null
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.NEW_PASSWORD)))) {
			throw new ProjectCommonException(ResponseCode.passwordRequired.getErrorCode(),
					ResponseCode.passwordRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
	}
	
	
	/**
	 * This method will validate add course request data.
	 * @param userRequest Request
	 */
	public static void validateAddCourse(Request courseRequest) {

		 if (courseRequest.getRequest().get(JsonKey.CONTENT_ID) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseIdRequiredError.getErrorCode(),
					ResponseCode.courseIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}else if (courseRequest.getRequest().get(JsonKey.COURSE_NAME) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseNameRequired.getErrorCode(),
					ResponseCode.courseNameRequired.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}else if (courseRequest.getRequest().get(JsonKey.ORGANISATION_ID) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.organisationIdRequiredError.getErrorCode(),
					ResponseCode.organisationIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}else if (courseRequest.getRequest().get(JsonKey.ENROLLMENT_START_DATE) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.enrollmentStartDateRequiredError.getErrorCode(),
					ResponseCode.enrollmentStartDateRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}else if (courseRequest.getRequest().get(JsonKey.COURSE_DURATION) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseDurationRequiredError.getErrorCode(),
					ResponseCode.courseDurationRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
	
	}
	
	/**
	 * This method will validate update course request data.
	 * @param userRequest Request
	 */
	public static void validateUpdateCourse(Request request) {

		if (request.getRequest().get(JsonKey.COURSE_ID) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseIdRequired.getErrorCode(),
					ResponseCode.courseIdRequired.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}

	}
	
	/**
	 * This method will validate published course request data.
	 * @param userRequest Request
	 */
	public static void validatePublishCourse(Request request) {
		 if (request.getRequest().get(JsonKey.COURSE_ID) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseIdRequiredError.getErrorCode(),
					ResponseCode.courseIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
	}
	
	/**
	 * This method will validate published course request data.
	 * @param userRequest Request
	 */
	public static void validateSearchCourse(Request userRequest) {
		
	}
	
	/**
	 * This method will validate Delete course request data.
	 * @param userRequest Request
	 */
	public static void validateDeleteCourse(Request request) {
		 if (request.getRequest().get(JsonKey.COURSE_ID) == null) {
			 ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseIdRequiredError.getErrorCode(),
					 ResponseCode.courseIdRequiredError.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
			 throw dataException;
		 }
	}
	
}
