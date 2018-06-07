package org.sunbird.common.responsecode;

import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.models.util.JsonKey;

/** @author Manzarul */
public enum ResponseCode {
  unAuthorized(ResponseMessage.Key.UNAUTHORIZED_USER, ResponseMessage.Message.UNAUTHORIZED_USER),
  invalidUserCredentials(
      ResponseMessage.Key.INVALID_USER_CREDENTIALS,
      ResponseMessage.Message.INVALID_USER_CREDENTIALS),
  operationTimeout(
      ResponseMessage.Key.OPERATION_TIMEOUT, ResponseMessage.Message.OPERATION_TIMEOUT),
  invalidOperationName(
      ResponseMessage.Key.INVALID_OPERATION_NAME, ResponseMessage.Message.INVALID_OPERATION_NAME),
  invalidRequestData(
      ResponseMessage.Key.INVALID_REQUESTED_DATA, ResponseMessage.Message.INVALID_REQUESTED_DATA),
  invalidCustomerId(
      ResponseMessage.Key.CONSUMER_ID_MISSING_ERROR,
      ResponseMessage.Message.CONSUMER_ID_MISSING_ERROR),
  customerIdRequired(
      ResponseMessage.Key.CONSUMER_ID_INVALID_ERROR,
      ResponseMessage.Message.CONSUMER_ID_INVALID_ERROR),
  deviceIdRequired(
      ResponseMessage.Key.DEVICE_ID_MISSING_ERROR, ResponseMessage.Message.DEVICE_ID_MISSING_ERROR),
  invalidContentId(
      ResponseMessage.Key.CONTENT_ID_INVALID_ERROR,
      ResponseMessage.Message.CONTENT_ID_INVALID_ERROR),
  courseIdRequired(
      ResponseMessage.Key.COURSE_ID_MISSING_ERROR, ResponseMessage.Message.COURSE_ID_MISSING_ERROR),
  contentIdRequired(
      ResponseMessage.Key.CONTENT_ID_MISSING_ERROR,
      ResponseMessage.Message.CONTENT_ID_MISSING_ERROR),
  apiKeyRequired(
      ResponseMessage.Key.API_KEY_MISSING_ERROR, ResponseMessage.Message.API_KEY_MISSING_ERROR),
  invalidApiKey(
      ResponseMessage.Key.API_KEY_INVALID_ERROR, ResponseMessage.Message.API_KEY_INVALID_ERROR),
  internalError(ResponseMessage.Key.INTERNAL_ERROR, ResponseMessage.Message.INTERNAL_ERROR),
  dbInsertionError(
      ResponseMessage.Key.DB_INSERTION_FAIL, ResponseMessage.Message.DB_INSERTION_FAIL),
  dbUpdateError(ResponseMessage.Key.DB_UPDATE_FAIL, ResponseMessage.Message.DB_UPDATE_FAIL),
  courseNameRequired(
      ResponseMessage.Key.COURSE_NAME_MISSING, ResponseMessage.Message.COURSE_NAME_MISSING),
  success(ResponseMessage.Key.SUCCESS_MESSAGE, ResponseMessage.Message.SUCCESS_MESSAGE),
  sessionIdRequiredError(
      ResponseMessage.Key.SESSION_ID_MISSING, ResponseMessage.Message.SESSION_ID_MISSING),
  courseIdRequiredError(
      ResponseMessage.Key.COURSE_ID_MISSING, ResponseMessage.Message.COURSE_ID_MISSING),
  contentIdRequiredError(
      ResponseMessage.Key.CONTENT_ID_MISSING, ResponseMessage.Message.CONTENT_ID_MISSING),
  versionRequiredError(
      ResponseMessage.Key.VERSION_MISSING, ResponseMessage.Message.VERSION_MISSING),
  courseVersionRequiredError(
      ResponseMessage.Key.COURSE_VERSION_MISSING, ResponseMessage.Message.COURSE_VERSION_MISSING),
  contentVersionRequiredError(
      ResponseMessage.Key.CONTENT_VERSION_MISSING, ResponseMessage.Message.CONTENT_VERSION_MISSING),
  courseDescriptionError(
      ResponseMessage.Key.COURSE_DESCRIPTION_MISSING,
      ResponseMessage.Message.COURSE_DESCRIPTION_MISSING),
  courseTocUrlError(
      ResponseMessage.Key.COURSE_TOCURL_MISSING, ResponseMessage.Message.COURSE_TOCURL_MISSING),
  emailRequired(ResponseMessage.Key.EMAIL_MISSING, ResponseMessage.Message.EMAIL_MISSING),
  emailFormatError(ResponseMessage.Key.EMAIL_FORMAT, ResponseMessage.Message.EMAIL_FORMAT),
  urlFormatError(ResponseMessage.Key.URL_FORMAT_ERROR, ResponseMessage.Message.URL_FORMAT_ERROR),
  firstNameRequired(
      ResponseMessage.Key.FIRST_NAME_MISSING, ResponseMessage.Message.FIRST_NAME_MISSING),
  languageRequired(ResponseMessage.Key.LANGUAGE_MISSING, ResponseMessage.Message.LANGUAGE_MISSING),
  passwordRequired(ResponseMessage.Key.PASSWORD_MISSING, ResponseMessage.Message.PASSWORD_MISSING),
  passwordMinLengthError(
      ResponseMessage.Key.PASSWORD_MIN_LENGHT, ResponseMessage.Message.PASSWORD_MIN_LENGHT),
  passwordMaxLengthError(
      ResponseMessage.Key.PASSWORD_MAX_LENGHT, ResponseMessage.Message.PASSWORD_MAX_LENGHT),
  organisationIdRequiredError(
      ResponseMessage.Key.ORGANISATION_ID_MISSING, ResponseMessage.Message.ORGANISATION_ID_MISSING),
  sourceAndExternalIdValidationError(
      ResponseMessage.Key.REQUIRED_DATA_ORG_MISSING,
      ResponseMessage.Message.REQUIRED_DATA_ORG_MISSING),
  organisationNameRequired(
      ResponseMessage.Key.ORGANISATION_NAME_MISSING,
      ResponseMessage.Message.ORGANISATION_NAME_MISSING),
  channelUniquenessInvalid(
      ResponseMessage.Key.CHANNEL_SHOULD_BE_UNIQUE,
      ResponseMessage.Message.CHANNEL_SHOULD_BE_UNIQUE),
  unableToConnect(
      ResponseMessage.Key.UNABLE_TO_CONNECT_TO_EKSTEP,
      ResponseMessage.Message.UNABLE_TO_CONNECT_TO_EKSTEP),
  unableToConnectToES(
      ResponseMessage.Key.UNABLE_TO_CONNECT_TO_ES, ResponseMessage.Message.UNABLE_TO_CONNECT_TO_ES),
  unableToParseData(
      ResponseMessage.Key.UNABLE_TO_PARSE_DATA, ResponseMessage.Message.UNABLE_TO_PARSE_DATA),
  invalidJsonData(ResponseMessage.Key.INVALID_JSON, ResponseMessage.Message.INVALID_JSON),
  invalidOrgData(ResponseMessage.Key.INVALID_ORG_DATA, ResponseMessage.Message.INVALID_ORG_DATA),
  invalidRootOrganisationId(
      ResponseMessage.Key.INVALID_ROOT_ORGANIZATION,
      ResponseMessage.Message.INVALID_ROOT_ORGANIZATION),
  invalidParentId(
      ResponseMessage.Key.INVALID_PARENT_ORGANIZATION_ID,
      ResponseMessage.Message.INVALID_PARENT_ORGANIZATION_ID),
  channelIdRequiredForRootOrg(
      ResponseMessage.Key.CHANNEL_MISSING, ResponseMessage.Message.CHANNEL_MISSING),
  cyclicValidationError(
      ResponseMessage.Key.CYCLIC_VALIDATION_FAILURE,
      ResponseMessage.Message.CYCLIC_VALIDATION_FAILURE),
  invalidUsrData(ResponseMessage.Key.INVALID_USR_DATA, ResponseMessage.Message.INVALID_USR_DATA),
  usrValidationError(
      ResponseMessage.Key.USR_DATA_VALIDATION_ERROR,
      ResponseMessage.Message.USR_DATA_VALIDATION_ERROR),
  enrollmentStartDateRequiredError(
      ResponseMessage.Key.ENROLLMENT_START_DATE_MISSING,
      ResponseMessage.Message.ENROLLMENT_START_DATE_MISSING),
  courseDurationRequiredError(
      ResponseMessage.Key.COURSE_DURATION_MISSING, ResponseMessage.Message.COURSE_DURATION_MISSING),
  loginTypeRequired(
      ResponseMessage.Key.LOGIN_TYPE_MISSING, ResponseMessage.Message.LOGIN_TYPE_MISSING),
  emailAlreadyExistError(ResponseMessage.Key.EMAIL_IN_USE, ResponseMessage.Message.EMAIL_IN_USE),
  invalidCredentials(
      ResponseMessage.Key.INVALID_CREDENTIAL, ResponseMessage.Message.INVALID_CREDENTIAL),
  userNameRequired(ResponseMessage.Key.USERNAME_MISSING, ResponseMessage.Message.USERNAME_MISSING),
  userNameAlreadyExistError(
      ResponseMessage.Key.USERNAME_IN_USE, ResponseMessage.Message.USERNAME_IN_USE),
  userIdRequired(ResponseMessage.Key.USERID_MISSING, ResponseMessage.Message.USERID_MISSING),
  roleRequired(ResponseMessage.Key.ROLE_MISSING, ResponseMessage.Message.ROLE_MISSING),
  msgIdRequiredError(
      ResponseMessage.Key.MESSAGE_ID_MISSING, ResponseMessage.Message.MESSAGE_ID_MISSING),
  userNameCanntBeUpdated(
      ResponseMessage.Key.USERNAME_CANNOT_BE_UPDATED,
      ResponseMessage.Message.USERNAME_CANNOT_BE_UPDATED),
  authTokenRequired(
      ResponseMessage.Key.AUTH_TOKEN_MISSING, ResponseMessage.Message.AUTH_TOKEN_MISSING),
  invalidAuthToken(
      ResponseMessage.Key.INVALID_AUTH_TOKEN, ResponseMessage.Message.INVALID_AUTH_TOKEN),
  timeStampRequired(
      ResponseMessage.Key.TIMESTAMP_REQUIRED, ResponseMessage.Message.TIMESTAMP_REQUIRED),
  publishedCourseCanNotBeUpdated(
      ResponseMessage.Key.PUBLISHED_COURSE_CAN_NOT_UPDATED,
      ResponseMessage.Message.PUBLISHED_COURSE_CAN_NOT_UPDATED),
  sourceRequired(ResponseMessage.Key.SOURCE_MISSING, ResponseMessage.Message.SOURCE_MISSING),
  sectionNameRequired(
      ResponseMessage.Key.SECTION_NAME_MISSING, ResponseMessage.Message.SECTION_NAME_MISSING),
  sectionDataTypeRequired(
      ResponseMessage.Key.SECTION_DATA_TYPE_MISSING,
      ResponseMessage.Message.SECTION_DATA_TYPE_MISSING),
  sectionIdRequired(
      ResponseMessage.Key.SECTION_ID_REQUIRED, ResponseMessage.Message.SECTION_ID_REQUIRED),
  pageNameRequired(
      ResponseMessage.Key.PAGE_NAME_REQUIRED, ResponseMessage.Message.PAGE_NAME_REQUIRED),
  pageIdRequired(ResponseMessage.Key.PAGE_ID_REQUIRED, ResponseMessage.Message.PAGE_ID_REQUIRED),
  invaidConfiguration(
      ResponseMessage.Key.INVALID_CONFIGURATION, ResponseMessage.Message.INVALID_CONFIGURATION),
  assessmentItemIdRequired(
      ResponseMessage.Key.ASSESSMENT_ITEM_ID_REQUIRED,
      ResponseMessage.Message.ASSESSMENT_ITEM_ID_REQUIRED),
  assessmentTypeRequired(
      ResponseMessage.Key.ASSESSMENT_TYPE_REQUIRED,
      ResponseMessage.Message.ASSESSMENT_TYPE_REQUIRED),
  assessmentAttemptDateRequired(
      ResponseMessage.Key.ATTEMPTED_DATE_REQUIRED, ResponseMessage.Message.ATTEMPTED_DATE_REQUIRED),
  assessmentAnswersRequired(
      ResponseMessage.Key.ATTEMPTED_ANSWERS_REQUIRED,
      ResponseMessage.Message.ATTEMPTED_ANSWERS_REQUIRED),
  assessmentmaxScoreRequired(
      ResponseMessage.Key.MAX_SCORE_REQUIRED, ResponseMessage.Message.MAX_SCORE_REQUIRED),
  statusCanntBeUpdated(
      ResponseMessage.Key.STATUS_CANNOT_BE_UPDATED,
      ResponseMessage.Message.STATUS_CANNOT_BE_UPDATED),
  attemptIdRequired(
      ResponseMessage.Key.ATTEMPT_ID_MISSING_ERROR,
      ResponseMessage.Message.ATTEMPT_ID_MISSING_ERROR),
  emailANDUserNameAlreadyExistError(
      ResponseMessage.Key.USERNAME_EMAIL_IN_USE, ResponseMessage.Message.USERNAME_EMAIL_IN_USE),
  keyCloakDefaultError(
      ResponseMessage.Key.KEY_CLOAK_DEFAULT_ERROR, ResponseMessage.Message.KEY_CLOAK_DEFAULT_ERROR),
  userRegUnSuccessfull(
      ResponseMessage.Key.USER_REG_UNSUCCESSFULL, ResponseMessage.Message.USER_REG_UNSUCCESSFULL),
  userUpdationUnSuccessfull(
      ResponseMessage.Key.USER_UPDATE_UNSUCCESSFULL,
      ResponseMessage.Message.USER_UPDATE_UNSUCCESSFULL),
  loginTypeError(ResponseMessage.Key.LOGIN_TYPE_ERROR, ResponseMessage.Message.LOGIN_TYPE_ERROR),
  invalidOrgId(ResponseMessage.Key.INVALID_ORG_ID, ResponseMessage.Key.INVALID_ORG_ID),
  invalidOrgStatus(ResponseMessage.Key.INVALID_ORG_STATUS, ResponseMessage.Key.INVALID_ORG_STATUS),
  invalidOrgStatusTransition(
      ResponseMessage.Key.INVALID_ORG_STATUS_TRANSITION,
      ResponseMessage.Key.INVALID_ORG_STATUS_TRANSITION),
  addressRequired(
      ResponseMessage.Key.ADDRESS_REQUIRED_ERROR, ResponseMessage.Message.ADDRESS_REQUIRED_ERROR),
  educationRequired(
      ResponseMessage.Key.EDUCATION_REQUIRED_ERROR,
      ResponseMessage.Message.EDUCATION_REQUIRED_ERROR),
  phoneNoRequired(
      ResponseMessage.Key.PHONE_NO_REQUIRED_ERROR, ResponseMessage.Message.PHONE_NO_REQUIRED_ERROR),
  jobDetailsRequired(
      ResponseMessage.Key.JOBDETAILS_REQUIRED_ERROR,
      ResponseMessage.Message.JOBDETAILS_REQUIRED_ERROR),
  dataAlreadyExist(
      ResponseMessage.Key.DATA_ALREADY_EXIST, ResponseMessage.Message.DATA_ALREADY_EXIST),
  invalidData(ResponseMessage.Key.INVALID_DATA, ResponseMessage.Message.INVALID_DATA),
  invalidCourseId(ResponseMessage.Key.INVALID_COURSE_ID, ResponseMessage.Message.INVALID_COURSE_ID),
  orgIdRequired(ResponseMessage.Key.ORG_ID_MISSING, ResponseMessage.Message.ORG_ID_MISSING),
  actorConnectionError(
      ResponseMessage.Key.ACTOR_CONNECTION_ERROR, ResponseMessage.Message.ACTOR_CONNECTION_ERROR),
  userAlreadyExists(
      ResponseMessage.Key.USER_ALREADY_EXISTS, ResponseMessage.Message.USER_ALREADY_EXISTS),
  invalidUserId(ResponseMessage.Key.INVALID_USER_ID, ResponseMessage.Key.INVALID_USER_ID),
  loginIdRequired(ResponseMessage.Key.LOGIN_ID_MISSING, ResponseMessage.Message.LOGIN_ID_MISSING),
  contentStatusRequired(
      ResponseMessage.Key.CONTENT_STATUS_MISSING_ERROR,
      ResponseMessage.Message.CONTENT_STATUS_MISSING_ERROR),
  esError(ResponseMessage.Key.ES_ERROR, ResponseMessage.Message.ES_ERROR),
  invalidPeriod(ResponseMessage.Key.INVALID_PERIOD, ResponseMessage.Message.INVALID_PERIOD),
  userNotFound(ResponseMessage.Key.USER_NOT_FOUND, ResponseMessage.Message.USER_NOT_FOUND),
  idRequired(ResponseMessage.Key.ID_REQUIRED_ERROR, ResponseMessage.Message.ID_REQUIRED_ERROR),
  dataTypeError(ResponseMessage.Key.DATA_TYPE_ERROR, ResponseMessage.Message.DATA_TYPE_ERROR),
  addressError(ResponseMessage.Key.ADDRESS_ERROR, ResponseMessage.Message.ADDRESS_ERROR),
  addressTypeError(
      ResponseMessage.Key.ADDRESS_TYPE_ERROR, ResponseMessage.Message.ADDRESS_TYPE_ERROR),
  educationNameError(
      ResponseMessage.Key.NAME_OF_INSTITUTION_ERROR,
      ResponseMessage.Message.NAME_OF_INSTITUTION_ERROR),
  jobNameError(ResponseMessage.Key.JOB_NAME_ERROR, ResponseMessage.Message.JOB_NAME_ERROR),
  educationDegreeError(
      ResponseMessage.Key.EDUCATION_DEGREE_ERROR, ResponseMessage.Message.EDUCATION_DEGREE_ERROR),
  organisationNameError(
      ResponseMessage.Key.NAME_OF_ORGANISATION_ERROR,
      ResponseMessage.Message.NAME_OF_ORGANISATION_ERROR),
  rolesRequired(ResponseMessage.Key.ROLES_MISSING, ResponseMessage.Message.ROLES_MISSING),
  invalidDateFormat(
      ResponseMessage.Key.INVALID_DATE_FORMAT, ResponseMessage.Message.INVALID_DATE_FORMAT),
  sourceAndExternalIdAlreadyExist(
      ResponseMessage.Key.SRC_EXTERNAL_ID_ALREADY_EXIST,
      ResponseMessage.Message.SRC_EXTERNAL_ID_ALREADY_EXIST),
  userAlreadyEnrolledThisCourse(
      ResponseMessage.Key.ALREADY_ENROLLED_COURSE, ResponseMessage.Message.ALREADY_ENROLLED_COURSE),
  pageAlreadyExist(
      ResponseMessage.Key.PAGE_ALREADY_EXIST, ResponseMessage.Message.PAGE_ALREADY_EXIST),
  contentTypeRequiredError(
      ResponseMessage.Key.CONTENT_TYPE_ERROR, ResponseMessage.Message.CONTENT_TYPE_ERROR),
  invalidPropertyError(
      ResponseMessage.Key.INVALID_PROPERTY_ERROR, ResponseMessage.Message.INVALID_PROPERTY_ERROR),
  usernameOrUserIdError(
      ResponseMessage.Key.USER_NAME_OR_ID_ERROR, ResponseMessage.Message.USER_NAME_OR_ID_ERROR),
  emailVerifiedError(
      ResponseMessage.Key.EMAIL_VERIFY_ERROR, ResponseMessage.Message.EMAIL_VERIFY_ERROR),
  phoneVerifiedError(
      ResponseMessage.Key.PHONE_VERIFY_ERROR, ResponseMessage.Message.PHONE_VERIFY_ERROR),
  bulkUserUploadError(
      ResponseMessage.Key.BULK_USER_UPLOAD_ERROR, ResponseMessage.Message.BULK_USER_UPLOAD_ERROR),
  dataSizeError(ResponseMessage.Key.DATA_SIZE_EXCEEDED, ResponseMessage.Message.DATA_SIZE_EXCEEDED),
  InvalidColumnError(
      ResponseMessage.Key.INVALID_COLUMN_NAME, ResponseMessage.Message.INVALID_COLUMN_NAME),
  userAccountlocked(
      ResponseMessage.Key.USER_ACCOUNT_BLOCKED, ResponseMessage.Message.USER_ACCOUNT_BLOCKED),
  userAlreadyActive(
      ResponseMessage.Key.USER_ALREADY_ACTIVE, ResponseMessage.Message.USER_ALREADY_ACTIVE),
  enrolmentTypeRequired(
      ResponseMessage.Key.ENROLMENT_TYPE_REQUIRED, ResponseMessage.Message.ENROLMENT_TYPE_REQUIRED),
  enrolmentIncorrectValue(
      ResponseMessage.Key.ENROLMENT_TYPE_VALUE_ERROR,
      ResponseMessage.Message.ENROLMENT_TYPE_VALUE_ERROR),
  courseBatchSatrtDateRequired(
      ResponseMessage.Key.COURSE_BATCH_START_DATE_REQUIRED,
      ResponseMessage.Message.COURSE_BATCH_START_DATE_REQUIRED),
  courseBatchStartDateError(
      ResponseMessage.Key.COURSE_BATCH_START_DATE_INVALID,
      ResponseMessage.Message.COURSE_BATCH_START_DATE_INVALID),
  dateFormatError(
      ResponseMessage.Key.DATE_FORMAT_ERRROR, ResponseMessage.Message.DATE_FORMAT_ERRROR),
  endDateError(ResponseMessage.Key.END_DATE_ERROR, ResponseMessage.Message.END_DATE_ERROR),
  csvError(ResponseMessage.Key.INVALID_CSV_FILE, ResponseMessage.Message.INVALID_CSV_FILE),
  invalidCourseBatchId(
      ResponseMessage.Key.INVALID_COURSE_BATCH_ID, ResponseMessage.Message.INVALID_COURSE_BATCH_ID),
  courseBatchIdRequired(
      ResponseMessage.Key.COURSE_BATCH_ID_MISSING, ResponseMessage.Message.COURSE_BATCH_ID_MISSING),
  enrollmentTypeValidation(
      ResponseMessage.Key.ENROLLMENT_TYPE_VALIDATION,
      ResponseMessage.Message.ENROLLMENT_TYPE_VALIDATION),
  courseCreatedForIsNull(
      ResponseMessage.Key.COURSE_CREATED_FOR_NULL, ResponseMessage.Message.COURSE_CREATED_FOR_NULL),
  userNotAssociatedToOrg(
      ResponseMessage.Key.USER_NOT_BELONGS_TO_ANY_ORG,
      ResponseMessage.Message.USER_NOT_BELONGS_TO_ANY_ORG),
  invalidObjectType(
      ResponseMessage.Key.INVALID_OBJECT_TYPE, ResponseMessage.Message.INVALID_OBJECT_TYPE),
  progressStatusError(
      ResponseMessage.Key.INVALID_PROGRESS_STATUS, ResponseMessage.Message.INVALID_PROGRESS_STATUS),
  courseBatchStartPassedDateError(
      ResponseMessage.Key.COURSE_BATCH_START_PASSED_DATE_INVALID,
      ResponseMessage.Message.COURSE_BATCH_START_PASSED_DATE_INVALID),
  csvFileEmpty(ResponseMessage.Key.EMPTY_CSV_FILE, ResponseMessage.Message.EMPTY_CSV_FILE),
  invalidRootOrgData(
      ResponseMessage.Key.INVALID_ROOT_ORG_DATA, ResponseMessage.Message.INVALID_ROOT_ORG_DATA),
  noDataForConsumption(ResponseMessage.Key.NO_DATA, ResponseMessage.Message.NO_DATA),
  invalidChannel(ResponseMessage.Key.INVALID_CHANNEL, ResponseMessage.Message.INVALID_CHANNEL),
  invalidProcessId(
      ResponseMessage.Key.INVALID_PROCESS_ID, ResponseMessage.Message.INVALID_PROCESS_ID),
  emailSubjectError(
      ResponseMessage.Key.EMAIL_SUBJECT_ERROR, ResponseMessage.Message.EMAIL_SUBJECT_ERROR),
  emailBodyError(ResponseMessage.Key.EMAIL_BODY_ERROR, ResponseMessage.Message.EMAIL_BODY_ERROR),
  recipientAddressError(
      ResponseMessage.Key.RECIPIENT_ADDRESS_ERROR, ResponseMessage.Message.RECIPIENT_ADDRESS_ERROR),
  storageContainerNameMandatory(
      ResponseMessage.Key.STORAGE_CONTAINER_NAME_MANDATORY,
      ResponseMessage.Message.STORAGE_CONTAINER_NAME_MANDATORY),
  userOrgAssociationError(
      ResponseMessage.Key.USER_ORG_ASSOCIATION_ERROR,
      ResponseMessage.Message.USER_ORG_ASSOCIATION_ERROR),
  cloudServiceError(
      ResponseMessage.Key.CLOUD_SERVICE_ERROR, ResponseMessage.Message.CLOUD_SERVICE_ERROR),
  badgeTypeIdMandatory(
      ResponseMessage.Key.BADGE_TYPE_ID_ERROR, ResponseMessage.Message.BADGE_TYPE_ID_ERROR),
  receiverIdMandatory(
      ResponseMessage.Key.RECEIVER_ID_ERROR, ResponseMessage.Message.RECEIVER_ID_ERROR),
  invalidReceiverId(
      ResponseMessage.Key.INVALID_RECEIVER_ID, ResponseMessage.Message.INVALID_RECEIVER_ID),
  invalidBadgeTypeId(
      ResponseMessage.Key.INVALID_BADGE_ID, ResponseMessage.Message.INVALID_BADGE_ID),
  invalidRole(ResponseMessage.Key.INVALID_ROLE, ResponseMessage.Message.INVALID_ROLE),
  saltValue(ResponseMessage.Key.INVALID_SALT, ResponseMessage.Message.INVALID_SALT),
  orgTypeMandatory(
      ResponseMessage.Key.ORG_TYPE_MANDATORY, ResponseMessage.Message.ORG_TYPE_MANDATORY),
  orgTypeAlreadyExist(
      ResponseMessage.Key.ORG_TYPE_ALREADY_EXIST, ResponseMessage.Message.ORG_TYPE_ALREADY_EXIST),
  orgTypeIdRequired(
      ResponseMessage.Key.ORG_TYPE_ID_REQUIRED_ERROR,
      ResponseMessage.Message.ORG_TYPE_ID_REQUIRED_ERROR),
  titleRequired(ResponseMessage.Key.TITLE_REQUIRED, ResponseMessage.Message.TITLE_REQUIRED),
  noteRequired(ResponseMessage.Key.NOTE_REQUIRED, ResponseMessage.Message.NOTE_REQUIRED),
  contentIdError(ResponseMessage.Key.CONTENT_ID_ERROR, ResponseMessage.Message.CONTENT_ID_ERROR),
  invalidTags(ResponseMessage.Key.INVALID_TAGS, ResponseMessage.Message.INVALID_TAGS),
  invalidNoteId(ResponseMessage.Key.NOTE_ID_INVALID, ResponseMessage.Message.NOTE_ID_INVALID),
  userDataEncryptionError(
      ResponseMessage.Key.USER_DATA_ENCRYPTION_ERROR,
      ResponseMessage.Message.USER_DATA_ENCRYPTION_ERROR),
  phoneNoFormatError(
      ResponseMessage.Key.INVALID_PHONE_NO_FORMAT, ResponseMessage.Message.INVALID_PHONE_NO_FORMAT),
  invalidWebPageData(
      ResponseMessage.Key.INVALID_WEBPAGE_DATA, ResponseMessage.Message.INVALID_WEBPAGE_DATA),
  invalidMediaType(
      ResponseMessage.Key.INVALID_MEDIA_TYPE, ResponseMessage.Message.INVALID_MEDIA_TYPE),
  invalidWebPageUrl(
      ResponseMessage.Key.INVALID_WEBPAGE_URL, ResponseMessage.Message.INVALID_WEBPAGE_URL),
  invalidDateRange(
      ResponseMessage.Key.INVALID_DATE_RANGE, ResponseMessage.Message.INVALID_DATE_RANGE),
  invalidBatchEndDateError(
      ResponseMessage.Key.INVALID_BATCH_END_DATE_ERROR,
      ResponseMessage.Message.INVALID_BATCH_END_DATE_ERROR),
  invalidBatchStartDateError(
      ResponseMessage.Key.INVALID_BATCH_START_DATE_ERROR,
      ResponseMessage.Message.INVALID_BATCH_START_DATE_ERROR),
  courseBatchEndDateError(
      ResponseMessage.Key.COURSE_BATCH_END_DATE_ERROR,
      ResponseMessage.Message.COURSE_BATCH_END_DATE_ERROR),
  BatchCloseError(
      ResponseMessage.Key.COURSE_BATCH_IS_CLOSED_ERROR,
      ResponseMessage.Message.COURSE_BATCH_IS_CLOSED_ERROR),
  newPasswordRequired(
      ResponseMessage.Key.CONFIIRM_PASSWORD_MISSING,
      ResponseMessage.Message.CONFIIRM_PASSWORD_MISSING),
  newPasswordEmpty(
      ResponseMessage.Key.CONFIIRM_PASSWORD_EMPTY, ResponseMessage.Message.CONFIIRM_PASSWORD_EMPTY),
  samePasswordError(
      ResponseMessage.Key.SAME_PASSWORD_ERROR, ResponseMessage.Message.SAME_PASSWORD_ERROR),
  endorsedUserIdRequired(
      ResponseMessage.Key.ENDORSED_USER_ID_REQUIRED,
      ResponseMessage.Message.ENDORSED_USER_ID_REQUIRED),
  canNotEndorse(ResponseMessage.Key.CAN_NOT_ENDORSE, ResponseMessage.Message.CAN_NOT_ENDORSE),
  invalidOrgTypeId(
      ResponseMessage.Key.INVALID_ORG_TYPE_ID_ERROR,
      ResponseMessage.Message.INVALID_ORG_TYPE_ID_ERROR),
  invalidOrgType(
      ResponseMessage.Key.INVALID_ORG_TYPE_ERROR, ResponseMessage.Message.INVALID_ORG_TYPE_ERROR),
  tableOrDocNameError(
      ResponseMessage.Key.TABLE_OR_DOC_NAME_ERROR, ResponseMessage.Message.TABLE_OR_DOC_NAME_ERROR),
  emailorPhoneRequired(
      ResponseMessage.Key.EMAIL_OR_PHONE_MISSING, ResponseMessage.Message.EMAIL_OR_PHONE_MISSING),
  PhoneNumberInUse(
      ResponseMessage.Key.PHONE_ALREADY_IN_USE, ResponseMessage.Message.PHONE_ALREADY_IN_USE),
  invalidClientName(
      ResponseMessage.Key.INVALID_CLIENT_NAME, ResponseMessage.Message.INVALID_CLIENT_NAME),
  invalidClientId(ResponseMessage.Key.INVALID_CLIENT_ID, ResponseMessage.Message.INVALID_CLIENT_ID),
  userPhoneUpdateFailed(
      ResponseMessage.Key.USER_PHONE_UPDATE_FAILED,
      ResponseMessage.Message.USER_PHONE_UPDATE_FAILED),
  esUpdateFailed(ResponseMessage.Key.ES_UPDATE_FAILED, ResponseMessage.Message.ES_UPDATE_FAILED),
  updateFailed(ResponseMessage.Key.UPDATE_FAILED, ResponseMessage.Message.UPDATE_FAILED),
  invalidTypeValue(ResponseMessage.Key.INVALID_TYPE_VALUE, ResponseMessage.Key.INVALID_TYPE_VALUE),
  invalidLocationId(
      ResponseMessage.Key.INVALID_LOCATION_ID, ResponseMessage.Message.INVALID_LOCATION_ID),
  invalidHashTagId(
      ResponseMessage.Key.INVALID_HASHTAG_ID, ResponseMessage.Message.INVALID_HASHTAG_ID),
  invalidUsrOrgData(
      ResponseMessage.Key.INVALID_USR_ORG_DATA, ResponseMessage.Message.INVALID_USR_ORG_DATA),
  visibilityInvalid(
      ResponseMessage.Key.INVALID_VISIBILITY_REQUEST,
      ResponseMessage.Message.INVALID_VISIBILITY_REQUEST),
  invalidTopic(ResponseMessage.Key.INVALID_TOPIC_NAME, ResponseMessage.Message.INVALID_TOPIC_NAME),
  invalidTopicData(
      ResponseMessage.Key.INVALID_TOPIC_DATA, ResponseMessage.Message.INVALID_TOPIC_DATA),
  invalidNotificationType(
      ResponseMessage.Key.INVALID_NOTIFICATION_TYPE,
      ResponseMessage.Message.INVALID_NOTIFICATION_TYPE),
  notificationTypeSupport(
      ResponseMessage.Key.INVALID_NOTIFICATION_TYPE_SUPPORT,
      ResponseMessage.Message.INVALID_NOTIFICATION_TYPE_SUPPORT),
  emailInUse(ResponseMessage.Key.EMAIL_IN_USE, ResponseMessage.Message.EMAIL_IN_USE),
  invalidPhoneNumber(
      ResponseMessage.Key.INVALID_PHONE_NUMBER, ResponseMessage.Message.INVALID_PHONE_NUMBER),
  invalidCountryCode(
      ResponseMessage.Key.INVALID_COUNTRY_CODE, ResponseMessage.Message.INVALID_COUNTRY_CODE),
  duplicatePhoneData(
      ResponseMessage.Key.DUPLICATE_PHONE_DATA, ResponseMessage.Message.DUPLICATE_PHONE_DATA),
  duplicateEmailData(
      ResponseMessage.Key.DUPLICATE_EMAIL_DATA, ResponseMessage.Message.DUPLICATE_EMAIL_DATA),
  locationIdRequired(
      ResponseMessage.Key.LOCATION_ID_REQUIRED, ResponseMessage.Message.LOCATION_ID_REQUIRED),
  functionalityMissing(ResponseMessage.Key.NOT_SUPPORTED, ResponseMessage.Message.NOT_SUPPORTED),
  userNameOrUserIdRequired(
      ResponseMessage.Key.USERNAME_USERID_MISSING, ResponseMessage.Message.USERNAME_USERID_MISSING),
  channelRegFailed(
      ResponseMessage.Key.CHANNEL_REG_FAILED, ResponseMessage.Message.CHANNEL_REG_FAILED),
  invalidCourseCreatorId(
      ResponseMessage.Key.INVALID_COURSE_CREATOR_ID,
      ResponseMessage.Message.INVALID_COURSE_CREATOR_ID),
  userNotAssociatedToRootOrg(
      ResponseMessage.Key.USER_NOT_ASSOCIATED_TO_ROOT_ORG,
      ResponseMessage.Message.USER_NOT_ASSOCIATED_TO_ROOT_ORG),
  slugIsNotUnique(
      ResponseMessage.Key.SLUG_IS_NOT_UNIQUE, ResponseMessage.Message.SLUG_IS_NOT_UNIQUE),
  invalidDataForCreateBadgeIssuer(
      ResponseMessage.Key.INVALID_CREATE_BADGE_ISSUER_DATA,
      ResponseMessage.Message.INVALID_CREATE_BADGE_ISSUER_DATA),
  issuerIdRequired(
      ResponseMessage.Key.ISSUER_ID_REQUIRED, ResponseMessage.Message.ISSUER_ID_REQUIRED),
  badgeIdRequired(ResponseMessage.Key.BADGE_ID_REQUIRED, ResponseMessage.Message.BADGE_ID_REQUIRED),
  badgeNameRequired(
      ResponseMessage.Key.BADGE_NAME_REQUIRED, ResponseMessage.Message.BADGE_NAME_REQUIRED),
  badgeDescriptionRequired(
      ResponseMessage.Key.BADGE_DESCRIPTION_REQUIRED,
      ResponseMessage.Message.BADGE_DESCRIPTION_REQUIRED),
  badgeCriteriaRequired(
      ResponseMessage.Key.BADGE_CRITERIA_REQUIRED, ResponseMessage.Message.BADGE_CRITERIA_REQUIRED),
  rootOrgIdRequired(
      ResponseMessage.Key.ROOT_ORG_ID_REQUIRED, ResponseMessage.Message.ROOT_ORG_ID_REQUIRED),
  badgeTypeRequired(
      ResponseMessage.Key.BADGE_TYPE_REQUIRED, ResponseMessage.Message.BADGE_TYPE_REQUIRED),
  invalidBadgeType(
      ResponseMessage.Key.INVALID_BADGE_TYPE, ResponseMessage.Message.INVALID_BADGE_TYPE),
  invalidBadgeSubtype(
      ResponseMessage.Key.INVALID_BADGE_SUBTYPE, ResponseMessage.Message.INVALID_BADGE_SUBTYPE),
  invalidBadgeRole(
      ResponseMessage.Key.INVALID_BADGE_ROLE, ResponseMessage.Message.INVALID_BADGE_ROLE),
  badgeRolesRequired(
      ResponseMessage.Key.BADGE_ROLES_REQUIRED, ResponseMessage.Message.BADGE_ROLES_REQUIRED),
  badgeImageRequired(
      ResponseMessage.Key.BADGE_IMAGE_REQUIRED, ResponseMessage.Message.BADGE_IMAGE_REQUIRED),
  recipientEmailRequired(
      ResponseMessage.Key.RECIPIENT_EMAIL_REQUIRED,
      ResponseMessage.Message.RECIPIENT_EMAIL_REQUIRED),
  evidenceRequired(
      ResponseMessage.Key.ASSERTION_EVIDENCE_REQUIRED,
      ResponseMessage.Message.ASSERTION_EVIDENCE_REQUIRED),
  assertionIdRequired(
      ResponseMessage.Key.ASSERTION_ID_REQUIRED, ResponseMessage.Message.ASSERTION_ID_REQUIRED),
  recipientIdRequired(
      ResponseMessage.Key.RECIPIENT_ID_REQUIRED, ResponseMessage.Message.RECIPIENT_ID_REQUIRED),
  recipientTypeRequired(
      ResponseMessage.Key.RECIPIENT_TYPE_REQUIRED, ResponseMessage.Message.RECIPIENT_TYPE_REQUIRED),
  badgingserverError(
      ResponseMessage.Key.BADGING_SERVER_ERROR, ResponseMessage.Message.BADGING_SERVER_ERROR),
  resourceNotFound(
      ResponseMessage.Key.RESOURCE_NOT_FOUND, ResponseMessage.Message.RESOURCE_NOT_FOUND),
  sizeLimitExceed(
      ResponseMessage.Key.MAX_ALLOWED_SIZE_LIMIT_EXCEED,
      ResponseMessage.Message.MAX_ALLOWED_SIZE_LIMIT_EXCEED),
  slugRequired(ResponseMessage.Key.SLUG_REQUIRED, ResponseMessage.Message.SLUG_REQUIRED),
  invalidIssuerId(ResponseMessage.Key.INVALID_ISSUER_ID, ResponseMessage.Message.INVALID_ISSUER_ID),
  revocationReasonRequired(
      ResponseMessage.Key.REVOCATION_REASON_REQUIRED,
      ResponseMessage.Message.REVOCATION_REASON_REQUIRED),
  badgeAssertionAlreadyRevoked(
      ResponseMessage.Key.ALREADY_REVOKED, ResponseMessage.Message.ALREADY_REVOKED),
  invalidRecipientType(
      ResponseMessage.Key.INVALID_RECIPIENT_TYPE, ResponseMessage.Message.INVALID_RECIPIENT_TYPE),
  customClientError(
      ResponseMessage.Key.CUSTOM_CLIENT_ERROR, ResponseMessage.Message.CUSTOM_CLIENT_ERROR),
  customResourceNotFound(
      ResponseMessage.Key.CUSTOM_RESOURCE_NOT_FOUND_ERROR,
      ResponseMessage.Message.CUSTOM_RESOURCE_NOT_FOUND_ERROR),
  customServerError(
      ResponseMessage.Key.CUSTOM_SERVER_ERROR, ResponseMessage.Message.CUSTOM_SERVER_ERROR),
  inactiveUser(ResponseMessage.Key.INACTIVE_USER, ResponseMessage.Message.INACTIVE_USER),
  userInactiveForThisOrg(
      ResponseMessage.Key.USER_INACTIVE_FOR_THIS_ORG,
      ResponseMessage.Message.USER_INACTIVE_FOR_THIS_ORG),
  userUpdateToOrgFailed(
      ResponseMessage.Key.USER_UPDATE_FAILED_FOR_THIS_ORG,
      ResponseMessage.Message.USER_UPDATE_FAILED_FOR_THIS_ORG),
  preferenceKeyMissing(
      ResponseMessage.Key.USER_UPDATE_FAILED_FOR_THIS_ORG,
      ResponseMessage.Message.USER_UPDATE_FAILED_FOR_THIS_ORG),
  pageDoesNotExist(ResponseMessage.Key.PAGE_NOT_EXIST, ResponseMessage.Message.PAGE_NOT_EXIST),
  orgDoesNotExist(ResponseMessage.Key.ORG_NOT_EXIST, ResponseMessage.Message.ORG_NOT_EXIST),
  invalidPageSource(
      ResponseMessage.Key.INVALID_PAGE_SOURCE, ResponseMessage.Message.INVALID_PAGE_SOURCE),
  badgeSubTypeRequired(
      ResponseMessage.Key.BADGE_SUBTYPE_REQUIRED, ResponseMessage.Message.BADGE_SUBTYPE_REQUIRED),
  locationTypeRequired(
      ResponseMessage.Key.LOCATION_TYPE_REQUIRED, ResponseMessage.Message.LOCATION_TYPE_REQUIRED),
  invalidRequestDataForLocation(
      ResponseMessage.Key.INVALID_REQUEST_DATA_FOR_LOCATION,
      ResponseMessage.Message.INVALID_REQUEST_DATA_FOR_LOCATION),
  alreadyExists(ResponseMessage.Key.ALREADY_EXISTS, ResponseMessage.Message.ALREADY_EXISTS),
  invalidValue(ResponseMessage.Key.INVALID_VALUE, ResponseMessage.Message.INVALID_VALUE),
  parentCodeAndIdValidationError(
      ResponseMessage.Key.PARENT_CODE_AND_PARENT_ID_MISSING,
      ResponseMessage.Message.PARENT_CODE_AND_PARENT_ID_MISSING),
  invalidParameter(
      ResponseMessage.Key.INVALID_PARAMETER, ResponseMessage.Message.INVALID_PARAMETER),
  invalidLocationDeleteRequest(
      ResponseMessage.Key.INVALID_LOCATION_DELETE_REQUEST,
      ResponseMessage.Message.INVALID_LOCATION_DELETE_REQUEST),
  locationTypeConflicts(
      ResponseMessage.Key.LOCATION_TYPE_CONFLICTS, ResponseMessage.Message.LOCATION_TYPE_CONFLICTS),
  mandatoryParamsMissing(
      ResponseMessage.Key.MANDATORY_PARAMETER_MISSING,
      ResponseMessage.Message.MANDATORY_PARAMETER_MISSING),
  unupdatableField(
      ResponseMessage.Key.UPDATE_NOT_ALLOWED, ResponseMessage.Message.UPDATE_NOT_ALLOWED),
  mandatoryHeadersMissing(
      ResponseMessage.Key.MANDATORY_HEADER_MISSING,
      ResponseMessage.Message.MANDATORY_HEADER_MISSING),
  invalidParameterValue(
      ResponseMessage.Key.INVALID_PARAMETER_VALUE, ResponseMessage.Message.INVALID_PARAMETER_VALUE),
  parentNotAllowed(
      ResponseMessage.Key.PARENT_NOT_ALLOWED, ResponseMessage.Message.PARENT_NOT_ALLOWED),
  missingFileAttachment(
      ResponseMessage.Key.MISSING_FILE_ATTACHMENT, ResponseMessage.Message.MISSING_FILE_ATTACHMENT),
  fileAttachmentSizeNotConfigured(
      ResponseMessage.Key.FILE_ATTACHMENT_SIZE_NOT_CONFIGURED,
      ResponseMessage.Message.FILE_ATTACHMENT_SIZE_NOT_CONFIGURED),
  emptyFile(ResponseMessage.Key.EMPTY_FILE, ResponseMessage.Message.EMPTY_FILE),
  invalidColumns(ResponseMessage.Key.INVALID_COLUMNS, ResponseMessage.Message.INVALID_COLUMNS),
  conflictingOrgLocations(
      ResponseMessage.Key.CONFLICTING_ORG_LOCATIONS,
      ResponseMessage.Message.CONFLICTING_ORG_LOCATIONS),
  unableToCommunicateWithActor(
      ResponseMessage.Key.UNABLE_TO_COMMUNICATE_WITH_ACTOR,
      ResponseMessage.Message.UNABLE_TO_COMMUNICATE_WITH_ACTOR),
  emptyHeaderLine(ResponseMessage.Key.EMPTY_HEADER_LINE, ResponseMessage.Message.EMPTY_HEADER_LINE),
  invalidRequestParameter(
      ResponseMessage.Key.INVALID_REQUEST_PARAMETER,
      ResponseMessage.Message.INVALID_REQUEST_PARAMETER),
  rootOrgAssociationError(
      ResponseMessage.Key.ROOT_ORG_ASSOCIATION_ERROR,
      ResponseMessage.Message.ROOT_ORG_ASSOCIATION_ERROR),
  dependentParameterMissing(
      ResponseMessage.Key.DEPENDENT_PARAMETER_MISSING,
      ResponseMessage.Message.DEPENDENT_PARAMETER_MISSING),
  OK(200),
  CLIENT_ERROR(400),
  SERVER_ERROR(500),
  RESOURCE_NOT_FOUND(404),
  UNAUTHORIZED(401),
  REDIRECTION_REQUIRED(302);
  private int responseCode;
  /** error code contains String value */
  private String errorCode;
  /** errorMessage contains proper error message. */
  private String errorMessage;

  /**
   * @param errorCode String
   * @param errorMessage String
   */
  private ResponseCode(String errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  private ResponseCode(String errorCode, String errorMessage, int responseCode) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.responseCode = responseCode;
  }

  /**
   * @param errorCode
   * @return
   */
  public String getMessage(int errorCode) {
    return "";
  }

  /** @return */
  public String getErrorCode() {
    return errorCode;
  }

  /** @param errorCode */
  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  /** @return */
  public String getErrorMessage() {
    return errorMessage;
  }

  /** @param errorMessage */
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
    if (StringUtils.isBlank(code)) {
      return "";
    }
    ResponseCode responseCodes[] = ResponseCode.values();
    for (ResponseCode actionState : responseCodes) {
      if (actionState.getErrorCode().equals(code)) {
        return actionState.getErrorMessage();
      }
    }
    return "";
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
   * This method will take header response code as int value and it provide matched enum value, if
   * code is not matched or exception occurs then it will provide SERVER_ERROR
   *
   * @param code int
   * @return HeaderResponseCode
   */
  public static ResponseCode getHeaderResponseCode(int code) {
    if (code > 0) {
      try {
        ResponseCode[] arr = ResponseCode.values();
        if (null != arr) {
          for (ResponseCode rc : arr) {
            if (rc.getResponseCode() == code) return rc;
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
   * @param errorCode
   * @return String
   */
  public static ResponseCode getResponse(String errorCode) {
    if (StringUtils.isBlank(errorCode)) {
      return null;
    } else if (JsonKey.UNAUTHORIZED.equals(errorCode)) {
      return ResponseCode.unAuthorized;
    } else {
      ResponseCode value = null;
      ResponseCode responseCodes[] = ResponseCode.values();
      for (ResponseCode response : responseCodes) {
        if (response.getErrorCode().equals(errorCode)) {
          return response;
        }
      }
      return value;
    }
  }
}
