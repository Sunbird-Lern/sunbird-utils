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
		}
	}
	
	/**
	 * This method will do content state request data
	 * validation. if all mandatory data is coming then it won't do any thing
	 * if any mandatory data is missing then it will throw exception.
	 * @param Request contentRequestDto
	 */
	public static void validateUpdateContent(Request contentRequestDto) {
		if (ProjectUtil.isStringNullOREmpty(contentRequestDto.getParams().getDid())) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.deviceIdRequired.getErrorCode(),
					ResponseCode.deviceIdRequired.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		} else if (ProjectUtil.isStringNullOREmpty(contentRequestDto.getParams().getSid())) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.sessionIdRequiredError.getErrorCode(),
					ResponseCode.sessionIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}else if (((List<Map<String,Object>>)(contentRequestDto.getRequest().get(JsonKey.CONTENTS))).size()== 0 ) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
					ResponseCode.contentIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
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
}
