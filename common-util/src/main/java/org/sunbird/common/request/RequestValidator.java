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
import org.sunbird.common.models.util.ProjectUtil.AddressType;
import org.sunbird.common.models.util.ProjectUtil.ProgressStatus;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;


/**
 * This call will do validation for all incoming request data.
 * 
 * @author Manzarul
 */
public final class RequestValidator {
  private static final int ERROR_CODE = ResponseCode.CLIENT_ERROR.getResponseCode();

  /**
   * This method will do course enrollment request data validation. if all mandatory data is coming
   * then it won't do any thing if any mandatory data is missing then it will throw exception.
   * 
   * @param courseRequestDto CourseRequestDto
   */
  public static void validateEnrollCourse(Request courseRequestDto) {
    if (courseRequestDto.getRequest().get(JsonKey.COURSE_ID) == null) {
      throw new ProjectCommonException(ResponseCode.courseIdRequiredError.getErrorCode(),
          ResponseCode.courseIdRequiredError.getErrorMessage(), ERROR_CODE);
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
        .size() == 0) {
      throw new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
          ResponseCode.contentIdRequiredError.getErrorMessage(), ERROR_CODE);
    } else {
      List<Map<String, Object>> list =
          (List<Map<String, Object>>) (contentRequestDto.getRequest().get(JsonKey.CONTENTS));
      for (Map<String, Object> map : list) {
        if (map.containsKey(JsonKey.CONTENT_ID)) {

          if (null == map.get(JsonKey.CONTENT_ID)) {
            throw new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
                ResponseCode.contentIdRequiredError.getErrorMessage(), ERROR_CODE);
          }
          if (ProjectUtil.isNull(map.get(JsonKey.STATUS))) {
            throw new ProjectCommonException(ResponseCode.contentStatusRequired.getErrorCode(),
                ResponseCode.contentStatusRequired.getErrorMessage(), ERROR_CODE);
          }

        } else {
          throw new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
              ResponseCode.contentIdRequiredError.getErrorMessage(), ERROR_CODE);
        }
      }
    }
  }


  /**
   * This method will validate create user data.
   * 
   * @param userRequest Request
   */
  @SuppressWarnings("unchecked")
  public static void validateCreateUser(Request userRequest) {
    Map<String, Object> addrReqMap = null;
    Map<String, Object> reqMap = null;
    phoneAndEmailValidationForCreateUser(userRequest);
    doUserBasicValidation(userRequest);
    if (userRequest.getRequest().containsKey(JsonKey.ADDRESS)
        && null != userRequest.getRequest().get(JsonKey.ADDRESS)) {
      if (!(userRequest.getRequest().get(JsonKey.ADDRESS) instanceof List)) {
        throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
            ProjectUtil.formatMessage(ResponseCode.dataTypeError.getErrorMessage(), JsonKey.ADDRESS,
                JsonKey.LIST),
            ERROR_CODE);
      } else if (userRequest.getRequest().get(JsonKey.ADDRESS) instanceof List) {
        List<Map<String, Object>> reqList =
            (List<Map<String, Object>>) userRequest.get(JsonKey.ADDRESS);
        for (int i = 0; i < reqList.size(); i++) {
          addrReqMap = reqList.get(i);
          validateAddress(addrReqMap, JsonKey.ADDRESS);
        }
      }
    }

    if (userRequest.getRequest().containsKey(JsonKey.EDUCATION)
        && null != userRequest.getRequest().get(JsonKey.EDUCATION)) {
      if (!(userRequest.getRequest().get(JsonKey.EDUCATION) instanceof List)) {
        throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
            ProjectUtil.formatMessage(ResponseCode.dataTypeError.getErrorMessage(),
                JsonKey.EDUCATION, JsonKey.LIST),
            ERROR_CODE);
      } else if (userRequest.getRequest().get(JsonKey.EDUCATION) instanceof List) {
        List<Map<String, Object>> reqList =
            (List<Map<String, Object>>) userRequest.get(JsonKey.EDUCATION);
        for (int i = 0; i < reqList.size(); i++) {
          reqMap = reqList.get(i);
          if (ProjectUtil.isStringNullOREmpty((String) reqMap.get(JsonKey.NAME))) {
            throw new ProjectCommonException(ResponseCode.educationNameError.getErrorCode(),
                ResponseCode.educationNameError.getErrorMessage(), ERROR_CODE);
          }
          if (ProjectUtil.isStringNullOREmpty((String) reqMap.get(JsonKey.DEGREE))) {
            throw new ProjectCommonException(ResponseCode.educationDegreeError.getErrorCode(),
                ResponseCode.educationDegreeError.getErrorMessage(), ERROR_CODE);
          }
          if (reqMap.containsKey(JsonKey.ADDRESS) && null != reqMap.get(JsonKey.ADDRESS)) {
            addrReqMap = (Map<String, Object>) reqMap.get(JsonKey.ADDRESS);
            validateAddress(addrReqMap, JsonKey.EDUCATION);
          }
        }
      }
    }

    if (userRequest.getRequest().containsKey(JsonKey.JOB_PROFILE)
        && null != userRequest.getRequest().get(JsonKey.JOB_PROFILE)) {
      if (!(userRequest.getRequest().get(JsonKey.JOB_PROFILE) instanceof List)) {
        throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
            ProjectUtil.formatMessage(ResponseCode.dataTypeError.getErrorMessage(),
                JsonKey.JOB_PROFILE, JsonKey.LIST),
            ERROR_CODE);
      } else if (userRequest.getRequest().get(JsonKey.JOB_PROFILE) instanceof List) {
        List<Map<String, Object>> reqList =
            (List<Map<String, Object>>) userRequest.get(JsonKey.JOB_PROFILE);
        for (int i = 0; i < reqList.size(); i++) {
          reqMap = reqList.get(i);
          if (ProjectUtil.isStringNullOREmpty((String) reqMap.get(JsonKey.JOB_NAME))) {
            throw new ProjectCommonException(ResponseCode.jobNameError.getErrorCode(),
                ResponseCode.jobNameError.getErrorMessage(), ERROR_CODE);
          }
          if (ProjectUtil.isStringNullOREmpty((String) reqMap.get(JsonKey.ORG_NAME))) {
            throw new ProjectCommonException(ResponseCode.organisationNameError.getErrorCode(),
                ResponseCode.organisationNameError.getErrorMessage(), ERROR_CODE);
          }
          if (reqMap.containsKey(JsonKey.ADDRESS) && null != reqMap.get(JsonKey.ADDRESS)) {
            addrReqMap = (Map<String, Object>) reqMap.get(JsonKey.ADDRESS);
            validateAddress(addrReqMap, JsonKey.JOB_PROFILE);
          }
        }
      }
    }
    validateWebPages(userRequest);
  }

  private static void phoneAndEmailValidationForCreateUser(Request userRequest) {
    if (!ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PHONE))) {
      String phone = (String) userRequest.getRequest().get(JsonKey.PHONE);
      validatePhoneNo(phone, (String) userRequest.getRequest().get(JsonKey.COUNTRY_CODE));
    }
    if (!ProjectUtil
        .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.COUNTRY_CODE))) {
      boolean bool = ProjectUtil
          .validateCountryCode((String) userRequest.getRequest().get(JsonKey.COUNTRY_CODE));
      if (!bool) {
        throw new ProjectCommonException(ResponseCode.invalidCountryCode.getErrorCode(),
            ResponseCode.invalidCountryCode.getErrorMessage(), ERROR_CODE);
      }
    }
    if (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.EMAIL))
        && ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PHONE))) {
      throw new ProjectCommonException(ResponseCode.emailorPhoneRequired.getErrorCode(),
          ResponseCode.emailorPhoneRequired.getErrorMessage(), ERROR_CODE);
    }

    // Email is always mandatory for both External as well as our internal portal
    /*
     * if (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.EMAIL))) {
     * throw new ProjectCommonException(ResponseCode.emailRequired.getErrorCode(),
     * ResponseCode.emailRequired.getErrorMessage(), ERROR_CODE); }
     */
    if (!ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.EMAIL))
        && !ProjectUtil.isEmailvalid((String) userRequest.getRequest().get(JsonKey.EMAIL))) {
      throw new ProjectCommonException(ResponseCode.emailFormatError.getErrorCode(),
          ResponseCode.emailFormatError.getErrorMessage(), ERROR_CODE);
    }
    if (!ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PROVIDER))) {
      if (!ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PHONE))) {
        if (null != userRequest.getRequest().get(JsonKey.PHONE_VERIFIED)) {
          if (userRequest.getRequest().get(JsonKey.PHONE_VERIFIED) instanceof Boolean) {
            if (!((boolean) userRequest.getRequest().get(JsonKey.PHONE_VERIFIED))) {
              throw new ProjectCommonException(ResponseCode.phoneVerifiedError.getErrorCode(),
                  ResponseCode.phoneVerifiedError.getErrorMessage(), ERROR_CODE);
            }
          } else {
            throw new ProjectCommonException(ResponseCode.phoneVerifiedError.getErrorCode(),
                ResponseCode.phoneVerifiedError.getErrorMessage(), ERROR_CODE);
          }
        } else {
          throw new ProjectCommonException(ResponseCode.phoneVerifiedError.getErrorCode(),
              ResponseCode.phoneVerifiedError.getErrorMessage(), ERROR_CODE);
        }
      }
      /*
       * if (null == userRequest.getRequest().get(JsonKey.EMAIL_VERIFIED) || !((boolean)
       * userRequest.getRequest().get(JsonKey.EMAIL_VERIFIED))) { throw new
       * ProjectCommonException(ResponseCode.emailVerifiedError.getErrorCode(),
       * ResponseCode.emailVerifiedError.getErrorMessage(), ERROR_CODE); }
       */

    }
  }

  private static boolean validatePhoneNo(String phone, String countryCode) {
    if (phone.contains("+")) {
      throw new ProjectCommonException(ResponseCode.invalidPhoneNumber.getErrorCode(),
          ResponseCode.invalidPhoneNumber.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil.validatePhone(phone, countryCode)) {
      return true;
    } else {
      throw new ProjectCommonException(ResponseCode.phoneNoFormatError.getErrorCode(),
          ResponseCode.phoneNoFormatError.getErrorMessage(), ERROR_CODE);
    }
  }

  public static void phoneAndEmailValidationForUpdateUser(Request userRequest) {
    if (!ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PHONE))) {
      validatePhoneNo((String) userRequest.getRequest().get(JsonKey.PHONE),
          (String) userRequest.getRequest().get(JsonKey.COUNTRY_CODE));
    }
    if (!ProjectUtil
        .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.COUNTRY_CODE))) {
      boolean bool = ProjectUtil
          .validateCountryCode((String) userRequest.getRequest().get(JsonKey.COUNTRY_CODE));
      if (!bool) {
        throw new ProjectCommonException(ResponseCode.invalidCountryCode.getErrorCode(),
            ResponseCode.invalidCountryCode.getErrorMessage(), ERROR_CODE);
      }
    }
    if (!ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PROVIDER))) {

      if (!ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.EMAIL))) {

        if (!ProjectUtil.isEmailvalid((String) userRequest.getRequest().get(JsonKey.EMAIL))) {
          throw new ProjectCommonException(ResponseCode.emailFormatError.getErrorCode(),
              ResponseCode.emailFormatError.getErrorMessage(), ERROR_CODE);
        }
        /*
         * if (null == userRequest.getRequest().get(JsonKey.EMAIL_VERIFIED) || !((boolean)
         * userRequest.getRequest().get(JsonKey.EMAIL_VERIFIED))) { throw new
         * ProjectCommonException(ResponseCode.emailVerifiedError.getErrorCode(),
         * ResponseCode.emailVerifiedError.getErrorMessage(), ERROR_CODE); }
         */
      }

      if (!ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PHONE))) {
        if (null != userRequest.getRequest().get(JsonKey.PHONE_VERIFIED)) {
          if (userRequest.getRequest().get(JsonKey.PHONE_VERIFIED) instanceof Boolean) {
            if (!((boolean) userRequest.getRequest().get(JsonKey.PHONE_VERIFIED))) {
              throw new ProjectCommonException(ResponseCode.phoneVerifiedError.getErrorCode(),
                  ResponseCode.phoneVerifiedError.getErrorMessage(), ERROR_CODE);
            }
          } else {
            throw new ProjectCommonException(ResponseCode.phoneVerifiedError.getErrorCode(),
                ResponseCode.phoneVerifiedError.getErrorMessage(), ERROR_CODE);
          }
        } else {
          throw new ProjectCommonException(ResponseCode.phoneVerifiedError.getErrorCode(),
              ResponseCode.phoneVerifiedError.getErrorMessage(), ERROR_CODE);
        }
      }
    }
  }

  /**
   * This method will do basic validation for user request object.
   * 
   * @param userRequest
   */
  public static void doUserBasicValidation(Request userRequest) {

    if (userRequest.getRequest().get(JsonKey.USERNAME) == null) {
      throw new ProjectCommonException(ResponseCode.userNameRequired.getErrorCode(),
          ResponseCode.userNameRequired.getErrorMessage(), ERROR_CODE);
    }

    if (userRequest.getRequest().get(JsonKey.FIRST_NAME) == null || (ProjectUtil
        .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.FIRST_NAME)))) {
      throw new ProjectCommonException(ResponseCode.firstNameRequired.getErrorCode(),
          ResponseCode.firstNameRequired.getErrorMessage(), ERROR_CODE);
    }
    if (userRequest.getRequest().containsKey(JsonKey.ROLES)
        && null != userRequest.getRequest().get(JsonKey.ROLES)
        && !(userRequest.getRequest().get(JsonKey.ROLES) instanceof List)) {
      throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(), ProjectUtil
          .formatMessage(ResponseCode.dataTypeError.getErrorMessage(), JsonKey.ROLES, JsonKey.LIST),
          ERROR_CODE);
    }
    if (userRequest.getRequest().containsKey(JsonKey.LANGUAGE)
        && null != userRequest.getRequest().get(JsonKey.LANGUAGE)
        && !(userRequest.getRequest().get(JsonKey.LANGUAGE) instanceof List)) {
      throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
          ProjectUtil.formatMessage(ResponseCode.dataTypeError.getErrorMessage(), JsonKey.LANGUAGE,
              JsonKey.LIST),
          ERROR_CODE);
    }
  }

  public static void validateCreateOrg(Request request) {
    if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.ORG_NAME))) {
      throw new ProjectCommonException(ResponseCode.organisationNameRequired.getErrorCode(),
          ResponseCode.organisationNameRequired.getErrorMessage(), ERROR_CODE);
    }
    if (null != request.getRequest().get(JsonKey.IS_ROOT_ORG)
        && (Boolean) request.getRequest().get(JsonKey.IS_ROOT_ORG)) {
      if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.CHANNEL))) {
        throw new ProjectCommonException(ResponseCode.channelIdRequiredForRootOrg.getErrorCode(),
            ResponseCode.channelIdRequiredForRootOrg.getErrorMessage(),
            ResponseCode.CLIENT_ERROR.getResponseCode());
      }
    }
  }

  public static void validateOrg(Request request) {
    if (ProjectUtil
        .isStringNullOREmpty((String) request.getRequest().get(JsonKey.ORGANISATION_ID))) {
      if ((ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.PROVIDER)))
          && (ProjectUtil
              .isStringNullOREmpty((String) request.getRequest().get(JsonKey.EXTERNAL_ID)))) {
        throw new ProjectCommonException(
            ResponseCode.sourceAndExternalIdValidationError.getErrorCode(),
            ResponseCode.sourceAndExternalIdValidationError.getErrorMessage(), ERROR_CODE);
      }
    }
  }

  public static void validateUpdateOrg(Request request) {
    validateOrg(request);
    if (request.getRequest().containsKey(JsonKey.ROOT_ORG_ID)) {
      if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.ROOT_ORG_ID))) {
        throw new ProjectCommonException(ResponseCode.invalidRootOrganisationId.getErrorCode(),
            ResponseCode.invalidRootOrganisationId.getErrorMessage(), ERROR_CODE);
      }
    }
    if (request.getRequest().get(JsonKey.STATUS) != null) {
      throw new ProjectCommonException(ResponseCode.invalidRequestData.getErrorCode(),
          ResponseCode.invalidRequestData.getErrorMessage(), ERROR_CODE);
    }
    if (null != request.getRequest().get(JsonKey.IS_ROOT_ORG)
        && (Boolean) request.getRequest().get(JsonKey.IS_ROOT_ORG)) {
      if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.CHANNEL))) {
        throw new ProjectCommonException(ResponseCode.channelIdRequiredForRootOrg.getErrorCode(),
            ResponseCode.channelIdRequiredForRootOrg.getErrorMessage(),
            ResponseCode.CLIENT_ERROR.getResponseCode());
      }
    }
  }

  public static void validateUpdateOrgStatus(Request request) {
    validateOrg(request);
    if (!request.getRequest().containsKey(JsonKey.STATUS)) {
      throw new ProjectCommonException(ResponseCode.invalidRequestData.getErrorCode(),
          ResponseCode.invalidRequestData.getErrorMessage(), ERROR_CODE);
    }
    if (!(request.getRequest().get(JsonKey.STATUS) instanceof BigInteger)) {
      throw new ProjectCommonException(ResponseCode.invalidRequestData.getErrorCode(),
          ResponseCode.invalidRequestData.getErrorMessage(), ERROR_CODE);
    }
  }


  /**
   * This method will validate update user data.
   * 
   * @param userRequest Request
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void validateUpdateUser(Request userRequest) {

    phoneAndEmailValidationForUpdateUser(userRequest);
    if (userRequest.getRequest().containsKey(JsonKey.FIRST_NAME) && (ProjectUtil
        .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.FIRST_NAME)))) {
      throw new ProjectCommonException(ResponseCode.firstNameRequired.getErrorCode(),
          ResponseCode.firstNameRequired.getErrorMessage(), ERROR_CODE);
    }

    if (userRequest.getRequest().containsKey(JsonKey.EMAIL)
        && userRequest.getRequest().get(JsonKey.EMAIL) != null) {
      if (!ProjectUtil.isEmailvalid((String) userRequest.getRequest().get(JsonKey.EMAIL))) {
        throw new ProjectCommonException(ResponseCode.emailFormatError.getErrorCode(),
            ResponseCode.emailFormatError.getErrorMessage(), ERROR_CODE);
      }
    }

    if (userRequest.getRequest().containsKey(JsonKey.ROLES)
        && null != userRequest.getRequest().get(JsonKey.ROLES)) {
      if (!(userRequest.getRequest().get(JsonKey.ROLES) instanceof List)) {
        throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
            ProjectUtil.formatMessage(ResponseCode.dataTypeError.getErrorMessage(), JsonKey.ROLES,
                JsonKey.LIST),
            ERROR_CODE);
      }
      if (userRequest.getRequest().get(JsonKey.ROLES) instanceof List
          && ((List) userRequest.getRequest().get(JsonKey.ROLES)).isEmpty()) {
        throw new ProjectCommonException(ResponseCode.rolesRequired.getErrorCode(),
            ResponseCode.rolesRequired.getErrorMessage(), ERROR_CODE);
      }
    }
    if (userRequest.getRequest().containsKey(JsonKey.LANGUAGE)
        && null != userRequest.getRequest().get(JsonKey.LANGUAGE)) {
      if (!(userRequest.getRequest().get(JsonKey.LANGUAGE) instanceof List)) {
        throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
            ProjectUtil.formatMessage(ResponseCode.dataTypeError.getErrorMessage(),
                JsonKey.LANGUAGE, JsonKey.LIST),
            ERROR_CODE);
      }
      if (userRequest.getRequest().get(JsonKey.LANGUAGE) instanceof List
          && ((List) userRequest.getRequest().get(JsonKey.LANGUAGE)).isEmpty()) {
        throw new ProjectCommonException(ResponseCode.languageRequired.getErrorCode(),
            ResponseCode.languageRequired.getErrorMessage(), ERROR_CODE);
      }
    }
    if (userRequest.getRequest().get(JsonKey.ADDRESS) != null
        && ((List) userRequest.getRequest().get(JsonKey.ADDRESS)).isEmpty()) {
      throw new ProjectCommonException(ResponseCode.addressRequired.getErrorCode(),
          ResponseCode.addressRequired.getErrorMessage(), ERROR_CODE);
    }
    if (userRequest.getRequest().get(JsonKey.EDUCATION) != null
        && ((List) userRequest.getRequest().get(JsonKey.EDUCATION)).isEmpty()) {
      throw new ProjectCommonException(ResponseCode.educationRequired.getErrorCode(),
          ResponseCode.educationRequired.getErrorMessage(), ERROR_CODE);
    }
    if (userRequest.getRequest().get(JsonKey.JOB_PROFILE) != null
        && ((List) userRequest.getRequest().get(JsonKey.JOB_PROFILE)).isEmpty()) {
      throw new ProjectCommonException(ResponseCode.jobDetailsRequired.getErrorCode(),
          ResponseCode.jobDetailsRequired.getErrorMessage(), ERROR_CODE);
    }

    if (userRequest.getRequest().get(JsonKey.ADDRESS) != null
        && (!((List) userRequest.getRequest().get(JsonKey.ADDRESS)).isEmpty())) {

      List<Map<String, Object>> reqList =
          (List<Map<String, Object>>) userRequest.get(JsonKey.ADDRESS);
      for (int i = 0; i < reqList.size(); i++) {
        Map<String, Object> reqMap = reqList.get(i);

        if (reqMap.containsKey(JsonKey.IS_DELETED) && null != reqMap.get(JsonKey.IS_DELETED)
            && ((boolean) reqMap.get(JsonKey.IS_DELETED))
            && ProjectUtil.isStringNullOREmpty((String) reqMap.get(JsonKey.ID))) {
          throw new ProjectCommonException(ResponseCode.idRequired.getErrorCode(),
              ResponseCode.idRequired.getErrorMessage(), ERROR_CODE);
        }
        if (!reqMap.containsKey(JsonKey.IS_DELETED)
            || (reqMap.containsKey(JsonKey.IS_DELETED) && (null == reqMap.get(JsonKey.IS_DELETED)
                || !(boolean) reqMap.get(JsonKey.IS_DELETED)))) {
          validateAddress(reqMap, JsonKey.ADDRESS);
        }
      }
    }

    if (userRequest.getRequest().get(JsonKey.JOB_PROFILE) != null
        && (!((List) userRequest.getRequest().get(JsonKey.JOB_PROFILE)).isEmpty())) {

      List<Map<String, Object>> reqList =
          (List<Map<String, Object>>) userRequest.get(JsonKey.JOB_PROFILE);
      for (int i = 0; i < reqList.size(); i++) {
        Map<String, Object> reqMap = reqList.get(i);
        if (reqMap.containsKey(JsonKey.IS_DELETED) && null != reqMap.get(JsonKey.IS_DELETED)
            && ((boolean) reqMap.get(JsonKey.IS_DELETED))
            && ProjectUtil.isStringNullOREmpty((String) reqMap.get(JsonKey.ID))) {
          throw new ProjectCommonException(ResponseCode.idRequired.getErrorCode(),
              ResponseCode.idRequired.getErrorMessage(), ERROR_CODE);
        }
        if (!reqMap.containsKey(JsonKey.IS_DELETED)
            || (reqMap.containsKey(JsonKey.IS_DELETED) && (null == reqMap.get(JsonKey.IS_DELETED)
                || !(boolean) reqMap.get(JsonKey.IS_DELETED)))) {
          if (ProjectUtil.isStringNullOREmpty((String) reqMap.get(JsonKey.JOB_NAME))) {
            throw new ProjectCommonException(ResponseCode.jobNameError.getErrorCode(),
                ResponseCode.jobNameError.getErrorMessage(), ERROR_CODE);
          }
          if (ProjectUtil.isStringNullOREmpty((String) reqMap.get(JsonKey.ORG_NAME))) {
            throw new ProjectCommonException(ResponseCode.organisationNameError.getErrorCode(),
                ResponseCode.organisationNameError.getErrorMessage(), ERROR_CODE);
          }

          if (reqMap.containsKey(JsonKey.ADDRESS) && null != reqMap.get(JsonKey.ADDRESS)) {
            validateAddress((Map<String, Object>) reqMap.get(JsonKey.ADDRESS), JsonKey.JOB_PROFILE);
          }
        }
      }
    }
    if (userRequest.getRequest().get(JsonKey.EDUCATION) != null
        && (!((List) userRequest.getRequest().get(JsonKey.EDUCATION)).isEmpty())) {

      List<Map<String, Object>> reqList =
          (List<Map<String, Object>>) userRequest.get(JsonKey.EDUCATION);
      for (int i = 0; i < reqList.size(); i++) {
        Map<String, Object> reqMap = reqList.get(i);
        if (reqMap.containsKey(JsonKey.IS_DELETED) && null != reqMap.get(JsonKey.IS_DELETED)
            && ((boolean) reqMap.get(JsonKey.IS_DELETED))
            && ProjectUtil.isStringNullOREmpty((String) reqMap.get(JsonKey.ID))) {
          throw new ProjectCommonException(ResponseCode.idRequired.getErrorCode(),
              ResponseCode.idRequired.getErrorMessage(), ERROR_CODE);
        }
        if (!reqMap.containsKey(JsonKey.IS_DELETED)
            || (reqMap.containsKey(JsonKey.IS_DELETED) && (null == reqMap.get(JsonKey.IS_DELETED)
                || !(boolean) reqMap.get(JsonKey.IS_DELETED)))) {
          if (ProjectUtil.isStringNullOREmpty((String) reqMap.get(JsonKey.NAME))) {
            throw new ProjectCommonException(ResponseCode.educationNameError.getErrorCode(),
                ResponseCode.educationNameError.getErrorMessage(), ERROR_CODE);
          }
          if (ProjectUtil.isStringNullOREmpty((String) reqMap.get(JsonKey.DEGREE))) {
            throw new ProjectCommonException(ResponseCode.educationDegreeError.getErrorCode(),
                ResponseCode.educationDegreeError.getErrorMessage(), ERROR_CODE);
          }
          if (reqMap.containsKey(JsonKey.ADDRESS) && null != reqMap.get(JsonKey.ADDRESS)) {
            validateAddress((Map<String, Object>) reqMap.get(JsonKey.ADDRESS), JsonKey.EDUCATION);
          }
        }
      }
    }
    if (userRequest.getRequest().containsKey(JsonKey.ROOT_ORG_ID)) {
      if (ProjectUtil
          .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.ROOT_ORG_ID))) {
        throw new ProjectCommonException(ResponseCode.invalidRootOrganisationId.getErrorCode(),
            ResponseCode.invalidRootOrganisationId.getErrorMessage(), ERROR_CODE);
      }
    }
  }

  /**
   * This method will validate user login data.
   * 
   * @param userRequest Request
   */
  public static void validateUserLogin(Request userRequest) {
    if (userRequest.getRequest().get(JsonKey.USERNAME) == null) {
      throw new ProjectCommonException(ResponseCode.userNameRequired.getErrorCode(),
          ResponseCode.userNameRequired.getErrorMessage(), ERROR_CODE);
    }
    if (userRequest.getRequest().get(JsonKey.PASSWORD) == null || (ProjectUtil
        .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PASSWORD)))) {
      throw new ProjectCommonException(ResponseCode.passwordRequired.getErrorCode(),
          ResponseCode.passwordRequired.getErrorMessage(), ERROR_CODE);
    }
    if (userRequest.getRequest().get(JsonKey.SOURCE) == null || (ProjectUtil
        .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PASSWORD)))) {
      throw new ProjectCommonException(ResponseCode.sourceRequired.getErrorCode(),
          ResponseCode.sourceRequired.getErrorMessage(), ERROR_CODE);
    }
  }

  /**
   * This method will validate change password requested data.
   * 
   * @param userRequest Request
   */
  public static void validateChangePassword(Request userRequest) {
    if (userRequest.getRequest().get(JsonKey.PASSWORD) == null || (ProjectUtil
        .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.PASSWORD)))) {
      throw new ProjectCommonException(ResponseCode.passwordRequired.getErrorCode(),
          ResponseCode.passwordRequired.getErrorMessage(), ERROR_CODE);
    }
    if (userRequest.getRequest().get(JsonKey.NEW_PASSWORD) == null) {
      throw new ProjectCommonException(ResponseCode.newPasswordRequired.getErrorCode(),
          ResponseCode.newPasswordRequired.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil
        .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.NEW_PASSWORD))) {
      throw new ProjectCommonException(ResponseCode.newPasswordEmpty.getErrorCode(),
          ResponseCode.newPasswordEmpty.getErrorMessage(), ERROR_CODE);
    }
    if (((String) userRequest.getRequest().get(JsonKey.NEW_PASSWORD))
        .equals((String) userRequest.getRequest().get(JsonKey.PASSWORD))) {

    }
  }

  /**
   * This method will validate get page data api.
   * 
   * @param request Request
   */
  public static void validateGetPageData(Request request) {
    if (request == null
        || (ProjectUtil.isStringNullOREmpty((String) request.get(JsonKey.SOURCE)))) {
      throw new ProjectCommonException(ResponseCode.sourceRequired.getErrorCode(),
          ResponseCode.sourceRequired.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil.isStringNullOREmpty((String) request.get(JsonKey.PAGE_NAME))) {
      throw new ProjectCommonException(ResponseCode.pageNameRequired.getErrorCode(),
          ResponseCode.pageNameRequired.getErrorMessage(), ERROR_CODE);
    }

  }

  /**
   * This method will validate add course request data.
   * 
   * @param courseRequest Request
   */
  public static void validateAddBatchCourse(Request courseRequest) {

    if (courseRequest.getRequest().get(JsonKey.BATCH_ID) == null) {
      throw new ProjectCommonException(ResponseCode.courseBatchIdRequired.getErrorCode(),
          ResponseCode.courseBatchIdRequired.getErrorMessage(), ERROR_CODE);
    }
    if (courseRequest.getRequest().get(JsonKey.USER_IDs) == null) {
      throw new ProjectCommonException(ResponseCode.userIdRequired.getErrorCode(),
          ResponseCode.userIdRequired.getErrorMessage(), ERROR_CODE);
    }
  }

  /**
   * This method will validate add course request data.
   * 
   * @param courseRequest Request
   */
  public static void validateGetBatchCourse(Request courseRequest) {

    if (courseRequest.getRequest().get(JsonKey.BATCH_ID) == null) {
      throw new ProjectCommonException(ResponseCode.courseBatchIdRequired.getErrorCode(),
          ResponseCode.courseBatchIdRequired.getErrorMessage(), ERROR_CODE);
    }
  }

  /**
   * This method will validate update course request data.
   * 
   * @param request Request
   */
  public static void validateUpdateCourse(Request request) {

    if (request.getRequest().get(JsonKey.COURSE_ID) == null) {
      throw new ProjectCommonException(ResponseCode.courseIdRequired.getErrorCode(),
          ResponseCode.courseIdRequired.getErrorMessage(), ERROR_CODE);
    }

  }

  /**
   * This method will validate published course request data.
   * 
   * @param request Request
   */
  public static void validatePublishCourse(Request request) {
    if (request.getRequest().get(JsonKey.COURSE_ID) == null) {
      throw new ProjectCommonException(ResponseCode.courseIdRequiredError.getErrorCode(),
          ResponseCode.courseIdRequiredError.getErrorMessage(), ERROR_CODE);
    }
  }


  /**
   * This method will validate Delete course request data.
   * 
   * @param request Request
   */
  public static void validateDeleteCourse(Request request) {
    if (request.getRequest().get(JsonKey.COURSE_ID) == null) {
      throw new ProjectCommonException(ResponseCode.courseIdRequiredError.getErrorCode(),
          ResponseCode.courseIdRequiredError.getErrorMessage(), ERROR_CODE);
    }
  }


  /*
   * This method will validate create section data
   * 
   * @param userRequest Request
   */
  public static void validateCreateSection(Request request) {
    if (ProjectUtil
        .isStringNullOREmpty((String) (request.getRequest().get(JsonKey.SECTION_NAME) != null
            ? request.getRequest().get(JsonKey.SECTION_NAME) : ""))) {
      throw new ProjectCommonException(ResponseCode.sectionNameRequired.getErrorCode(),
          ResponseCode.sectionNameRequired.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil
        .isStringNullOREmpty((String) (request.getRequest().get(JsonKey.SECTION_DATA_TYPE) != null
            ? request.getRequest().get(JsonKey.SECTION_DATA_TYPE) : ""))) {
      throw new ProjectCommonException(ResponseCode.sectionDataTypeRequired.getErrorCode(),
          ResponseCode.sectionDataTypeRequired.getErrorMessage(), ERROR_CODE);
    }
  }

  /**
   * This method will validate update section request data
   * 
   * @param request Request
   */
  public static void validateUpdateSection(Request request) {
    if (ProjectUtil
        .isStringNullOREmpty((String) (request.getRequest().get(JsonKey.SECTION_NAME) != null
            ? request.getRequest().get(JsonKey.SECTION_NAME) : ""))) {
      throw new ProjectCommonException(ResponseCode.sectionNameRequired.getErrorCode(),
          ResponseCode.sectionNameRequired.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.ID) != null
        ? request.getRequest().get(JsonKey.ID) : ""))) {
      throw new ProjectCommonException(ResponseCode.sectionIdRequired.getErrorCode(),
          ResponseCode.sectionIdRequired.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil
        .isStringNullOREmpty((String) (request.getRequest().get(JsonKey.SECTION_DATA_TYPE) != null
            ? request.getRequest().get(JsonKey.SECTION_DATA_TYPE) : ""))) {
      throw new ProjectCommonException(ResponseCode.sectionDataTypeRequired.getErrorCode(),
          ResponseCode.sectionDataTypeRequired.getErrorMessage(), ERROR_CODE);
    }
  }


  /**
   * This method will validate create page data
   * 
   * @param request Request
   */
  public static void validateCreatePage(Request request) {
    if (ProjectUtil
        .isStringNullOREmpty((String) (request.getRequest().get(JsonKey.PAGE_NAME) != null
            ? request.getRequest().get(JsonKey.PAGE_NAME) : ""))) {
      throw new ProjectCommonException(ResponseCode.pageNameRequired.getErrorCode(),
          ResponseCode.pageNameRequired.getErrorMessage(), ERROR_CODE);
    }
  }

  /**
   * This method will validate update page request data
   * 
   * @param request Request
   */
  public static void validateUpdatepage(Request request) {
    if (ProjectUtil
        .isStringNullOREmpty((String) (request.getRequest().get(JsonKey.PAGE_NAME) != null
            ? request.getRequest().get(JsonKey.PAGE_NAME) : ""))) {
      throw new ProjectCommonException(ResponseCode.pageNameRequired.getErrorCode(),
          ResponseCode.pageNameRequired.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil.isStringNullOREmpty((String) (request.getRequest().get(JsonKey.ID) != null
        ? request.getRequest().get(JsonKey.ID) : ""))) {
      throw new ProjectCommonException(ResponseCode.pageIdRequired.getErrorCode(),
          ResponseCode.pageIdRequired.getErrorMessage(), ERROR_CODE);
    }
  }


  /**
   * This method will validate save Assessment data.
   * 
   * @param request Request
   */
  public static void validateSaveAssessment(Request request) {
    if (ProjectUtil
        .isStringNullOREmpty((String) (request.getRequest().get(JsonKey.COURSE_ID) != null
            ? request.getRequest().get(JsonKey.COURSE_ID) : ""))) {
      throw new ProjectCommonException(ResponseCode.courseIdRequired.getErrorCode(),
          ResponseCode.courseIdRequired.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil
        .isStringNullOREmpty((String) (request.getRequest().get(JsonKey.CONTENT_ID) != null
            ? request.getRequest().get(JsonKey.CONTENT_ID) : ""))) {
      throw new ProjectCommonException(ResponseCode.contentIdRequired.getErrorCode(),
          ResponseCode.contentIdRequired.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil
        .isStringNullOREmpty((String) (request.getRequest().get(JsonKey.ATTEMPT_ID) != null
            ? request.getRequest().get(JsonKey.ATTEMPT_ID) : ""))) {
      throw new ProjectCommonException(ResponseCode.attemptIdRequired.getErrorCode(),
          ResponseCode.attemptIdRequired.getErrorMessage(), ERROR_CODE);
    }
    if (request.getRequest().get(JsonKey.ASSESSMENT) != null) {
      @SuppressWarnings("unchecked")
      List<Map<String, Object>> list =
          (List<Map<String, Object>>) request.getRequest().get(JsonKey.ASSESSMENT);
      for (Map<String, Object> map : list) {
        if (ProjectUtil.isStringNullOREmpty((String) (map.get(JsonKey.ASSESSMENT_ITEM_ID) != null
            ? map.get(JsonKey.ASSESSMENT_ITEM_ID) : ""))) {
          throw new ProjectCommonException(ResponseCode.assessmentItemIdRequired.getErrorCode(),
              ResponseCode.assessmentItemIdRequired.getErrorMessage(), ERROR_CODE);
        }
        if (ProjectUtil.isStringNullOREmpty((String) (map.get(JsonKey.ASSESSMENT_TYPE) != null
            ? map.get(JsonKey.ASSESSMENT_TYPE) : ""))) {
          throw new ProjectCommonException(ResponseCode.assessmentTypeRequired.getErrorCode(),
              ResponseCode.assessmentTypeRequired.getErrorMessage(), ERROR_CODE);
        }
        if (ProjectUtil.isStringNullOREmpty((String) (map.get(JsonKey.ASSESSMENT_ANSWERS) != null
            ? map.get(JsonKey.ASSESSMENT_ANSWERS) : ""))) {
          throw new ProjectCommonException(ResponseCode.assessmentAnswersRequired.getErrorCode(),
              ResponseCode.assessmentAnswersRequired.getErrorMessage(), ERROR_CODE);
        }
        if (ProjectUtil.isStringNullOREmpty((String) (map.get(JsonKey.ASSESSMENT_MAX_SCORE) != null
            ? map.get(JsonKey.ASSESSMENT_ANSWERS) : ""))) {
          throw new ProjectCommonException(ResponseCode.assessmentmaxScoreRequired.getErrorCode(),
              ResponseCode.assessmentmaxScoreRequired.getErrorMessage(), ERROR_CODE);
        }
      }
    }

  }


  /**
   * This method will validate get Assessment data.
   * 
   * @param request Request
   */
  public static void validateGetAssessment(Request request) {
    if (ProjectUtil
        .isStringNullOREmpty((String) (request.getRequest().get(JsonKey.COURSE_ID) != null
            ? request.getRequest().get(JsonKey.COURSE_ID) : ""))) {
      throw new ProjectCommonException(ResponseCode.courseIdRequiredError.getErrorCode(),
          ResponseCode.courseIdRequiredError.getErrorMessage(), ERROR_CODE);
    }
  }

  /**
   * This method will validate user org requested data.
   * 
   * @param userRequest Request
   */
  public static void validateUserOrg(Request userRequest) {
    validateOrg(userRequest);
    if (!ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.USER_ID))
        && (!ProjectUtil
            .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.USER_NAME))
            || !ProjectUtil
                .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.USERNAME)))) {
      throw new ProjectCommonException(ResponseCode.usernameOrUserIdError.getErrorCode(),
          ResponseCode.usernameOrUserIdError.getErrorMessage(), ERROR_CODE);
    }
  }

  /**
   * This method will validate verifyUser requested data.
   * 
   * @param userRequest Request
   */
  public static void validateVerifyUser(Request userRequest) {
    if (ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.LOGIN_ID))) {
      throw new ProjectCommonException(ResponseCode.loginIdRequired.getErrorCode(),
          ResponseCode.loginIdRequired.getErrorMessage(), ERROR_CODE);
    }
  }

  /**
   * This method will validate composite search request data.
   * 
   * @param searchRequest Request
   */
  public static void validateCompositeSearch(Request searchRequest) {}

  /**
   * This method will validate user org requested data.
   * 
   * @param userRequest Request
   */
  @SuppressWarnings("rawtypes")
  public static void validateAddMember(Request userRequest) {
    validateOrg(userRequest);
    if (userRequest.getRequest().containsKey(JsonKey.ROLES)
        && ((List) userRequest.getRequest().get(JsonKey.ROLES)).isEmpty()) {
      throw new ProjectCommonException(ResponseCode.roleRequired.getErrorCode(),
          ResponseCode.roleRequired.getErrorMessage(), ERROR_CODE);
    }
    if (!ProjectUtil.isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.USER_ID))
        && (!ProjectUtil
            .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.USER_NAME))
            || !ProjectUtil
                .isStringNullOREmpty((String) userRequest.getRequest().get(JsonKey.USERNAME)))) {
      throw new ProjectCommonException(ResponseCode.usernameOrUserIdError.getErrorCode(),
          ResponseCode.usernameOrUserIdError.getErrorMessage(), ERROR_CODE);
    }
  }

  private static void validateAddress(Map<String, Object> address, String type) {
    if (ProjectUtil.isStringNullOREmpty((String) address.get(JsonKey.ADDRESS_LINE1))) {
      throw new ProjectCommonException(ResponseCode.addressError.getErrorCode(), ProjectUtil
          .formatMessage(ResponseCode.addressError.getErrorMessage(), type, JsonKey.ADDRESS_LINE1),
          ERROR_CODE);
    }
    if (ProjectUtil.isStringNullOREmpty((String) address.get(JsonKey.CITY))) {
      throw new ProjectCommonException(ResponseCode.addressError.getErrorCode(), ProjectUtil
          .formatMessage(ResponseCode.addressError.getErrorMessage(), type, JsonKey.CITY),
          ERROR_CODE);
    }
    if (address.containsKey(JsonKey.ADD_TYPE) && type.equals(JsonKey.ADDRESS)) {

      if (ProjectUtil.isStringNullOREmpty((String) address.get(JsonKey.ADD_TYPE))) {
        throw new ProjectCommonException(ResponseCode.addressError.getErrorCode(), ProjectUtil
            .formatMessage(ResponseCode.addressError.getErrorMessage(), type, JsonKey.TYPE),
            ERROR_CODE);
      }

      if (!ProjectUtil.isStringNullOREmpty((String) address.get(JsonKey.ADD_TYPE))
          && !checkAddressType((String) address.get(JsonKey.ADD_TYPE))) {
        throw new ProjectCommonException(ResponseCode.addressTypeError.getErrorCode(),
            ResponseCode.addressTypeError.getErrorMessage(), ERROR_CODE);
      }
    }
  }

  private static boolean checkAddressType(String addrType) {
    for (AddressType type : AddressType.values()) {
      if (type.getTypeName().equals(addrType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method will validate bulk user upload requested data.
   * 
   * @param reqObj Request
   */
  public static void validateUploadUser(Request reqObj) {
    if (ProjectUtil
        .isStringNullOREmpty((String) reqObj.getRequest().get(JsonKey.ORGANISATION_ID))) {
      if ((ProjectUtil.isStringNullOREmpty((String) reqObj.getRequest().get(JsonKey.EXTERNAL_ID))
          || ProjectUtil.isStringNullOREmpty((String) reqObj.getRequest().get(JsonKey.PROVIDER)))) {
        throw new ProjectCommonException(ResponseCode.bulkUserUploadError.getErrorCode(),
            ResponseCode.bulkUserUploadError.getErrorMessage(), ERROR_CODE);
      }
    }
  }

  /**
   * Either user will send UserId or (provider and externalId).
   * 
   * @param request
   */
  public static void validateAssignRole(Request request) {
    if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.USER_ID))) {
      if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.EXTERNAL_ID))
          || ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.PROVIDER))) {
        throw new ProjectCommonException(
            ResponseCode.sourceAndExternalIdValidationError.getErrorCode(),
            ResponseCode.sourceAndExternalIdValidationError.getErrorMessage(), ERROR_CODE);
      }
    }
    if (request.getRequest().get(JsonKey.ROLES) == null
        || !(request.getRequest().get(JsonKey.ROLES) instanceof List)) {
      throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(), ProjectUtil
          .formatMessage(ResponseCode.dataTypeError.getErrorMessage(), JsonKey.ROLES, JsonKey.LIST),
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
    if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.COURSE_ID))) {
      throw new ProjectCommonException(ResponseCode.invalidCourseId.getErrorCode(),
          ResponseCode.invalidCourseId.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.NAME))) {
      throw new ProjectCommonException(ResponseCode.courseNameRequired.getErrorCode(),
          ResponseCode.courseNameRequired.getErrorMessage(), ERROR_CODE);
    }
    String enrolmentType = (String) request.getRequest().get(JsonKey.ENROLLMENT_TYPE);
    validateEnrolmentType(enrolmentType);
    String startDate = (String) request.getRequest().get(JsonKey.START_DATE);
    validateStartDate(startDate);
    if (request.getRequest().containsKey(JsonKey.END_DATE)
        && !ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.END_DATE))) {
      validateEndDate(startDate, (String) request.getRequest().get(JsonKey.END_DATE));
    }
    if (request.getRequest().containsKey(JsonKey.COURSE_CREATED_FOR)
        && !(request.getRequest().get(JsonKey.COURSE_CREATED_FOR) instanceof List)) {
      throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
          ResponseCode.dataTypeError.getErrorMessage(), ERROR_CODE);
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
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    format.setLenient(false);
    if (null != request.getRequest().get(JsonKey.STATUS)) {
      boolean status = false;
      try {
        status =
            checkProgressStatus(Integer.parseInt("" + request.getRequest().get(JsonKey.STATUS)));
      } catch (Exception e) {
        ProjectLogger.log(e.getMessage(), e);
        throw new ProjectCommonException(ResponseCode.progressStatusError.getErrorCode(),
            ResponseCode.progressStatusError.getErrorMessage(), ERROR_CODE);
      }
      if (!status) {
        throw new ProjectCommonException(ResponseCode.progressStatusError.getErrorCode(),
            ResponseCode.progressStatusError.getErrorMessage(), ERROR_CODE);
      }
    }
    if (request.getRequest().containsKey(JsonKey.NAME)
        && ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.NAME))) {
      throw new ProjectCommonException(ResponseCode.courseNameRequired.getErrorCode(),
          ResponseCode.courseNameRequired.getErrorMessage(), ERROR_CODE);
    }
    if (request.getRequest().containsKey(JsonKey.ENROLLMENT_TYPE)) {
      String enrolmentType = (String) request.getRequest().get(JsonKey.ENROLLMENT_TYPE);
      validateEnrolmentType(enrolmentType);
    }
    if (request.getRequest().containsKey(JsonKey.END_DATE)
        && !ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.END_DATE))) {
      boolean bool = validateDateWithTodayDate((String) request.getRequest().get(JsonKey.END_DATE));
      if (!bool) {
        throw new ProjectCommonException(ResponseCode.invalidBatchEndDateError.getErrorCode(),
            ResponseCode.invalidBatchEndDateError.getErrorMessage(), ERROR_CODE);
      }
    }

    if (request.getRequest().containsKey(JsonKey.END_DATE)
        && !ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.END_DATE))
        && request.getRequest().containsKey(JsonKey.START_DATE) && !ProjectUtil
            .isStringNullOREmpty((String) request.getRequest().get(JsonKey.START_DATE))) {
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
        throw new ProjectCommonException(ResponseCode.dateFormatError.getErrorCode(),
            ResponseCode.dateFormatError.getErrorMessage(), ERROR_CODE);
      }
      if (batchEndDate.before(batchStartDate)) {
        throw new ProjectCommonException(ResponseCode.invalidBatchEndDateError.getErrorCode(),
            ResponseCode.invalidBatchEndDateError.getErrorMessage(), ERROR_CODE);
      }
    }
    if (request.getRequest().containsKey(JsonKey.COURSE_CREATED_FOR)
        && !(request.getRequest().get(JsonKey.COURSE_CREATED_FOR) instanceof List)) {
      throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
          ResponseCode.dataTypeError.getErrorMessage(), ERROR_CODE);
    }

    if (request.getRequest().containsKey(JsonKey.MENTORS)
        && !(request.getRequest().get(JsonKey.MENTORS) instanceof List)) {
      throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
          ResponseCode.dataTypeError.getErrorMessage(), ERROR_CODE);
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
      throw new ProjectCommonException(ResponseCode.dateFormatError.getErrorCode(),
          ResponseCode.dateFormatError.getErrorMessage(), ERROR_CODE);
    }
    return true;
  }

  /**
   * 
   * @param enrolmentType
   */
  private static void validateEnrolmentType(String enrolmentType) {
    if (ProjectUtil.isStringNullOREmpty(enrolmentType)) {
      throw new ProjectCommonException(ResponseCode.enrolmentTypeRequired.getErrorCode(),
          ResponseCode.enrolmentTypeRequired.getErrorMessage(), ERROR_CODE);
    }
    if (!(ProjectUtil.EnrolmentType.open.getVal().equalsIgnoreCase(enrolmentType)
        || ProjectUtil.EnrolmentType.inviteOnly.getVal().equalsIgnoreCase(enrolmentType))) {
      throw new ProjectCommonException(ResponseCode.enrolmentIncorrectValue.getErrorCode(),
          ResponseCode.enrolmentIncorrectValue.getErrorMessage(), ERROR_CODE);
    }
  }

  /**
   * 
   * @param startDate
   */
  private static void validateStartDate(String startDate) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    format.setLenient(false);
    if (ProjectUtil.isStringNullOREmpty(startDate)) {
      throw new ProjectCommonException(ResponseCode.courseBatchSatrtDateRequired.getErrorCode(),
          ResponseCode.courseBatchSatrtDateRequired.getErrorMessage(), ERROR_CODE);
    }
    try {
      Date batchStartDate = format.parse(startDate);
      Date todayDate = format.parse(format.format(new Date()));
      Calendar cal1 = Calendar.getInstance();
      Calendar cal2 = Calendar.getInstance();
      cal1.setTime(batchStartDate);
      cal2.setTime(todayDate);
      if (batchStartDate.before(todayDate)) {
        throw new ProjectCommonException(ResponseCode.courseBatchStartDateError.getErrorCode(),
            ResponseCode.courseBatchStartDateError.getErrorMessage(), ERROR_CODE);
      }
    } catch (ProjectCommonException e) {
      throw e;
    } catch (Exception e) {
      throw new ProjectCommonException(ResponseCode.dateFormatError.getErrorCode(),
          ResponseCode.dateFormatError.getErrorMessage(), ERROR_CODE);
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
      throw new ProjectCommonException(ResponseCode.dateFormatError.getErrorCode(),
          ResponseCode.dateFormatError.getErrorMessage(), ERROR_CODE);
    }
    if (batchStartDate.getTime() >= batchEndDate.getTime()) {
      throw new ProjectCommonException(ResponseCode.endDateError.getErrorCode(),
          ResponseCode.endDateError.getErrorMessage(), ERROR_CODE);
    }
  }


  public static void validateSyncRequest(Request request) {
    if (request.getRequest().get(JsonKey.OBJECT_TYPE) == null || ProjectUtil
        .isStringNullOREmpty((String) request.getRequest().get(JsonKey.OBJECT_TYPE))) {
      throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
          ResponseCode.dataTypeError.getErrorMessage(), ERROR_CODE);
    }
    List<String> list = new ArrayList<>(Arrays.asList(
        new String[] {JsonKey.USER, JsonKey.ORGANISATION, JsonKey.BATCH, JsonKey.USER_COURSE}));
    if (!list.contains(request.getRequest().get(JsonKey.OBJECT_TYPE))) {
      throw new ProjectCommonException(ResponseCode.invalidObjectType.getErrorCode(),
          ResponseCode.invalidObjectType.getErrorMessage(), ERROR_CODE);
    }

  }

  public static void validateUpdateSystemSettingsRequest(Request request) {
    List<String> list = new ArrayList<>(Arrays.asList(
        PropertiesCache.getInstance().getProperty("system_settings_properties").split(",")));
    for(String str : request.getRequest().keySet()){
      if (!list.contains(str)) {
        throw new ProjectCommonException(ResponseCode.invalidPropertyError.getErrorCode(),
            MessageFormat.format(ResponseCode.invalidPropertyError.getErrorMessage(),str), ERROR_CODE);
      }
    }
  }

  @SuppressWarnings("rawtypes")
  public static void validateSendMail(Request request) {
    if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.SUBJECT))) {
      throw new ProjectCommonException(ResponseCode.emailSubjectError.getErrorCode(),
          ResponseCode.emailSubjectError.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.BODY))) {
      throw new ProjectCommonException(ResponseCode.emailBodyError.getErrorCode(),
          ResponseCode.emailBodyError.getErrorMessage(), ERROR_CODE);
    }
    if (null == (request.getRequest().get(JsonKey.RECIPIENT_EMAILS))
        && null == (request.getRequest().get(JsonKey.RECIPIENT_USERIDS))) {
      throw new ProjectCommonException(ResponseCode.recipientAddressError.getErrorCode(),
          ResponseCode.recipientAddressError.getErrorMessage(), ERROR_CODE);
    }
    if ((null != (request.getRequest().get(JsonKey.RECIPIENT_EMAILS))
        && ((List) request.getRequest().get(JsonKey.RECIPIENT_EMAILS)).isEmpty())
        && (null != (request.getRequest().get(JsonKey.RECIPIENT_USERIDS))
            && ((List) request.getRequest().get(JsonKey.RECIPIENT_USERIDS)).isEmpty())) {
      throw new ProjectCommonException(ResponseCode.recipientAddressError.getErrorCode(),
          ResponseCode.recipientAddressError.getErrorMessage(), ERROR_CODE);
    }
  }


  public static void validateFileUpload(Request reqObj) {

    if (ProjectUtil.isStringNullOREmpty((String) reqObj.get(JsonKey.CONTAINER))) {
      throw new ProjectCommonException(ResponseCode.storageContainerNameMandatory.getErrorCode(),
          ResponseCode.storageContainerNameMandatory.getErrorMessage(), ERROR_CODE);
    }

  }

  /**
   * 
   * @param reqObj
   */
  public static void validateAddUserBadge(Request reqObj) {

    if (ProjectUtil.isStringNullOREmpty((String) reqObj.get(JsonKey.BADGE_TYPE_ID))) {
      throw new ProjectCommonException(ResponseCode.badgeTypeIdMandatory.getErrorCode(),
          ResponseCode.badgeTypeIdMandatory.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil.isStringNullOREmpty((String) reqObj.get(JsonKey.RECEIVER_ID))) {
      throw new ProjectCommonException(ResponseCode.receiverIdMandatory.getErrorCode(),
          ResponseCode.receiverIdMandatory.getErrorMessage(), ERROR_CODE);
    }

  }

  /**
   * 
   * @param reqObj
   */
  public static void validateCreateOrgType(Request reqObj) {
    if (ProjectUtil.isStringNullOREmpty((String) reqObj.getRequest().get(JsonKey.NAME))) {
      throw new ProjectCommonException(ResponseCode.orgTypeMandatory.getErrorCode(),
          ResponseCode.orgTypeMandatory.getErrorMessage(), ERROR_CODE);
    }
  }

  /**
   * 
   * @param reqObj
   */
  public static void validateUpdateOrgType(Request reqObj) {
    if (ProjectUtil.isStringNullOREmpty((String) reqObj.getRequest().get(JsonKey.NAME))) {
      throw new ProjectCommonException(ResponseCode.orgTypeMandatory.getErrorCode(),
          ResponseCode.orgTypeMandatory.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil.isStringNullOREmpty((String) reqObj.getRequest().get(JsonKey.ID))) {
      throw new ProjectCommonException(ResponseCode.orgTypeIdRequired.getErrorCode(),
          ResponseCode.orgTypeIdRequired.getErrorMessage(), ERROR_CODE);
    }
  }

  /**
   * Method to validate not for userId, title, note, courseId, contentId and tags
   * 
   * @param request
   */
  @SuppressWarnings("rawtypes")
  public static void validateNote(Request request) {
    if (ProjectUtil.isStringNullOREmpty((String) request.get(JsonKey.USER_ID))) {
      throw new ProjectCommonException(ResponseCode.userIdRequired.getErrorCode(),
          ResponseCode.userIdRequired.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil.isStringNullOREmpty((String) request.get(JsonKey.TITLE))) {
      throw new ProjectCommonException(ResponseCode.titleRequired.getErrorCode(),
          ResponseCode.titleRequired.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil.isStringNullOREmpty((String) request.get(JsonKey.NOTE))) {
      throw new ProjectCommonException(ResponseCode.noteRequired.getErrorCode(),
          ResponseCode.noteRequired.getErrorMessage(), ERROR_CODE);
    }
    if (ProjectUtil.isStringNullOREmpty((String) request.get(JsonKey.CONTENT_ID))) {
      if (ProjectUtil.isStringNullOREmpty((String) request.get(JsonKey.COURSE_ID))) {
        throw new ProjectCommonException(ResponseCode.contentIdError.getErrorCode(),
            ResponseCode.contentIdError.getErrorMessage(), ERROR_CODE);
      }
    }
    if (request.getRequest().containsKey(JsonKey.TAGS)) {
      if ((request.getRequest().get(JsonKey.TAGS) instanceof List)
          && ((List) request.getRequest().get(JsonKey.TAGS)).isEmpty()) {
        throw new ProjectCommonException(ResponseCode.invalidTags.getErrorCode(),
            ResponseCode.invalidTags.getErrorMessage(), ERROR_CODE);
      } else if (request.getRequest().get(JsonKey.TAGS) instanceof String) {
        throw new ProjectCommonException(ResponseCode.invalidTags.getErrorCode(),
            ResponseCode.invalidTags.getErrorMessage(), ERROR_CODE);
      }
    }
  }

  /**
   * Method to validate noteId
   * 
   * @param noteId
   */
  public static void validateNoteId(String noteId) {
    if (ProjectUtil.isStringNullOREmpty(noteId)) {
      throw new ProjectCommonException(ResponseCode.invalidNoteId.getErrorCode(),
          ResponseCode.invalidNoteId.getErrorMessage(), ERROR_CODE);
    }
  }

  @SuppressWarnings("unchecked")
  public static void validateWebPages(Request request) {
    if (request.getRequest().containsKey(JsonKey.WEB_PAGES)) {
      List<Map<String, String>> data =
          (List<Map<String, String>>) request.getRequest().get(JsonKey.WEB_PAGES);
      if (null == data || data.isEmpty()) {
        throw new ProjectCommonException(ResponseCode.invalidWebPageData.getErrorCode(),
            ResponseCode.invalidWebPageData.getErrorMessage(), ERROR_CODE);
      }
    }
  }

  /**
   * 
   * @param request
   */
  public static void validateForgotpassword(Request request) {
    if (request.getRequest().get(JsonKey.USERNAME) == null
        || ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.USERNAME))) {
      throw new ProjectCommonException(ResponseCode.userNameRequired.getErrorCode(),
          ResponseCode.userNameRequired.getErrorMessage(), ERROR_CODE);
    }
  }

  @SuppressWarnings("unchecked")
  public static void validateProfileVisibility(Request request) {
    if (request.getRequest().get(JsonKey.PRIVATE) == null
        && request.getRequest().get(JsonKey.PUBLIC) == null) {
      throw new ProjectCommonException(ResponseCode.invalidData.getErrorCode(),
          ResponseCode.invalidData.getErrorMessage(), ERROR_CODE);
    }
    if (request.getRequest().containsKey(JsonKey.PRIVATE)
        && !(request.getRequest().get(JsonKey.PRIVATE) instanceof List)) {
      throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
          ProjectUtil.formatMessage(ResponseCode.dataTypeError.getErrorMessage(), JsonKey.PRIVATE,
              JsonKey.LIST),
          ERROR_CODE);
    }
    if (request.getRequest().containsKey(JsonKey.PUBLIC)
        && !(request.getRequest().get(JsonKey.PUBLIC) instanceof List)) {
      throw new ProjectCommonException(ResponseCode.dataTypeError.getErrorCode(),
          ProjectUtil.formatMessage(ResponseCode.dataTypeError.getErrorMessage(), JsonKey.PUBLIC,
              JsonKey.LIST),
          ERROR_CODE);
    }
    if (request.getRequest().get(JsonKey.USER_ID) == null
        || ProjectUtil.isStringNullOREmpty(((String) request.getRequest().get(JsonKey.USER_ID)))) {
      throw new ProjectCommonException(ResponseCode.usernameOrUserIdError.getErrorCode(),
          ResponseCode.usernameOrUserIdError.getErrorMessage(), ERROR_CODE);
    }
    if (null != request.getRequest().get(JsonKey.PRIVATE)
        && null != request.getRequest().get(JsonKey.PUBLIC)) {
      List<String> privateList = (List<String>) request.getRequest().get(JsonKey.PRIVATE);
      List<String> publicList = (List<String>) request.getRequest().get(JsonKey.PUBLIC);
      if (privateList.size() > publicList.size()) {
        for (String field : publicList) {
          if (privateList.contains(field)) {
            throw new ProjectCommonException(ResponseCode.visibilityInvalid.getErrorCode(),
                ResponseCode.visibilityInvalid.getErrorMessage(), ERROR_CODE);
          }
        }
      } else {
        for (String field : privateList) {
          if (publicList.contains(field)) {
            throw new ProjectCommonException(ResponseCode.visibilityInvalid.getErrorCode(),
                ResponseCode.visibilityInvalid.getErrorMessage(), ERROR_CODE);
          }
        }
      }
    }

  }

  /**
   * Method to validate
   * 
   * @param request
   */
  public static void validateRegisterClient(Request request) {

    if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.CLIENT_NAME))) {
      throw new ProjectCommonException(ResponseCode.invalidClientName.getErrorCode(),
          ResponseCode.invalidClientName.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
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
    if (ProjectUtil.isStringNullOREmpty(masterAccessToken)) {
      throw new ProjectCommonException(ResponseCode.invalidRequestData.getErrorCode(),
          ResponseCode.invalidRequestData.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }

  /**
   * Method to validate clientId.
   * 
   * @param clientId
   */
  public static void validateClientId(String clientId) {
    if (ProjectUtil.isStringNullOREmpty(clientId)) {
      throw new ProjectCommonException(ResponseCode.invalidClientId.getErrorCode(),
          ResponseCode.invalidClientId.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }

  /**
   * Method to validate notification request data.
   * 
   * @param request Request
   */
  @SuppressWarnings("unchecked")
  public static void validateSendNotification(Request request) {
    if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.TO))) {
      throw new ProjectCommonException(ResponseCode.invalidTopic.getErrorCode(),
          ResponseCode.invalidTopic.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    if (request.getRequest().get(JsonKey.DATA) == null
        || !(request.getRequest().get(JsonKey.DATA) instanceof Map)
        || ((Map<String, Object>) request.getRequest().get(JsonKey.DATA)).size() == 0) {
      throw new ProjectCommonException(ResponseCode.invalidTopicData.getErrorCode(),
          ResponseCode.invalidTopicData.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }

    if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(JsonKey.TYPE))) {
      throw new ProjectCommonException(ResponseCode.invalidNotificationType.getErrorCode(),
          ResponseCode.invalidNotificationType.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    if (!(JsonKey.FCM.equalsIgnoreCase((String) request.getRequest().get(JsonKey.TYPE)))) {
      throw new ProjectCommonException(ResponseCode.notificationTypeSupport.getErrorCode(),
          ResponseCode.notificationTypeSupport.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }

}
