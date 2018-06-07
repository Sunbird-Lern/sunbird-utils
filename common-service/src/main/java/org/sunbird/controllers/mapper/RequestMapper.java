package org.sunbird.controllers.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.responsecode.ResponseCode;
import play.libs.Json;

/**
 * This class will map the requested json data into custom class
 *
 * @author Manzarul
 */
public class RequestMapper {

  /**
   * Method to map request
   *
   * @param requestData Request body JSON data
   * @param obj Map to request type
   * @exception RuntimeException
   * @return <T> Return mapped request
   */
  public static <T> Object mapRequest(JsonNode requestData, Class<T> obj) throws RuntimeException {

    try {
      return Json.fromJson(requestData, obj);
    } catch (Exception e) {
      ProjectLogger.log(
          "RequestMapper: mapRequest: Generic exception occurred = " + e.getMessage(), e);
      ProjectLogger.log(
          "RequestMapper: mapRequest: Generic exception for request with data = "
              + requestData.toString(),
          LoggerEnum.INFO.name());
      throw new ProjectCommonException(
          ResponseCode.invalidData.getErrorCode(),
          ResponseCode.invalidData.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }
}
