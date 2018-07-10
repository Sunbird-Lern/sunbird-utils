package org.sunbird.common.request;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.ProjectUtil.ProgressStatus;
import org.sunbird.common.models.util.ProjectUtil.Source;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.common.responsecode.ResponseMessage;

/**
 * This call will do validation for all incoming request data.
 *
 * @author Manzarul
 */
public final class RequestValidator {
	private static final int ERROR_CODE = ResponseCode.CLIENT_ERROR.getResponseCode();

	private RequestValidator() {}

	/**
	 * This method will do course enrollment request data validation. if all mandatory data is coming
	 * then it won't do any thing if any mandatory data is missing then it will throw exception.
	 *
	 * @param courseRequestDto CourseRequestDto
	 */
	public static void validateEnrollCourse(Request courseRequestDto) {
		if (courseRequestDto.getRequest().get(JsonKey.COURSE_ID) == null) {
			throw new ProjectCommonException(
					ResponseCode.courseIdRequiredError.getErrorCode(),
					ResponseCode.courseIdRequiredError.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * This method will do content state request data validation. if all mandatory data is coming then
	 * it won't do any thing if any mandatory data is missing then it will throw exception.
	 *
	 * @param contentRequestDto Request
	 */
	@SuppressWarnings("unchecked")
	public static void validateUpdateContent(Request contentRequestDto) {
		if (((List<Map<String, Object>>) (contentRequestDto.getRequest().get(JsonKey.CONTENTS)))
				.isEmpty()) {
			throw new ProjectCommonException(
					ResponseCode.contentIdRequired.getErrorCode(),
					ResponseCode.contentIdRequiredError.getErrorMessage(),
					ERROR_CODE);
		} else {
			List<Map<String, Object>> list =
					(List<Map<String, Object>>) (contentRequestDto.getRequest().get(JsonKey.CONTENTS));
			for (Map<String, Object> map : list) {
				if (null != map.get(JsonKey.LAST_UPDATED_TIME)) {
					boolean bool =
							ProjectUtil.isDateValidFormat(
									"yyyy-MM-dd HH:mm:ss:SSSZ", (String) map.get(JsonKey.LAST_UPDATED_TIME));
					if (!bool) {
						throw new ProjectCommonException(
								ResponseCode.dateFormatError.getErrorCode(),
								ResponseCode.dateFormatError.getErrorMessage(),
								ERROR_CODE);
					}
				}
				if (null != map.get(JsonKey.LAST_COMPLETED_TIME)) {
					boolean bool =
							ProjectUtil.isDateValidFormat(
									"yyyy-MM-dd HH:mm:ss:SSSZ", (String) map.get(JsonKey.LAST_COMPLETED_TIME));
					if (!bool) {
						throw new ProjectCommonException(
								ResponseCode.dateFormatError.getErrorCode(),
								ResponseCode.dateFormatError.getErrorMessage(),
								ERROR_CODE);
					}
				}
				if (map.containsKey(JsonKey.CONTENT_ID)) {

					if (null == map.get(JsonKey.CONTENT_ID)) {
						throw new ProjectCommonException(
								ResponseCode.contentIdRequired.getErrorCode(),
								ResponseCode.contentIdRequiredError.getErrorMessage(),
								ERROR_CODE);
					}
					if (ProjectUtil.isNull(map.get(JsonKey.STATUS))) {
						throw new ProjectCommonException(
								ResponseCode.contentStatusRequired.getErrorCode(),
								ResponseCode.contentStatusRequired.getErrorMessage(),
								ERROR_CODE);
					}

				} else {
					throw new ProjectCommonException(
							ResponseCode.contentIdRequired.getErrorCode(),
							ResponseCode.contentIdRequiredError.getErrorMessage(),
							ERROR_CODE);
				}
			}
		}
	}

	public static void validateCreateOrg(Request request) {
		if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.ORG_NAME))) {
			throw new ProjectCommonException(
					ResponseCode.organisationNameRequired.getErrorCode(),
					ResponseCode.organisationNameRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if ((null != request.getRequest().get(JsonKey.IS_ROOT_ORG)
				&& (Boolean) request.getRequest().get(JsonKey.IS_ROOT_ORG))
				&& StringUtils.isEmpty((String) request.getRequest().get(JsonKey.CHANNEL))) {
			throw new ProjectCommonException(
					ResponseCode.channelIdRequiredForRootOrg.getErrorCode(),
					ResponseCode.channelIdRequiredForRootOrg.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());
		}
	}

	public static void validateOrg(Request request) {
		if (StringUtils.isEmpty((String) request.getRequest().get(JsonKey.ORGANISATION_ID))
				&& ((StringUtils.isEmpty((String) request.getRequest().get(JsonKey.PROVIDER)))
						|| (StringUtils.isBlank((String) request.getRequest().get(JsonKey.EXTERNAL_ID))))) {
			throw new ProjectCommonException(
					ResponseCode.sourceAndExternalIdValidationError.getErrorCode(),
					ResponseCode.sourceAndExternalIdValidationError.getErrorMessage(),
					ERROR_CODE);
		}
	}

	public static void validateUpdateOrg(Request request) {
		validateOrg(request);
		if (request.getRequest().containsKey(JsonKey.ROOT_ORG_ID)
				&& StringUtils.isEmpty((String) request.getRequest().get(JsonKey.ROOT_ORG_ID))) {
			throw new ProjectCommonException(
					ResponseCode.invalidRootOrganisationId.getErrorCode(),
					ResponseCode.invalidRootOrganisationId.getErrorMessage(),
					ERROR_CODE);
		}
		if (request.getRequest().get(JsonKey.STATUS) != null) {
			throw new ProjectCommonException(
					ResponseCode.invalidRequestData.getErrorCode(),
					ResponseCode.invalidRequestData.getErrorMessage(),
					ERROR_CODE);
		}
		if ((null != request.getRequest().get(JsonKey.IS_ROOT_ORG)
				&& (Boolean) request.getRequest().get(JsonKey.IS_ROOT_ORG))
				&& StringUtils.isEmpty((String) request.getRequest().get(JsonKey.CHANNEL))) {
			throw new ProjectCommonException(
					ResponseCode.channelIdRequiredForRootOrg.getErrorCode(),
					ResponseCode.channelIdRequiredForRootOrg.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());
		}
	}

	public static void validateUpdateOrgStatus(Request request) {
		validateOrg(request);
		if (!request.getRequest().containsKey(JsonKey.STATUS)) {
			throw new ProjectCommonException(
					ResponseCode.invalidRequestData.getErrorCode(),
					ResponseCode.invalidRequestData.getErrorMessage(),
					ERROR_CODE);
		}
		if (!(request.getRequest().get(JsonKey.STATUS) instanceof BigInteger)) {
			throw new ProjectCommonException(
					ResponseCode.invalidRequestData.getErrorCode(),
					ResponseCode.invalidRequestData.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * This method will validate get page data api.
	 *
	 * @param request Request
	 */
	public static void validateGetPageData(Request request) {
		if (request == null || (StringUtils.isBlank((String) request.get(JsonKey.SOURCE)))) {
			throw new ProjectCommonException(
					ResponseCode.sourceRequired.getErrorCode(),
					ResponseCode.sourceRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (!validPageSourceType((String) request.get(JsonKey.SOURCE))) {
			throw new ProjectCommonException(
					ResponseCode.invalidPageSource.getErrorCode(),
					ResponseCode.invalidPageSource.getErrorMessage(),
					ERROR_CODE);
		}
		if (StringUtils.isBlank((String) request.get(JsonKey.PAGE_NAME))) {
			throw new ProjectCommonException(
					ResponseCode.pageNameRequired.getErrorCode(),
					ResponseCode.pageNameRequired.getErrorMessage(),
					ERROR_CODE);
		}
	}

	private static boolean validPageSourceType(String source) {

		Boolean isValidSource = false;
		for (Source src : ProjectUtil.Source.values()) {
			if (src.getValue().equalsIgnoreCase(source)) {
				isValidSource = true;
				break;
			}
		}
		return isValidSource;
	}

	/**
	 * This method will validate add course request data.
	 *
	 * @param courseRequest Request
	 */
	public static void validateAddBatchCourse(Request courseRequest) {

		if (courseRequest.getRequest().get(JsonKey.BATCH_ID) == null) {
			throw new ProjectCommonException(
					ResponseCode.courseBatchIdRequired.getErrorCode(),
					ResponseCode.courseBatchIdRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (courseRequest.getRequest().get(JsonKey.USER_IDs) == null) {
			throw new ProjectCommonException(
					ResponseCode.userIdRequired.getErrorCode(),
					ResponseCode.userIdRequired.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * This method will validate add course request data.
	 *
	 * @param courseRequest Request
	 */
	public static void validateGetBatchCourse(Request courseRequest) {

		if (courseRequest.getRequest().get(JsonKey.BATCH_ID) == null) {
			throw new ProjectCommonException(
					ResponseCode.courseBatchIdRequired.getErrorCode(),
					ResponseCode.courseBatchIdRequired.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * This method will validate update course request data.
	 *
	 * @param request Request
	 */
	public static void validateUpdateCourse(Request request) {

		if (request.getRequest().get(JsonKey.COURSE_ID) == null) {
			throw new ProjectCommonException(
					ResponseCode.courseIdRequired.getErrorCode(),
					ResponseCode.courseIdRequired.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * This method will validate published course request data.
	 *
	 * @param request Request
	 */
	public static void validatePublishCourse(Request request) {
		if (request.getRequest().get(JsonKey.COURSE_ID) == null) {
			throw new ProjectCommonException(
					ResponseCode.courseIdRequiredError.getErrorCode(),
					ResponseCode.courseIdRequiredError.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * This method will validate Delete course request data.
	 *
	 * @param request Request
	 */
	public static void validateDeleteCourse(Request request) {
		if (request.getRequest().get(JsonKey.COURSE_ID) == null) {
			throw new ProjectCommonException(
					ResponseCode.courseIdRequiredError.getErrorCode(),
					ResponseCode.courseIdRequiredError.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/*
	 * This method will validate create section data
	 *
	 * @param userRequest Request
	 */
	public static void validateCreateSection(Request request) {
		if (StringUtils.isBlank(
				(String)
				(request.getRequest().get(JsonKey.SECTION_NAME) != null
				? request.getRequest().get(JsonKey.SECTION_NAME)
						: ""))) {
			throw new ProjectCommonException(
					ResponseCode.sectionNameRequired.getErrorCode(),
					ResponseCode.sectionNameRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (StringUtils.isBlank(
				(String)
				(request.getRequest().get(JsonKey.SECTION_DATA_TYPE) != null
				? request.getRequest().get(JsonKey.SECTION_DATA_TYPE)
						: ""))) {
			throw new ProjectCommonException(
					ResponseCode.sectionDataTypeRequired.getErrorCode(),
					ResponseCode.sectionDataTypeRequired.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * This method will validate update section request data
	 *
	 * @param request Request
	 */
	public static void validateUpdateSection(Request request) {
		if (request.getRequest().containsKey(JsonKey.SECTION_NAME)
				&& StringUtils.isBlank(
						(String)
						(request.getRequest().get(JsonKey.SECTION_NAME) != null
						? request.getRequest().get(JsonKey.SECTION_NAME)
								: ""))) {
			throw new ProjectCommonException(
					ResponseCode.sectionNameRequired.getErrorCode(),
					ResponseCode.sectionNameRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (StringUtils.isBlank(
				(String)
				(request.getRequest().get(JsonKey.ID) != null
				? request.getRequest().get(JsonKey.ID)
						: ""))) {
			throw new ProjectCommonException(
					ResponseCode.sectionIdRequired.getErrorCode(),
					ResponseCode.sectionIdRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (request.getRequest().containsKey(JsonKey.SECTION_DATA_TYPE)
				&& StringUtils.isBlank(
						(String)
						(request.getRequest().get(JsonKey.SECTION_DATA_TYPE) != null
						? request.getRequest().get(JsonKey.SECTION_DATA_TYPE)
								: ""))) {
			throw new ProjectCommonException(
					ResponseCode.sectionDataTypeRequired.getErrorCode(),
					ResponseCode.sectionDataTypeRequired.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * This method will validate create page data
	 *
	 * @param request Request
	 */
	public static void validateCreatePage(Request request) {
		if (StringUtils.isEmpty(
				(String)
				(request.getRequest().get(JsonKey.PAGE_NAME) != null
				? request.getRequest().get(JsonKey.PAGE_NAME)
						: ""))) {
			throw new ProjectCommonException(
					ResponseCode.pageNameRequired.getErrorCode(),
					ResponseCode.pageNameRequired.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * This method will validate update page request data
	 *
	 * @param request Request
	 */
	public static void validateUpdatepage(Request request) {
		if (request.getRequest().containsKey(JsonKey.PAGE_NAME)
				&& StringUtils.isEmpty(
						(String)
						(request.getRequest().get(JsonKey.PAGE_NAME) != null
						? request.getRequest().get(JsonKey.PAGE_NAME)
								: ""))) {
			throw new ProjectCommonException(
					ResponseCode.pageNameRequired.getErrorCode(),
					ResponseCode.pageNameRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (StringUtils.isBlank(
				(String)
				(request.getRequest().get(JsonKey.ID) != null
				? request.getRequest().get(JsonKey.ID)
						: ""))) {
			throw new ProjectCommonException(
					ResponseCode.pageIdRequired.getErrorCode(),
					ResponseCode.pageIdRequired.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * This method will validate save Assessment data.
	 *
	 * @param request Request
	 */
	public static void validateSaveAssessment(Request request) {
		if (StringUtils.isEmpty(
				(String)
				(request.getRequest().get(JsonKey.COURSE_ID) != null
				? request.getRequest().get(JsonKey.COURSE_ID)
						: ""))) {
			throw new ProjectCommonException(
					ResponseCode.courseIdRequired.getErrorCode(),
					ResponseCode.courseIdRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (StringUtils.isEmpty(
				(String)
				(request.getRequest().get(JsonKey.CONTENT_ID) != null
				? request.getRequest().get(JsonKey.CONTENT_ID)
						: ""))) {
			throw new ProjectCommonException(
					ResponseCode.contentIdRequired.getErrorCode(),
					ResponseCode.contentIdRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (StringUtils.isEmpty(
				(String)
				(request.getRequest().get(JsonKey.ATTEMPT_ID) != null
				? request.getRequest().get(JsonKey.ATTEMPT_ID)
						: ""))) {
			throw new ProjectCommonException(
					ResponseCode.attemptIdRequired.getErrorCode(),
					ResponseCode.attemptIdRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (request.getRequest().get(JsonKey.ASSESSMENT) != null) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list =
			(List<Map<String, Object>>) request.getRequest().get(JsonKey.ASSESSMENT);
			if (list == null) {
				throw new ProjectCommonException(
						ResponseCode.invalidRequestData.getErrorCode(),
						ResponseCode.invalidRequestData.getErrorMessage(),
						ERROR_CODE);
			}
			validateAssessment(list);
		}
	}

	private static void validateAssessment(List<Map<String, Object>> list) {
		for (Map<String, Object> map : list) {
			if (StringUtils.isEmpty(
					(String)
					(map.get(JsonKey.ASSESSMENT_ITEM_ID) != null
					? map.get(JsonKey.ASSESSMENT_ITEM_ID)
							: ""))) {
				throw new ProjectCommonException(
						ResponseCode.assessmentItemIdRequired.getErrorCode(),
						ResponseCode.assessmentItemIdRequired.getErrorMessage(),
						ERROR_CODE);
			}
			if (StringUtils.isBlank(
					(String)
					(map.get(JsonKey.ASSESSMENT_TYPE) != null ? map.get(JsonKey.ASSESSMENT_TYPE) : ""))) {
				throw new ProjectCommonException(
						ResponseCode.assessmentTypeRequired.getErrorCode(),
						ResponseCode.assessmentTypeRequired.getErrorMessage(),
						ERROR_CODE);
			}
			if (StringUtils.isEmpty(
					(String)
					(map.get(JsonKey.ASSESSMENT_ANSWERS) != null
					? map.get(JsonKey.ASSESSMENT_ANSWERS)
							: ""))) {
				throw new ProjectCommonException(
						ResponseCode.assessmentAnswersRequired.getErrorCode(),
						ResponseCode.assessmentAnswersRequired.getErrorMessage(),
						ERROR_CODE);
			}
			if (StringUtils.isEmpty(
					(String)
					(map.get(JsonKey.ASSESSMENT_MAX_SCORE) != null
					? map.get(JsonKey.ASSESSMENT_MAX_SCORE)
							: ""))) {
				throw new ProjectCommonException(
						ResponseCode.assessmentmaxScoreRequired.getErrorCode(),
						ResponseCode.assessmentmaxScoreRequired.getErrorMessage(),
						ERROR_CODE);
			}
		}
	}

	/**
	 * This method will validate get Assessment data.
	 *
	 * @param request Request
	 */
	public static void validateGetAssessment(Request request) {
		if (StringUtils.isEmpty(
				(String)
				(request.getRequest().get(JsonKey.COURSE_ID) != null
				? request.getRequest().get(JsonKey.COURSE_ID)
						: ""))) {
			throw new ProjectCommonException(
					ResponseCode.courseIdRequiredError.getErrorCode(),
					ResponseCode.courseIdRequiredError.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * This method will validate user org requested data.
	 *
	 * @param userRequest Request
	 */
	public static void validateUserOrg(Request userRequest) {
		validateOrg(userRequest);
		if ((StringUtils.isEmpty((String) userRequest.getRequest().get(JsonKey.USERNAME))
				|| StringUtils.isBlank((String) userRequest.getRequest().get(JsonKey.PROVIDER)))
				&& StringUtils.isBlank((String) userRequest.getRequest().get(JsonKey.USER_ID))) {
			throw new ProjectCommonException(
					ResponseCode.usrValidationError.getErrorCode(),
					ResponseCode.usrValidationError.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());
		}
	}

	/**
	 * This method will validate user org requested data.
	 *
	 * @param userRequest Request
	 */
	@SuppressWarnings("rawtypes")
	public static void validateAddMember(Request userRequest) {
		validateOrg(userRequest);
		if (userRequest.getRequest().containsKey(JsonKey.ROLES)
				&& (!(userRequest.getRequest().get(JsonKey.ROLES) instanceof List)
						|| ((List) userRequest.getRequest().get(JsonKey.ROLES)).isEmpty())) {
			throw new ProjectCommonException(
					ResponseCode.roleRequired.getErrorCode(),
					ResponseCode.roleRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if ((StringUtils.isEmpty((String) userRequest.getRequest().get(JsonKey.USERNAME))
				|| StringUtils.isBlank((String) userRequest.getRequest().get(JsonKey.PROVIDER)))
				&& StringUtils.isBlank((String) userRequest.getRequest().get(JsonKey.USER_ID))) {
			throw new ProjectCommonException(
					ResponseCode.usrValidationError.getErrorCode(),
					ResponseCode.usrValidationError.getErrorMessage(),
					ResponseCode.CLIENT_ERROR.getResponseCode());
		}
	}

	/**
	 * This method will validate bulk user upload requested data.
	 *
	 * @param reqObj Request
	 */
	public static void validateUploadUser(Map<String, Object> reqObj) {
		if (StringUtils.isBlank((String) reqObj.get(JsonKey.ORGANISATION_ID))
				&& (StringUtils.isBlank((String) reqObj.get(JsonKey.ORG_EXTERNAL_ID))
						|| StringUtils.isBlank((String) reqObj.get(JsonKey.ORG_PROVIDER)))) {
			throw new ProjectCommonException(
					ResponseCode.mandatoryParamsMissing.getErrorCode(),
					ProjectUtil.formatMessage(
							ResponseCode.mandatoryParamsMissing.getErrorMessage(),
							(ProjectUtil.formatMessage(
									ResponseMessage.Message.OR_FORMAT,
									JsonKey.ORGANISATION_ID,
									ProjectUtil.formatMessage(
											ResponseMessage.Message.AND_FORMAT,
											JsonKey.ORG_EXTERNAL_ID,
											JsonKey.ORG_PROVIDER)))),
					ERROR_CODE);
		}
		if (null == reqObj.get(JsonKey.FILE)) {
			throw new ProjectCommonException(
					ResponseCode.mandatoryParamsMissing.getErrorCode(),
					ProjectUtil.formatMessage(
							ResponseCode.mandatoryParamsMissing.getErrorMessage(), JsonKey.FILE),
					ERROR_CODE);
		}
	}

	/**
	 * courseId : Should be a valid courseId under EKStep. name : should not be null or empty
	 * enrolmentType: can have only following two values {"open","invite-only"} startDate : In
	 * yyyy-MM-DD format , and must be >= today date. endDate : In yyyy-MM-DD format and must be >
	 * startDate createdFor : List of valid organisation ids. this filed will be used in case of
	 * "invite-only" enrolmentType. for open type if createdFor values is coming then system will just
	 * save that value. mentors : List of user ids , who will work as a mentor.
	 *
	 * @param request
	 */
	public static void validateCreateBatchReq(Request request) {
		if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.COURSE_ID))) {
			throw new ProjectCommonException(
					ResponseCode.invalidCourseId.getErrorCode(),
					ResponseCode.invalidCourseId.getErrorMessage(),
					ERROR_CODE);
		}
		if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.NAME))) {
			throw new ProjectCommonException(
					ResponseCode.courseNameRequired.getErrorCode(),
					ResponseCode.courseNameRequired.getErrorMessage(),
					ERROR_CODE);
		}
		String enrolmentType = (String) request.getRequest().get(JsonKey.ENROLLMENT_TYPE);
		validateEnrolmentType(enrolmentType);
		String startDate = (String) request.getRequest().get(JsonKey.START_DATE);
		validateStartDate(startDate);
		if (request.getRequest().containsKey(JsonKey.END_DATE)
				&& !StringUtils.isEmpty((String) request.getRequest().get(JsonKey.END_DATE))) {
			validateEndDate(startDate, (String) request.getRequest().get(JsonKey.END_DATE));
		}
		if (request.getRequest().containsKey(JsonKey.COURSE_CREATED_FOR)
				&& !(request.getRequest().get(JsonKey.COURSE_CREATED_FOR) instanceof List)) {
			throw new ProjectCommonException(
					ResponseCode.dataTypeError.getErrorCode(),
					ResponseCode.dataTypeError.getErrorMessage(),
					ERROR_CODE);
		}
	}

	private static boolean checkProgressStatus(int status) {
		for (ProgressStatus pstatus : ProgressStatus.values()) {
			if (pstatus.getValue() == status) {
				return true;
			}
		}
		return false;
	}

	public static void validateUpdateCourseBatchReq(Request request) {
		
		if (null != request.getRequest().get(JsonKey.STATUS)) {
			boolean status = validateBatchStatus(request);
			if (!status) {
				throw new ProjectCommonException(
						ResponseCode.progressStatusError.getErrorCode(),
						ResponseCode.progressStatusError.getErrorMessage(),
						ERROR_CODE);
			}
		}
		if (request.getRequest().containsKey(JsonKey.NAME)
				&& StringUtils.isEmpty((String) request.getRequest().get(JsonKey.NAME))) {
			throw new ProjectCommonException(
					ResponseCode.courseNameRequired.getErrorCode(),
					ResponseCode.courseNameRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (request.getRequest().containsKey(JsonKey.ENROLLMENT_TYPE)) {
			String enrolmentType = (String) request.getRequest().get(JsonKey.ENROLLMENT_TYPE);
			validateEnrolmentType(enrolmentType);
		}
		String startDate = (String) request.getRequest().get(JsonKey.START_DATE);
		validateStartDate(startDate);
		if (request.getRequest().containsKey(JsonKey.END_DATE)
				&& !StringUtils.isEmpty((String) request.getRequest().get(JsonKey.END_DATE))) {
			validateEndDate(startDate, (String) request.getRequest().get(JsonKey.END_DATE));
		}
		if (request.getRequest().containsKey(JsonKey.END_DATE)
				&& !StringUtils.isEmpty((String) request.getRequest().get(JsonKey.END_DATE))) {
			boolean bool = validateDateWithTodayDate((String) request.getRequest().get(JsonKey.END_DATE));
			if (!bool) {
				throw new ProjectCommonException(
						ResponseCode.invalidBatchEndDateError.getErrorCode(),
						ResponseCode.invalidBatchEndDateError.getErrorMessage(),
						ERROR_CODE);
			}
		}
		validateUpdateBatchEndDate(request);
		if (request.getRequest().containsKey(JsonKey.COURSE_CREATED_FOR)
				&& !(request.getRequest().get(JsonKey.COURSE_CREATED_FOR) instanceof List)) {
			throw new ProjectCommonException(
					ResponseCode.dataTypeError.getErrorCode(),
					ResponseCode.dataTypeError.getErrorMessage(),
					ERROR_CODE);
		}

		if (request.getRequest().containsKey(JsonKey.MENTORS)
				&& !(request.getRequest().get(JsonKey.MENTORS) instanceof List)) {
			throw new ProjectCommonException(
					ResponseCode.dataTypeError.getErrorCode(),
					ResponseCode.dataTypeError.getErrorMessage(),
					ERROR_CODE);
		}
	}

	private static boolean validateBatchStatus(Request request) {
		boolean status = false;
		try {
			status = checkProgressStatus(Integer.parseInt("" + request.getRequest().get(JsonKey.STATUS)));

		} catch (Exception e) {
			ProjectLogger.log(e.getMessage(), e);
		}
		return status;
	}

	private static void validateUpdateBatchEndDate(Request request) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setLenient(false);
		if (request.getRequest().containsKey(JsonKey.END_DATE)
				&& !StringUtils.isEmpty((String) request.getRequest().get(JsonKey.END_DATE))
				&& request.getRequest().containsKey(JsonKey.START_DATE)
				&& !StringUtils.isBlank((String) request.getRequest().get(JsonKey.START_DATE))) {
			Date batchStartDate = null;
			Date batchEndDate = null;
			try {
				batchStartDate = format.parse((String) request.getRequest().get(JsonKey.START_DATE));
				batchEndDate = format.parse((String) request.getRequest().get(JsonKey.END_DATE));
				Calendar cal1 = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();
				cal1.setTime(batchStartDate);
				cal2.setTime(batchEndDate);
			} catch (Exception e) {
				throw new ProjectCommonException(
						ResponseCode.dateFormatError.getErrorCode(),
						ResponseCode.dateFormatError.getErrorMessage(),
						ERROR_CODE);
			}
			if (batchEndDate.before(batchStartDate)) {
				throw new ProjectCommonException(
						ResponseCode.invalidBatchEndDateError.getErrorCode(),
						ResponseCode.invalidBatchEndDateError.getErrorMessage(),
						ERROR_CODE);
			}
		}
	}

	private static boolean validateDateWithTodayDate(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setLenient(false);
		try {
			Date reqDate = format.parse(date);
			Date todayDate = format.parse(format.format(new Date()));
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTime(reqDate);
			cal2.setTime(todayDate);
			if (reqDate.before(todayDate)) {
				return false;
			}
		} catch (Exception e) {
			throw new ProjectCommonException(
					ResponseCode.dateFormatError.getErrorCode(),
					ResponseCode.dateFormatError.getErrorMessage(),
					ERROR_CODE);
		}
		return true;
	}

	/** @param enrolmentType */
	public static void validateEnrolmentType(String enrolmentType) {
		if (StringUtils.isBlank(enrolmentType)) {
			throw new ProjectCommonException(
					ResponseCode.enrolmentTypeRequired.getErrorCode(),
					ResponseCode.enrolmentTypeRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (!(ProjectUtil.EnrolmentType.open.getVal().equalsIgnoreCase(enrolmentType)
				|| ProjectUtil.EnrolmentType.inviteOnly.getVal().equalsIgnoreCase(enrolmentType))) {
			throw new ProjectCommonException(
					ResponseCode.enrolmentIncorrectValue.getErrorCode(),
					ResponseCode.enrolmentIncorrectValue.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/** @param startDate */
	private static void validateStartDate(String startDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setLenient(false);
		if (StringUtils.isBlank(startDate)) {
			throw new ProjectCommonException(
					ResponseCode.courseBatchSatrtDateRequired.getErrorCode(),
					ResponseCode.courseBatchSatrtDateRequired.getErrorMessage(),
					ERROR_CODE);
		}
		try {
			Date batchStartDate = format.parse(startDate);
			Date todayDate = format.parse(format.format(new Date()));
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTime(batchStartDate);
			cal2.setTime(todayDate);
			if (batchStartDate.before(todayDate)) {
				throw new ProjectCommonException(
						ResponseCode.courseBatchStartDateError.getErrorCode(),
						ResponseCode.courseBatchStartDateError.getErrorMessage(),
						ERROR_CODE);
			}
		} catch (ProjectCommonException e) {
			throw e;
		} catch (Exception e) {
			throw new ProjectCommonException(
					ResponseCode.dateFormatError.getErrorCode(),
					ResponseCode.dateFormatError.getErrorMessage(),
					ERROR_CODE);
		}
	}

	private static void validateEndDate(String startDate, String endDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setLenient(false);
		Date batchEndDate = null;
		Date batchStartDate = null;
		try {
			batchEndDate = format.parse(endDate);
			batchStartDate = format.parse(startDate);
		} catch (Exception e) {
			throw new ProjectCommonException(
					ResponseCode.dateFormatError.getErrorCode(),
					ResponseCode.dateFormatError.getErrorMessage(),
					ERROR_CODE);
		}
		if (batchStartDate.getTime() >= batchEndDate.getTime()) {
			throw new ProjectCommonException(
					ResponseCode.endDateError.getErrorCode(),
					ResponseCode.endDateError.getErrorMessage(),
					ERROR_CODE);
		}
	}

	public static void validateSyncRequest(Request request) {
		String operation = (String) request.getRequest().get(JsonKey.OPERATION_FOR);
		if ((null != operation) && (!operation.equalsIgnoreCase("keycloak"))) {
			if (request.getRequest().get(JsonKey.OBJECT_TYPE) == null) {
				throw new ProjectCommonException(
						ResponseCode.dataTypeError.getErrorCode(),
						ResponseCode.dataTypeError.getErrorMessage(),
						ERROR_CODE);
			}
			List<String> list =
					new ArrayList<>(
							Arrays.asList(
									new String[] {
											JsonKey.USER, JsonKey.ORGANISATION, JsonKey.BATCH, JsonKey.USER_COURSE
									}));
			if (!list.contains(request.getRequest().get(JsonKey.OBJECT_TYPE))) {
				throw new ProjectCommonException(
						ResponseCode.invalidObjectType.getErrorCode(),
						ResponseCode.invalidObjectType.getErrorMessage(),
						ERROR_CODE);
			}
		}
	}

	public static void validateUpdateSystemSettingsRequest(Request request) {
		List<String> list =
				new ArrayList<>(
						Arrays.asList(
								PropertiesCache.getInstance()
								.getProperty("system_settings_properties")
								.split(",")));
		for (String str : request.getRequest().keySet()) {
			if (!list.contains(str)) {
				throw new ProjectCommonException(
						ResponseCode.invalidPropertyError.getErrorCode(),
						MessageFormat.format(ResponseCode.invalidPropertyError.getErrorMessage(), str),
						ERROR_CODE);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void validateSendMail(Request request) {
		if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.SUBJECT))) {
			throw new ProjectCommonException(
					ResponseCode.emailSubjectError.getErrorCode(),
					ResponseCode.emailSubjectError.getErrorMessage(),
					ERROR_CODE);
		}
		if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.BODY))) {
			throw new ProjectCommonException(
					ResponseCode.emailBodyError.getErrorCode(),
					ResponseCode.emailBodyError.getErrorMessage(),
					ERROR_CODE);
		}
		if (null == (request.getRequest().get(JsonKey.RECIPIENT_EMAILS))
				&& null == (request.getRequest().get(JsonKey.RECIPIENT_USERIDS))) {
			throw new ProjectCommonException(
					ResponseCode.recipientAddressError.getErrorCode(),
					ResponseCode.recipientAddressError.getErrorMessage(),
					ERROR_CODE);
		}
		if ((null != (request.getRequest().get(JsonKey.RECIPIENT_EMAILS))
				&& ((List) request.getRequest().get(JsonKey.RECIPIENT_EMAILS)).isEmpty())
				&& (null != (request.getRequest().get(JsonKey.RECIPIENT_USERIDS))
				&& ((List) request.getRequest().get(JsonKey.RECIPIENT_USERIDS)).isEmpty())) {
			throw new ProjectCommonException(
					ResponseCode.recipientAddressError.getErrorCode(),
					ResponseCode.recipientAddressError.getErrorMessage(),
					ERROR_CODE);
		}
	}

	public static void validateFileUpload(Request reqObj) {

		if (StringUtils.isBlank((String) reqObj.get(JsonKey.CONTAINER))) {
			throw new ProjectCommonException(
					ResponseCode.storageContainerNameMandatory.getErrorCode(),
					ResponseCode.storageContainerNameMandatory.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/** @param reqObj */
	public static void validateAddUserBadge(Request reqObj) {

		if (StringUtils.isBlank((String) reqObj.get(JsonKey.BADGE_TYPE_ID))) {
			throw createExceptionInstance(ResponseCode.badgeTypeIdMandatory.getErrorCode());
		}
		if (StringUtils.isBlank((String) reqObj.get(JsonKey.RECEIVER_ID))) {
			throw createExceptionInstance(ResponseCode.receiverIdMandatory.getErrorCode());
		}
	}

	/** @param reqObj */
	public static void validateCreateOrgType(Request reqObj) {
		if (StringUtils.isBlank((String) reqObj.getRequest().get(JsonKey.NAME))) {
			throw createExceptionInstance(ResponseCode.orgTypeMandatory.getErrorCode());
		}
	}

	/** @param reqObj */
	public static void validateUpdateOrgType(Request reqObj) {
		if (StringUtils.isBlank((String) reqObj.getRequest().get(JsonKey.NAME))) {
			throw createExceptionInstance(ResponseCode.orgTypeMandatory.getErrorCode());
		}
		if (StringUtils.isBlank((String) reqObj.getRequest().get(JsonKey.ID))) {
			throw createExceptionInstance(ResponseCode.orgTypeIdRequired.getErrorCode());
		}
	}

	/**
	 * Method to validate not for userId, title, note, courseId, contentId and tags
	 *
	 * @param request
	 */
	@SuppressWarnings("rawtypes")
	public static void validateNote(Request request) {
		if (StringUtils.isBlank((String) request.get(JsonKey.USER_ID))) {
			throw new ProjectCommonException(
					ResponseCode.userIdRequired.getErrorCode(),
					ResponseCode.userIdRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (StringUtils.isBlank((String) request.get(JsonKey.TITLE))) {
			throw new ProjectCommonException(
					ResponseCode.titleRequired.getErrorCode(),
					ResponseCode.titleRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (StringUtils.isBlank((String) request.get(JsonKey.NOTE))) {
			throw new ProjectCommonException(
					ResponseCode.noteRequired.getErrorCode(),
					ResponseCode.noteRequired.getErrorMessage(),
					ERROR_CODE);
		}
		if (StringUtils.isBlank((String) request.get(JsonKey.CONTENT_ID))
				&& StringUtils.isBlank((String) request.get(JsonKey.COURSE_ID))) {
			throw new ProjectCommonException(
					ResponseCode.contentIdError.getErrorCode(),
					ResponseCode.contentIdError.getErrorMessage(),
					ERROR_CODE);
		}
		if (request.getRequest().containsKey(JsonKey.TAGS)
				&& ((request.getRequest().get(JsonKey.TAGS) instanceof List)
						&& ((List) request.getRequest().get(JsonKey.TAGS)).isEmpty())) {
			throw new ProjectCommonException(
					ResponseCode.invalidTags.getErrorCode(),
					ResponseCode.invalidTags.getErrorMessage(),
					ERROR_CODE);
		} else if (request.getRequest().get(JsonKey.TAGS) instanceof String) {
			throw new ProjectCommonException(
					ResponseCode.invalidTags.getErrorCode(),
					ResponseCode.invalidTags.getErrorMessage(),
					ERROR_CODE);
		}
	}

	/**
	 * Method to validate noteId
	 *
	 * @param noteId
	 */
	public static void validateNoteId(String noteId) {
		if (StringUtils.isBlank(noteId)) {
			throw createExceptionInstance(ResponseCode.invalidNoteId.getErrorCode());
		}
	}

	/**
	 * Method to validate
	 *
	 * @param request
	 */
	public static void validateRegisterClient(Request request) {

		if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.CLIENT_NAME))) {
			throw createExceptionInstance(ResponseCode.invalidClientName.getErrorCode());
		}
	}

	/**
	 * Method to validate the request for updating the client key
	 *
	 * @param clientId
	 * @param masterAccessToken
	 */
	public static void validateUpdateClientKey(String clientId, String masterAccessToken) {
		validateClientId(clientId);
		if (StringUtils.isBlank(masterAccessToken)) {
			throw createExceptionInstance(ResponseCode.invalidRequestData.getErrorCode());
		}
	}

	/**
	 * Method to validate the request for updating the client key
	 *
	 * @param id
	 * @param type
	 */
	public static void validateGetClientKey(String id, String type) {
		validateClientId(id);
		if (StringUtils.isBlank(type)) {
			throw createExceptionInstance(ResponseCode.invalidRequestData.getErrorCode());
		}
	}

	/**
	 * Method to validate clientId.
	 *
	 * @param clientId
	 */
	public static void validateClientId(String clientId) {
		if (StringUtils.isBlank(clientId)) {
			throw createExceptionInstance(ResponseCode.invalidClientId.getErrorCode());
		}
	}

	/**
	 * Method to validate notification request data.
	 *
	 * @param request Request
	 */
	@SuppressWarnings("unchecked")
	public static void validateSendNotification(Request request) {
		if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.TO))) {
			throw createExceptionInstance(ResponseCode.invalidTopic.getErrorCode());
		}
		if (request.getRequest().get(JsonKey.DATA) == null
				|| !(request.getRequest().get(JsonKey.DATA) instanceof Map)
				|| ((Map<String, Object>) request.getRequest().get(JsonKey.DATA)).size() == 0) {
			throw createExceptionInstance(ResponseCode.invalidTopicData.getErrorCode());
		}

		if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.TYPE))) {
			throw createExceptionInstance(ResponseCode.invalidNotificationType.getErrorCode());
		}
		if (!(JsonKey.FCM.equalsIgnoreCase((String) request.getRequest().get(JsonKey.TYPE)))) {
			throw createExceptionInstance(ResponseCode.notificationTypeSupport.getErrorCode());
		}
	}

	@SuppressWarnings("rawtypes")
	public static void validateGetUserCount(Request request) {
		if (!validateListType(request, JsonKey.LOCATION_IDS)) {
			throw createDataTypeException(
					ResponseCode.dataTypeError.getErrorCode(), JsonKey.LOCATION_IDS, JsonKey.LIST);
		}
		if (null == request.getRequest().get(JsonKey.LOCATION_IDS)
				&& ((List) request.getRequest().get(JsonKey.LOCATION_IDS)).isEmpty()) {
			throw createExceptionInstance(ResponseCode.locationIdRequired.getErrorCode());
		}

		if (!validateBooleanType(request, JsonKey.USER_LIST_REQ)) {
			throw createDataTypeException(
					ResponseCode.dataTypeError.getErrorCode(), JsonKey.USER_LIST_REQ, "Boolean");
		}

		if (null != request.getRequest().get(JsonKey.USER_LIST_REQ)
				&& (Boolean) request.getRequest().get(JsonKey.USER_LIST_REQ)) {
			throw createExceptionInstance(ResponseCode.functionalityMissing.getErrorCode());
		}

		if (!validateBooleanType(request, JsonKey.ESTIMATED_COUNT_REQ)) {
			throw createDataTypeException(
					ResponseCode.dataTypeError.getErrorCode(), JsonKey.ESTIMATED_COUNT_REQ, "Boolean");
		}

		if (null != request.getRequest().get(JsonKey.ESTIMATED_COUNT_REQ)
				&& (Boolean) request.getRequest().get(JsonKey.ESTIMATED_COUNT_REQ)) {
			throw createExceptionInstance(ResponseCode.functionalityMissing.getErrorCode());
		}
	}

	/**
	 * if the request contains that key and key is not instance of List then it will return false.
	 * other cases it will return true.
	 *
	 * @param request Request
	 * @param key String
	 * @return boolean
	 */
	private static boolean validateListType(Request request, String key) {
		return !(request.getRequest().containsKey(key)
				&& null != request.getRequest().get(key)
				&& !(request.getRequest().get(key) instanceof List));
	}

	/**
	 * If the request contains the key and key value is not Boolean type then it will return false ,
	 * for any other case it will return true.
	 *
	 * @param request Request
	 * @param key String
	 * @return boolean
	 */
	private static boolean validateBooleanType(Request request, String key) {
		return !(request.getRequest().containsKey(key)
				&& null != request.getRequest().get(key)
				&& !(request.getRequest().get(key) instanceof Boolean));
	}

	private static ProjectCommonException createDataTypeException(
			String errorCode, String key1, String key2) {
		return new ProjectCommonException(
				ResponseCode.getResponse(errorCode).getErrorCode(),
				ProjectUtil.formatMessage(
						ResponseCode.getResponse(errorCode).getErrorMessage(), key1, key2),
				ERROR_CODE);
	}

	private static ProjectCommonException createExceptionInstance(String errorCode) {
		return new ProjectCommonException(
				ResponseCode.getResponse(errorCode).getErrorCode(),
				ResponseCode.getResponse(errorCode).getErrorMessage(),
				ERROR_CODE);
	}
}
