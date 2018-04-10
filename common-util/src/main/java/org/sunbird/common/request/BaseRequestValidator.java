package org.sunbird.common.request;

import org.apache.commons.lang.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * Base request validator class to house common validation methods.
 *
 * @author B Vinaya Kumar
 */
public class BaseRequestValidator {
    /**
     * Helper method which throws an exception if given parameter value is blank (null or empty).
     *
     * @param value Request parameter value.
     * @param error Error to be thrown in case of validation error.
     */
    public void validateParam(String value, ResponseCode error) {
        if (StringUtils.isBlank(value)) {
            throw new ProjectCommonException(error.getErrorCode(), error.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
        }
    }
}
