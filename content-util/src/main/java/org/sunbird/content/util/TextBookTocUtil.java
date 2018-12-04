package org.sunbird.content.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;

public class TextBookTocUtil {

  private static ObjectMapper mapper = new ObjectMapper();

  public static <T> T getObjectFrom(String s, Class<T> clazz) {
    if (StringUtils.isBlank(s))
      throw new ProjectCommonException(
          ResponseCode.errorProcessingRequest.getErrorCode(),
          ResponseCode.errorProcessingRequest.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());

    try {
      return mapper.readValue(
          ProjectUtil.getConfigValue(JsonKey.TEXTBOOK_TOC_INPUT_MAPPING), clazz);
    } catch (IOException e) {
      ProjectLogger.log("Error Mapping File input Mapping Properties.");
      throw new ProjectCommonException(
          ResponseCode.errorProcessingRequest.getErrorCode(),
          ResponseCode.errorProcessingRequest.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
  }
}
