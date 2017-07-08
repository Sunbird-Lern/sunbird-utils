package org.sunbird.common.request;

import java.util.List;
import java.util.Map;

import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;
import static org.sunbird.common.models.util.ProjectUtil.isNull;


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
	 * @param  contentRequestDto Request
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
					if(map.containsKey(JsonKey.CONTENT_ID)){
						
						if(null == map.get(JsonKey.CONTENT_ID)){
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
	

	/**
	 * This method will validate create user data.
	 * @param userRequest Request
	 */
	@SuppressWarnings("rawtypes")
	public static void validateCreateUser(Request userRequest) {
		if (userRequest.getRequest().get(JsonKey.USERNAME) == null) {
			throw new ProjectCommonException(ResponseCode.userNameRequired.getErrorCode(),
					ResponseCode.userNameRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
		/*if (userRequest.getRequest().get(JsonKey.EMAIL) == null) {
			throw new ProjectCommonException(ResponseCode.emailRequired.getErrorCode(),
					ResponseCode.emailRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}*/
		if (null != userRequest.getRequest().get(JsonKey.EMAIL) && !ProjectUtil.isEmailvalid((String) userRequest.getRequest().get(JsonKey.EMAIL))) {
			throw new ProjectCommonException(ResponseCode.emailFormatError.getErrorCode(),
					ResponseCode.emailFormatError.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
		if (userRequest.getRequest().get(JsonKey.FIRST_NAME) == null
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.FIRST_NAME)))) {
			throw new ProjectCommonException(ResponseCode.firstNameRequired.getErrorCode(),
					ResponseCode.firstNameRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} 
		if (userRequest.getRequest().get(JsonKey.PASSWORD) == null
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PASSWORD)))) {
			throw new ProjectCommonException(ResponseCode.passwordRequired.getErrorCode(),
					ResponseCode.passwordRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} 
		if (userRequest.getRequest().get(JsonKey.LANGUAGE) == null
				|| ((List) userRequest.getRequest().get(JsonKey.LANGUAGE)).isEmpty()) {
			throw new ProjectCommonException(ResponseCode.languageRequired.getErrorCode(),
					ResponseCode.languageRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
		if (ProjectUtil.isStringNullOREmpty(userRequest.getParams().getMsgid())) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.msgIdRequiredError.getErrorCode(),
					ResponseCode.msgIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
	}
	
	public static void validateCreateOrganisation(Request request) {
		if (request.getRequest().get(JsonKey.ORG_NAME) == null) {
			throw new ProjectCommonException(ResponseCode.organisationNameRequired.getErrorCode(),
					ResponseCode.organisationNameRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
	}
	
	
	/**
	 * This method will validate update user data.
	 * @param userRequest Request
	 */
	@SuppressWarnings("rawtypes")
	public static void validateUpdateUser(Request userRequest) {
		if (userRequest.getRequest().containsKey(JsonKey.FIRST_NAME) && (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.FIRST_NAME)))) {
			throw new ProjectCommonException(ResponseCode.firstNameRequired.getErrorCode(),
					ResponseCode.firstNameRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} 
		
		if (userRequest.getRequest().containsKey(JsonKey.EMAIL) && userRequest.getRequest().get(JsonKey.EMAIL) != null) {
		  if(!ProjectUtil.isEmailvalid((String) userRequest.getRequest().get(JsonKey.EMAIL))) {
			throw new ProjectCommonException(ResponseCode.emailFormatError.getErrorCode(),
					ResponseCode.emailFormatError.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		  }
		}
		if (userRequest.getRequest().get(JsonKey.LANGUAGE) != null
				&&((List) userRequest.getRequest().get(JsonKey.LANGUAGE)).isEmpty()) {
			throw new ProjectCommonException(ResponseCode.languageRequired.getErrorCode(),
					ResponseCode.languageRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
		if (userRequest.getRequest().get(JsonKey.ADDRESS) != null
            && ((List) userRequest.getRequest().get(JsonKey.ADDRESS)).isEmpty()) {
        throw new ProjectCommonException(ResponseCode.addressRequired.getErrorCode(),
                ResponseCode.addressRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
       }if (userRequest.getRequest().get(JsonKey.EDUCATION) != null
           && ((List) userRequest.getRequest().get(JsonKey.EDUCATION)).isEmpty()) {
       throw new ProjectCommonException(ResponseCode.educationRequired.getErrorCode(),
               ResponseCode.educationRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
        }if (userRequest.getRequest().get(JsonKey.JOB_PROFILE) != null
           && ((List) userRequest.getRequest().get(JsonKey.JOB_PROFILE)).isEmpty()) {
         throw new ProjectCommonException(ResponseCode.jobDetailsRequired.getErrorCode(),
           ResponseCode.jobDetailsRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
        }
   }

	/**
	 * This method will validate user login data.
	 * @param userRequest Request
	 */
	public static void validateUserLogin(Request userRequest) {
		if (userRequest.getRequest().get(JsonKey.USERNAME) == null) {
			throw new ProjectCommonException(ResponseCode.userNameRequired.getErrorCode(),
					ResponseCode.userNameRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} 
		if (userRequest.getRequest().get(JsonKey.PASSWORD) == null
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PASSWORD)))) {
			throw new ProjectCommonException(ResponseCode.passwordRequired.getErrorCode(),
					ResponseCode.passwordRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
		if (userRequest.getRequest().get(JsonKey.SOURCE) == null
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PASSWORD)))) {
			throw new ProjectCommonException(ResponseCode.sourceRequired.getErrorCode(),
					ResponseCode.sourceRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
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
		} 
	    if (userRequest.getRequest().get(JsonKey.NEW_PASSWORD) == null
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.NEW_PASSWORD)))) {
			throw new ProjectCommonException(ResponseCode.sourceRequired.getErrorCode(),
					ResponseCode.sourceRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
	}
	
	/**
	 * This method will validate get page data api.
	 * @param request Request
	 */
	public static void validateGetPageData(String request) {
	    if (request == null
				|| (ProjectUtil.isStringNullOREmpty(request))) {
			throw new ProjectCommonException(ResponseCode.sourceRequired.getErrorCode(),
					ResponseCode.sourceRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		} 
	    
	}
	
	/**
	 * This method will validate add course request data.
	 * @param courseRequest Request
	 */
	public static void validateAddCourse(Request courseRequest) {

		 if (courseRequest.getRequest().get(JsonKey.CONTENT_ID) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseIdRequiredError.getErrorCode(),
					ResponseCode.courseIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
		 if (courseRequest.getRequest().get(JsonKey.COURSE_NAME) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseNameRequired.getErrorCode(),
					ResponseCode.courseNameRequired.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
		 if (courseRequest.getRequest().get(JsonKey.ORGANISATION_ID) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.organisationIdRequiredError.getErrorCode(),
					ResponseCode.organisationIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
		 if (courseRequest.getRequest().get(JsonKey.ENROLLMENT_START_DATE) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.enrollmentStartDateRequiredError.getErrorCode(),
					ResponseCode.enrollmentStartDateRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
		 if (courseRequest.getRequest().get(JsonKey.COURSE_DURATION) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseDurationRequiredError.getErrorCode(),
					ResponseCode.courseDurationRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
	
	}
	
	/**
	 * This method will validate update course request data.
	 * @param request Request
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
	 * @param request Request
	 */
	public static void validatePublishCourse(Request request) {
		 if (request.getRequest().get(JsonKey.COURSE_ID) == null) {
			ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseIdRequiredError.getErrorCode(),
					ResponseCode.courseIdRequiredError.getErrorMessage(),ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
	}
	
	
	/**
	 * This method will validate Delete course request data.
	 * @param request Request
	 */
	public static void validateDeleteCourse(Request request) {
		 if (request.getRequest().get(JsonKey.COURSE_ID) == null) {
			 ProjectCommonException dataException = new ProjectCommonException(ResponseCode.courseIdRequiredError.getErrorCode(),
					 ResponseCode.courseIdRequiredError.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
			 throw dataException;
		 }
	}
	
	
	/* This method will validate create section data
	 * @param userRequest Request
	 */
	public static void validateCreateSection(Request request) {
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.SECTION_NAME) != null
				? request.getRequest().get(JsonKey.SECTION_NAME) : ""))) {
			ProjectCommonException dataException = new ProjectCommonException(
					ResponseCode.sectionNameRequired.getErrorCode(), ResponseCode.sectionNameRequired.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.SECTION_DATA_TYPE) != null
				? request.getRequest().get(JsonKey.SECTION_DATA_TYPE) : ""))) {
			ProjectCommonException dataException = new ProjectCommonException(
					ResponseCode.sectionDataTypeRequired.getErrorCode(), ResponseCode.sectionDataTypeRequired.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
	}
	
	/**
	 * This method will validate update section request data
	 * @param request Request
	 */
	public static void validateUpdateSection(Request request) {
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.SECTION_NAME) != null
				? request.getRequest().get(JsonKey.SECTION_NAME) : ""))) {
			ProjectCommonException dataException = new ProjectCommonException(
					ResponseCode.sectionNameRequired.getErrorCode(), ResponseCode.sectionNameRequired.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.ID) != null
				? request.getRequest().get(JsonKey.ID) : ""))) {
			 throw new ProjectCommonException(
					ResponseCode.sectionIdRequired.getErrorCode(), ResponseCode.sectionIdRequired.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());	
		}
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.SECTION_DATA_TYPE) != null
				? request.getRequest().get(JsonKey.SECTION_DATA_TYPE) : ""))) {
			ProjectCommonException dataException = new ProjectCommonException(
					ResponseCode.sectionDataTypeRequired.getErrorCode(), ResponseCode.sectionDataTypeRequired.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
	}
	
	
	/**
	 * This method will validate create page data
	 * @param request Request
	 */
	public static void validateCreatePage(Request request) {
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.PAGE_NAME) != null
				? request.getRequest().get(JsonKey.PAGE_NAME) : ""))) {
			ProjectCommonException dataException = new ProjectCommonException(
					ResponseCode.pageNameRequired.getErrorCode(), ResponseCode.pageNameRequired.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
	}
	
	/**
	 * This method will validate update page request data
	 * @param request Request
	 */
	public static void validateUpdatepage(Request request) {
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.PAGE_NAME) != null
				? request.getRequest().get(JsonKey.PAGE_NAME) : ""))) {
			ProjectCommonException dataException = new ProjectCommonException(
					ResponseCode.pageNameRequired.getErrorCode(), ResponseCode.pageNameRequired.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.ID) != null
				? request.getRequest().get(JsonKey.ID) : ""))) {
			 throw new ProjectCommonException(
					ResponseCode.pageIdRequired.getErrorCode(), ResponseCode.pageIdRequired.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());	
		}
	}
	
	
	/**
	 * This method will validate save Assessment data.
	 * @param request Request
	 */
	public static void validateSaveAssessment(Request request) {
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.COURSE_ID) != null
				? request.getRequest().get(JsonKey.COURSE_ID) : ""))) {
			ProjectCommonException dataException = new ProjectCommonException(
					ResponseCode.courseIdRequired.getErrorCode(), ResponseCode.courseIdRequired.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		} 
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.CONTENT_ID) != null
				? request.getRequest().get(JsonKey.CONTENT_ID) : ""))) {
			throw new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
					ResponseCode.contentIdRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.ATTEMPT_ID) != null
				? request.getRequest().get(JsonKey.ATTEMPT_ID) : ""))) {
			throw new ProjectCommonException(ResponseCode.attemptIdRequired.getErrorCode(),
					ResponseCode.attemptIdRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
		if( request.getRequest().get(JsonKey.ASSESSMENT) != null ){
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> list = (List<Map<String, Object>>) request.getRequest().get(JsonKey.ASSESSMENT);
			for(Map<String,Object> map :list){
				if (ProjectUtil
						.isStringNullOREmpty((String) (map.get(JsonKey.ASSESSMENT_ITEM_ID) != null
								? map.get(JsonKey.ASSESSMENT_ITEM_ID) : ""))) {
					throw new ProjectCommonException(ResponseCode.assessmentItemIdRequired.getErrorCode(),
							ResponseCode.assessmentItemIdRequired.getErrorMessage(),
							ResponseCode.CLIENT_ERROR.getResponseCode());
				}
				if (ProjectUtil.isStringNullOREmpty((String) (map.get(JsonKey.ASSESSMENT_TYPE) != null
						? map.get(JsonKey.ASSESSMENT_TYPE) : ""))) {
					throw new ProjectCommonException(ResponseCode.assessmentTypeRequired.getErrorCode(),
							ResponseCode.assessmentTypeRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
				}
				if (ProjectUtil
						.isStringNullOREmpty((String) (map.get(JsonKey.ASSESSMENT_ANSWERS) != null
								? map.get(JsonKey.ASSESSMENT_ANSWERS) : ""))) {
					throw new ProjectCommonException(ResponseCode.assessmentAnswersRequired.getErrorCode(),
							ResponseCode.assessmentAnswersRequired.getErrorMessage(), 
							ResponseCode.CLIENT_ERROR.getResponseCode());
				}
				if (ProjectUtil
						.isStringNullOREmpty((String) (map.get(JsonKey.ASSESSMENT_MAX_SCORE) != null
								? map.get(JsonKey.ASSESSMENT_ANSWERS) : ""))) {
					throw new ProjectCommonException(ResponseCode.assessmentmaxScoreRequired.getErrorCode(),
							ResponseCode.assessmentmaxScoreRequired.getErrorMessage(),
							ResponseCode.CLIENT_ERROR.getResponseCode());
				}
			}
		}
		
	}
	
	
	/**
	 * This method will validate get Assessment data.
	 * @param request Request
	 */
	public static void validateGetAssessment(Request request) {
		if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.COURSE_ID) != null
				? request.getRequest().get(JsonKey.COURSE_ID) : ""))) {
			ProjectCommonException dataException = new ProjectCommonException(
					ResponseCode.courseIdRequiredError.getErrorCode(),
					ResponseCode.courseIdRequiredError.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
			throw dataException;
		}
	}

	/**
	 * This method will validate user org requested data.
	 * @param userRequest Request
	 */
	public static void validateUserOrg(Request userRequest) {
		if (isNull(userRequest.getRequest().get(JsonKey.ORGANISATION_ID))
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.ORGANISATION_ID)))) {
			throw new ProjectCommonException(ResponseCode.orgIdRequired.getErrorCode(),
					ResponseCode.orgIdRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
		if (isNull(userRequest.getRequest().get(JsonKey.USER_ID))
				|| (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.USER_ID)))) {
			throw new ProjectCommonException(ResponseCode.userIdRequired.getErrorCode(),
					ResponseCode.userIdRequired.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
		}
	}

	
	/**
     * This method will validate composite search request data.
     * @param searchRequest Request
     */
    public static void validateCompositeSearch(Request searchRequest) {
    }
	
	
}
