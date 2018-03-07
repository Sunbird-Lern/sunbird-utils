package org.sunbird.common.request.badge;

import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.BadgingJsonKey;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * This class will do the request validation for Badge assertion.
 * @author Manzarul
 *
 */
public class BadgeAssertionValidator {

	private static final int ERROR_CODE = ResponseCode.CLIENT_ERROR.getResponseCode();

	/*
	 * Default private constructor
	 */
	private BadgeAssertionValidator() {
	}
	
	/**
	 * This method will do the basic validation of badge assertion 
	 * request.
	 * @param request Request
	 */
	public static void validateBadgeAssertion(Request request) {

		if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(BadgingJsonKey.ISSUER_SLUG))) {
			throw new ProjectCommonException(ResponseCode.issuerSlugRequired.getErrorCode(),
					ResponseCode.issuerSlugRequired.getErrorMessage(), ERROR_CODE);
		}
		if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(BadgingJsonKey.BADGE_CLASS_SLUG))) {
			throw new ProjectCommonException(ResponseCode.badgeSlugRequired.getErrorCode(),
					ResponseCode.badgeSlugRequired.getErrorMessage(), ERROR_CODE);

		}
		if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(BadgingJsonKey.RECIPIENT_EMAIL))) {
			throw new ProjectCommonException(ResponseCode.recipientEmailRequired.getErrorCode(),
					ResponseCode.recipientEmailRequired.getErrorMessage(), ERROR_CODE);
		}
		if (!ProjectUtil.isEmailvalid((String) request.getRequest().get(BadgingJsonKey.RECIPIENT_EMAIL))) {
			throw new ProjectCommonException(ResponseCode.emailFormatError.getErrorCode(),
					ResponseCode.emailFormatError.getErrorMessage(), ERROR_CODE);

		}
		if (ProjectUtil.isStringNullOREmpty((String) request.getRequest().get(BadgingJsonKey.EVIDENCE))) {
			throw new ProjectCommonException(ResponseCode.evidenceRequired.getErrorCode(),
					ResponseCode.evidenceRequired.getErrorMessage(), ERROR_CODE);
		}

	}
	
	/**
	 * This method will verify null and empty check for requested param.
	 * if any param is null or empty then it will throw exception
	 * @param issuerSlug String
	 * @param badgeSlug String
	 * @param assertionSlug String
	 */
	public static void validategetBadgeAssertion(String issuerSlug, String badgeSlug, String assertionSlug) {
		if (ProjectUtil.isStringNullOREmpty(badgeSlug)) {
			throw new ProjectCommonException(ResponseCode.issuerSlugRequired.getErrorCode(),
					ResponseCode.issuerSlugRequired.getErrorMessage(), ERROR_CODE);
		}
		if (ProjectUtil.isStringNullOREmpty(badgeSlug)) {
			throw new ProjectCommonException(ResponseCode.badgeSlugRequired.getErrorCode(),
					ResponseCode.badgeSlugRequired.getErrorMessage(), ERROR_CODE);

		}
		if (ProjectUtil.isStringNullOREmpty(assertionSlug)) {
			throw new ProjectCommonException(ResponseCode.assertionSlugRequired.getErrorCode(),
					ResponseCode.assertionSlugRequired.getErrorMessage(), ERROR_CODE);

		}
	}

}
