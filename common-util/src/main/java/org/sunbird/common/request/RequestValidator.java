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
 *
 */
public final class RequestValidator {
    
	/**
	 * This method will do course enrollment request data
	 * validation. if all mandatory data is coming then it won't do any thing
	 * if any mandatory data is missing then it will throw exception.
	 * @param courseRequestDto CourseRequestDto
	 */
	public static void validateCreateCourse(Request courseRequestDto) {
		if (ProjectUtil.isStringNullOREmpty(courseRequestDto.getParams().getDid())) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.deviceIdRequired.getErrorCode(),
					ResponseCode.deviceIdRequired.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		} else if (ProjectUtil.isStringNullOREmpty(courseRequestDto.getParams().getSid())) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.sessionIdRequiredError.getErrorCode(),
					ResponseCode.sessionIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}else if (courseRequestDto.getRequest().get(JsonKey.COURSE_ID) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseIdRequiredError.getErrorCode(),
					ResponseCode.courseIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}else if (courseRequestDto.getRequest().get(JsonKey.COURSE_NAME) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseNameRequired.getErrorCode(),
					ResponseCode.courseNameRequired.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}else if (courseRequestDto.getRequest().get(JsonKey.DESCRIPTION) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseDescriptionError.getErrorCode(),
					ResponseCode.courseDescriptionError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}else if (courseRequestDto.getRequest().get(JsonKey.TOC_URL) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseTocUrlError.getErrorCode(),
					ResponseCode.courseTocUrlError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}else if (courseRequestDto.getRequest().get(JsonKey.VERSION) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.versionRequiredError.getErrorCode(),
					ResponseCode.versionRequiredError.getErrorMessage(),ResponseCode.versionRequiredError.getResponseCode());
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
		if (ProjectUtil.isStringNullOREmpty(contentRequestDto.getParams().getDid())) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.deviceIdRequired.getErrorCode(),
					ResponseCode.deviceIdRequired.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		} else if (ProjectUtil.isStringNullOREmpty(contentRequestDto.getParams().getSid())) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.sessionIdRequiredError.getErrorCode(),
					ResponseCode.sessionIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}else 
			{
			if (((List<Map<String,Object>>)(contentRequestDto.getRequest().get(JsonKey.CONTENTS))).size()== 0 ) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
					ResponseCode.contentIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
			}else{
				List<Map<String,Object>> list= (List<Map<String,Object>>)(contentRequestDto.getRequest().get(JsonKey.CONTENTS));
				for(Map<String,Object> map :list){
					if(map.containsKey(JsonKey.CONTENT_ID) && map.containsKey(JsonKey.VERSION)){
						
						if(null == map.get(JsonKey.CONTENT_ID)){
							ProjectCommonException dataException = new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
									ResponseCode.contentIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
							throw dataException;
						}
						if(null == map.get(JsonKey.VERSION)){
							ProjectCommonException dataException = new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
									ResponseCode.contentIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
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
		
	}
	
	/**
	 * This method will do get course request data
	 * validation. if all mandatory data is coming then it won't do any thing
	 * if any mandatory data is missing then it will throw exception.
	 * @param Request contentRequestDto
	 */
	public static void validateGetData(Request contentRequestDto) {
		if (ProjectUtil.isStringNullOREmpty(contentRequestDto.getParams().getDid())) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.deviceIdRequired.getErrorCode(),
					ResponseCode.deviceIdRequired.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		} else if (ProjectUtil.isStringNullOREmpty(contentRequestDto.getParams().getSid())) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.sessionIdRequiredError.getErrorCode(),
					ResponseCode.sessionIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
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
		}
	}
	
	
	
	/**
	 * This method will validate update user data.
	 * @param userRequest Request
	 */
	public static void validateUpdateUser(Request userRequest) {
		if (ProjectUtil.isStringNullOREmpty(userRequest.getParams().getSid())) {
			throw new ProjectCommonException(ResponseCode.sessionIdRequiredError.getErrorCode(),
					ResponseCode.sessionIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());		
	}
   }

	/**
	 * This method will validate user login data.
	 * @param userRequest Request
	 */
	public static void validateUserLogin(Request userRequest) {
		if (userRequest.getRequest().get(JsonKey.EMAIL) == null) {
			throw new ProjectCommonException(ResponseCode.emailRequired.getErrorCode(),
					ResponseCode.emailRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} else if (!ProjectUtil.isEmailvalid((String) userRequest.getRequest().get(JsonKey.EMAIL))) {
			throw new ProjectCommonException(ResponseCode.emailFormatError.getErrorCode(),
					ResponseCode.emailFormatError.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
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
		if (ProjectUtil.isStringNullOREmpty(userRequest.getParams().getSid())) {
			throw new ProjectCommonException(ResponseCode.sessionIdRequiredError.getErrorCode(),
					ResponseCode.sessionIdRequiredError.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} else if (userRequest.getRequest().get(JsonKey.PASSWORD) == null
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
	public static void validateaddCourse(Request userRequest) {
		
	}
	
	/**
	 * This method will validate update course request data.
	 * @param userRequest Request
	 */
	public static void validateUpdateCourse(Request userRequest) {
		
	}
	
	/**
	 * This method will validate published course request data.
	 * @param userRequest Request
	 */
	public static void validatePublishCourse(Request userRequest) {
		
	}
	
	/**
	 * This method will validate published course request data.
	 * @param userRequest Request
	 */
	public static void validateSearchCourse(Request userRequest) {
		
	}
	
}
