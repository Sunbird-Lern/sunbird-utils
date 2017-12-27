/**
 * 
 */
package org.sunbird.common.request;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * @author Manzarul
 *
 */
public class CommonRequestValidatorTest {

	@Test
	public void enrollCourseValidationSuccess() {
		Request request = new Request();
		boolean response = false;
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.COURSE_ID, "do_1233343");
		request.setRequest(requestObj);
		try {
			RequestValidator.validateEnrollCourse(request);
			response = true;
		} catch (ProjectCommonException e) {
			Assert.assertNull(e);
		}
		assertEquals(true, response);
	}

	@Test
	public void enrollCourseValidationWithOutCourseId() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		request.setRequest(requestObj);
		request.setRequest(requestObj);
		try {
			RequestValidator.validateEnrollCourse(request);
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.courseIdRequiredError.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateUpdateContentSuccess() {
		Request request = new Request();
		boolean response = false;
		List<Map<String, Object>> listOfMap = new ArrayList<>();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.CONTENT_ID, "do_1233343");
		requestObj.put(JsonKey.STATUS, "Completed");
		listOfMap.add(requestObj);
		Map<String, Object> innerMap = new HashMap<>();
		innerMap.put(JsonKey.CONTENTS, listOfMap);
		request.setRequest(innerMap);
		try {
			RequestValidator.validateUpdateContent(request);
			response = true;
		} catch (ProjectCommonException e) {
			Assert.assertNull(e);
		}
		assertEquals(true, response);
	}

	@Test
	public void validateUpdateContentWithContentIdAsNull() {
		Request request = new Request();
		boolean response = false;
		List<Map<String, Object>> listOfMap = new ArrayList<>();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.CONTENT_ID, null);
		requestObj.put(JsonKey.STATUS, "Completed");
		listOfMap.add(requestObj);
		Map<String, Object> innerMap = new HashMap<>();
		innerMap.put(JsonKey.CONTENTS, listOfMap);
		request.setRequest(innerMap);
		try {
			RequestValidator.validateUpdateContent(request);
			response = true;
		} catch (ProjectCommonException e) {
			Assert.assertNotNull(e);
		}
		assertEquals(false, response);
	}

	@Test
	public void validateUpdateContentWithOutContentId() {
		Request request = new Request();
		List<Map<String, Object>> listOfMap = new ArrayList<>();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.STATUS, "Completed");
		listOfMap.add(requestObj);
		Map<String, Object> innerMap = new HashMap<>();
		innerMap.put(JsonKey.CONTENTS, listOfMap);
		request.setRequest(innerMap);
		try {
			RequestValidator.validateUpdateContent(request);
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.contentIdRequiredError.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateUpdateContentWithOutStatus() {
		Request request = new Request();
		List<Map<String, Object>> listOfMap = new ArrayList<>();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.CONTENT_ID, "do_1233343");
		listOfMap.add(requestObj);
		Map<String, Object> innerMap = new HashMap<>();
		innerMap.put(JsonKey.CONTENTS, listOfMap);
		request.setRequest(innerMap);
		try {
			RequestValidator.validateUpdateContent(request);
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.contentStatusRequired.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateUpdateContentWithWrongType() {
		Request request = new Request();
		List<Map<String, Object>> listOfMap = new ArrayList<>();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.CONTENT_ID, "do_1233343");
		Map<String, Object> innerMap = new HashMap<>();
		innerMap.put(JsonKey.CONTENTS, listOfMap);
		request.setRequest(innerMap);
		try {
			RequestValidator.validateUpdateContent(request);
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.contentIdRequiredError.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateRegisterClientTest() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.CLIENT_NAME, "");
		request.setRequest(requestObj);
		try {
			RequestValidator.validateRegisterClient(request);
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.invalidClientName.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateRegisterClientSuccessTest() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.CLIENT_NAME, "1234");
		request.setRequest(requestObj);
		try {
			RequestValidator.validateRegisterClient(request);
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.invalidClientName.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateUpdateClientKeyTest() {
		try {
			RequestValidator.validateUpdateClientKey("1234", "");
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.invalidRequestData.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateUpdateClientKeyWithSuccessTest() {
		try {
			RequestValidator.validateUpdateClientKey("1234", "test123");
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.invalidRequestData.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateClientIdTest() {
		try {
			RequestValidator.validateClientId("");
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.invalidClientId.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateFileUploadTest() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.CONTAINER, "");
		request.setRequest(requestObj);
		try {
			RequestValidator.validateFileUpload(request);
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.storageContainerNameMandatory.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateSendMailRecipientUserTest() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.SUBJECT, "test123");
		requestObj.put(JsonKey.BODY, "test");
		List<String> data = new ArrayList<>();
		data.add("test123@gmail.com");
		requestObj.put(JsonKey.RECIPIENT_EMAILS, data);
		requestObj.put(JsonKey.RECIPIENT_USERIDS, new ArrayList<>());
		request.setRequest(requestObj);
		try {
			RequestValidator.validateSendMail(request);
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.recipientAddressError.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateSendMailRecipientEmailTest() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.SUBJECT, "test123");
		requestObj.put(JsonKey.BODY, "test");
		requestObj.put(JsonKey.RECIPIENT_EMAILS, null);
		request.setRequest(requestObj);
		try {
			RequestValidator.validateSendMail(request);
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.recipientAddressError.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateSendMailBodyTest() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.SUBJECT, "test123");
		requestObj.put(JsonKey.BODY, "");
		request.setRequest(requestObj);
		try {
			RequestValidator.validateSendMail(request);
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.emailBodyError.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateSendMailSubjectTest() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.SUBJECT, "");
		request.setRequest(requestObj);
		try {
			RequestValidator.validateSendMail(request);
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.emailSubjectError.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateEnrollmentTypeWithEmptyType() {
		try {
			RequestValidator.validateEnrolmentType("");
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.enrolmentTypeRequired.getErrorCode(), e.getCode());
		}

	}

	@Test
	public void validateEnrollmentTypeWithWrongType() {
		try {
			RequestValidator.validateEnrolmentType("test");
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.enrolmentIncorrectValue.getErrorCode(), e.getCode());
		}
	}

	@Test
	public void validateEnrollmentOpenType() {
		boolean response = false;
		try {
			RequestValidator.validateEnrolmentType(ProjectUtil.EnrolmentType.open.getVal());
			response = true;
		} catch (ProjectCommonException e) {
			Assert.assertNull(e);
		}
		Assert.assertTrue(response);
	}

	@Test
	public void validateEnrollmentInviteType() {
		boolean response = false;
		try {
			RequestValidator.validateEnrolmentType(ProjectUtil.EnrolmentType.inviteOnly.getVal());
			response = true;
		} catch (ProjectCommonException e) {
			Assert.assertNull(e);
		}
		Assert.assertTrue(response);
	}

	@Test
	public void validateAssessmentSuccess() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.COURSE_ID, "cours_09");
		requestObj.put(JsonKey.CONTENT_ID, "content09");
		requestObj.put(JsonKey.ATTEMPT_ID, "attempt_id");
		List<Map<String, Object>> assessmentList = new ArrayList<>();
		Map<String, Object> assessmentMap = new HashMap<>();
		assessmentMap.put(JsonKey.ASSESSMENT_ITEM_ID, "assessmentItemId");
		assessmentMap.put(JsonKey.ASSESSMENT_TYPE, "assessmentType");
		assessmentMap.put(JsonKey.ASSESSMENT_ANSWERS, "testAnswer");
		assessmentMap.put(JsonKey.ASSESSMENT_MAX_SCORE, "maxScore");
		assessmentList.add(assessmentMap);
		requestObj.put(JsonKey.ASSESSMENT, assessmentList);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSaveAssessment(request);
			response = true;
		} catch (ProjectCommonException e) {
			Assert.assertNull(e);
		}
		Assert.assertTrue(response);
	}

	@Test
	public void validateAssessmentWithOutCourseId() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.COURSE_ID, "");
		requestObj.put(JsonKey.CONTENT_ID, "content09");
		requestObj.put(JsonKey.ATTEMPT_ID, "attempt_id");
		List<Map<String, Object>> assessmentList = new ArrayList<>();
		Map<String, Object> assessmentMap = new HashMap<>();
		assessmentMap.put(JsonKey.ASSESSMENT_ITEM_ID, "assessmentItemId");
		assessmentMap.put(JsonKey.ASSESSMENT_TYPE, "assessmentType");
		assessmentMap.put(JsonKey.ASSESSMENT_ANSWERS, "testAnswer");
		assessmentMap.put(JsonKey.ASSESSMENT_MAX_SCORE, "maxScore");
		assessmentList.add(assessmentMap);
		requestObj.put(JsonKey.ASSESSMENT, assessmentList);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSaveAssessment(request);
			response = true;
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.courseIdRequired.getErrorCode(), e.getCode());
		}
		Assert.assertFalse(response);
	}

	@Test
	public void validateAssessmentWithOutContentId() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.COURSE_ID, "courseId");
		requestObj.put(JsonKey.CONTENT_ID, null);
		requestObj.put(JsonKey.ATTEMPT_ID, "attempt_id");
		List<Map<String, Object>> assessmentList = new ArrayList<>();
		Map<String, Object> assessmentMap = new HashMap<>();
		assessmentMap.put(JsonKey.ASSESSMENT_ITEM_ID, "assessmentItemId");
		assessmentMap.put(JsonKey.ASSESSMENT_TYPE, "assessmentType");
		assessmentMap.put(JsonKey.ASSESSMENT_ANSWERS, "testAnswer");
		assessmentMap.put(JsonKey.ASSESSMENT_MAX_SCORE, "maxScore");
		assessmentList.add(assessmentMap);
		requestObj.put(JsonKey.ASSESSMENT, assessmentList);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSaveAssessment(request);
			response = true;
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.contentIdRequired.getErrorCode(), e.getCode());
		}
		Assert.assertFalse(response);
	}

	@Test
	public void validateAssessmentWithOutattemptId() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.COURSE_ID, "courseId");
		requestObj.put(JsonKey.CONTENT_ID, "contentId");
		List<Map<String, Object>> assessmentList = new ArrayList<>();
		Map<String, Object> assessmentMap = new HashMap<>();
		assessmentMap.put(JsonKey.ASSESSMENT_ITEM_ID, "assessmentItemId");
		assessmentMap.put(JsonKey.ASSESSMENT_TYPE, "assessmentType");
		assessmentMap.put(JsonKey.ASSESSMENT_ANSWERS, "testAnswer");
		assessmentMap.put(JsonKey.ASSESSMENT_MAX_SCORE, "maxScore");
		assessmentList.add(assessmentMap);
		requestObj.put(JsonKey.ASSESSMENT, assessmentList);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSaveAssessment(request);
			response = true;
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.attemptIdRequired.getErrorCode(), e.getCode());
		}
		Assert.assertFalse(response);
	}

	@Test
	public void validateAssessmentWithOutatassessmentItemId() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.COURSE_ID, "courseId");
		requestObj.put(JsonKey.CONTENT_ID, "contentId");
		requestObj.put(JsonKey.ATTEMPT_ID, "attempt_id");
		List<Map<String, Object>> assessmentList = new ArrayList<>();
		Map<String, Object> assessmentMap = new HashMap<>();
		assessmentMap.put(JsonKey.ASSESSMENT_TYPE, "assessmentType");
		assessmentMap.put(JsonKey.ASSESSMENT_ANSWERS, "testAnswer");
		assessmentMap.put(JsonKey.ASSESSMENT_MAX_SCORE, "maxScore");
		assessmentList.add(assessmentMap);
		requestObj.put(JsonKey.ASSESSMENT, assessmentList);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSaveAssessment(request);
			response = true;
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.assessmentItemIdRequired.getErrorCode(), e.getCode());
		}
		Assert.assertFalse(response);
	}

	@Test
	public void validateAssessmentWithOutatassessmentType() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.COURSE_ID, "courseId");
		requestObj.put(JsonKey.CONTENT_ID, "contentId");
		requestObj.put(JsonKey.ATTEMPT_ID, "attempt_id");
		List<Map<String, Object>> assessmentList = new ArrayList<>();
		Map<String, Object> assessmentMap = new HashMap<>();
		assessmentMap.put(JsonKey.ASSESSMENT_ITEM_ID, "assessmentItemId");
		assessmentMap.put(JsonKey.ASSESSMENT_ANSWERS, "testAnswer");
		assessmentMap.put(JsonKey.ASSESSMENT_MAX_SCORE, "maxScore");
		assessmentList.add(assessmentMap);
		requestObj.put(JsonKey.ASSESSMENT, assessmentList);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSaveAssessment(request);
			response = true;
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.assessmentTypeRequired.getErrorCode(), e.getCode());
		}
		Assert.assertFalse(response);
	}

	@Test
	public void validateAssessmentWithOutatassessmentAns() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.COURSE_ID, "courseId");
		requestObj.put(JsonKey.CONTENT_ID, "contentId");
		requestObj.put(JsonKey.ATTEMPT_ID, "attempt_id");
		List<Map<String, Object>> assessmentList = new ArrayList<>();
		Map<String, Object> assessmentMap = new HashMap<>();
		assessmentMap.put(JsonKey.ASSESSMENT_ITEM_ID, "assessmentItemId");
		assessmentMap.put(JsonKey.ASSESSMENT_TYPE, "assessmentType");
		assessmentMap.put(JsonKey.ASSESSMENT_MAX_SCORE, "maxScore");
		assessmentList.add(assessmentMap);
		requestObj.put(JsonKey.ASSESSMENT, assessmentList);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSaveAssessment(request);
			response = true;
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.assessmentAnswersRequired.getErrorCode(), e.getCode());
		}
		Assert.assertFalse(response);
	}

	@Test
	public void validateAssessmentWithOutatassessmentScore() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.COURSE_ID, "courseId");
		requestObj.put(JsonKey.CONTENT_ID, "contentId");
		requestObj.put(JsonKey.ATTEMPT_ID, "attempt_id");
		List<Map<String, Object>> assessmentList = new ArrayList<>();
		Map<String, Object> assessmentMap = new HashMap<>();
		assessmentMap.put(JsonKey.ASSESSMENT_ITEM_ID, "assessmentItemId");
		assessmentMap.put(JsonKey.ASSESSMENT_TYPE, "assessmentType");
		assessmentMap.put(JsonKey.ASSESSMENT_ANSWERS, "testAnswer");
		// assessmentMap.put(JsonKey.ASSESSMENT_MAX_SCORE, "maxScore");
		assessmentList.add(assessmentMap);
		requestObj.put(JsonKey.ASSESSMENT, assessmentList);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSaveAssessment(request);
			response = true;
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.assessmentmaxScoreRequired.getErrorCode(), e.getCode());
		}
		Assert.assertFalse(response);
	}

	@Test
	public void validateAssessmentWithOutatassessmentList() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.COURSE_ID, "courseId");
		requestObj.put(JsonKey.CONTENT_ID, "contentId");
		requestObj.put(JsonKey.ATTEMPT_ID, "attempt_id");
		List<Map<String, Object>> assessmentList = new ArrayList<>();
		Map<String, Object> assessmentMap = new HashMap<>();
		assessmentMap.put(JsonKey.ASSESSMENT_ITEM_ID, "assessmentItemId");
		assessmentMap.put(JsonKey.ASSESSMENT_TYPE, "assessmentType");
		assessmentMap.put(JsonKey.ASSESSMENT_ANSWERS, "testAnswer");
		assessmentMap.put(JsonKey.ASSESSMENT_MAX_SCORE, "maxScore");
		assessmentList.add(assessmentMap);
		requestObj.put(JsonKey.ASSESSMENT, null);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSaveAssessment(request);
			response = true;
		} catch (ProjectCommonException e) {
			
		}
		Assert.assertTrue(response);
	}

	@Test
	public void validateKeyuclaockSyncRequest() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.OPERATION_FOR, "keycloak");
		requestObj.put(JsonKey.OBJECT_TYPE, JsonKey.USER);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSyncRequest(request);
			response = true;
		} catch (ProjectCommonException e) {
			Assert.assertNull(e);
		}
		Assert.assertTrue(response);
	}
	
	@Test
	public void validateSyncRequestwithInvalidObjType() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.OPERATION_FOR, "not keycloack");
		requestObj.put(JsonKey.OBJECT_TYPE, null);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSyncRequest(request);
			response = true;
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
		}
		Assert.assertFalse(response);
	}
	
	@Test
	public void validateSyncRequestwithInvalidObjTypeValue() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.OPERATION_FOR, "not keycloack");
		List<String> objectLsit = new ArrayList<>();
		objectLsit.add("testval");
		requestObj.put(JsonKey.OBJECT_TYPE, objectLsit);
		request.setRequest(requestObj);
		boolean response = false;
		try {
			RequestValidator.validateSyncRequest(request);
			response = true;
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.invalidObjectType.getErrorCode(), e.getCode());
		}
		Assert.assertFalse(response);
	}
	
}
