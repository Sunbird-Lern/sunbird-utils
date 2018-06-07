package org.sunbird.util;

import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.models.util.ProjectUtil;

/**
 * Helper utility class for generating response ID based on received request.
 *
 * @author Manzarul
 */
public class ResponseIdUtil {
  private static final String version = "v1";

  /**
   * Enum consisting delimiters
   *
   * @author Manzarul
   */
  public enum Delimiter {
    slash("/"),
    dot("."),
    escape("//"),
    question("?");
    String symbol;

    Delimiter(String symbol) {
      this.symbol = symbol;
    }

    public String getSymbol() {
      return symbol;
    }

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }
  }

  /**
   * Construct API response ID based on request path and method.
   *
   * @param path Request URI path (e.g. /v1/user/create)
   * @param method Request method (e.g. GET)
   * @return API response ID for given request URI.
   */
  public String getApiResponseId(String path, String method) {
    String val;
    if (ProjectUtil.Method.GET.name().equalsIgnoreCase(method)) {
      val = getResponseId(path);
      if (StringUtils.isBlank(val)) {
        String[] splitPathArray = path.split("[/]");
        String tempPath = removeLastValue(splitPathArray);
        val = getResponseId(tempPath);
      }
    } else {
      val = getResponseId(path);
    }
    return val;
  }

  /**
   * Construct API response ID based on request path.
   *
   * @param requestPath Request URI path (e.g. /v1/user/create)
   * @return API response ID for given request URI.
   */
  public static String getResponseId(String requestPath) {

    String path = requestPath;
    final String ver = Delimiter.slash.getSymbol() + version;
    path = path.trim();
    StringBuilder builder = new StringBuilder("");
    if (path.startsWith(ver)) {
      String requestUrl = (path.split("\\?"))[0];
      requestUrl = requestUrl.replaceFirst(ver, "api");
      String[] list = requestUrl.split("/");
      for (String str : list) {
        if (str.matches("[A-Za-z]+")) {
          builder.append(str).append(Delimiter.dot.getSymbol());
        }
      }
      builder.deleteCharAt(builder.length() - 1);
    } else {
      if ("/health".equalsIgnoreCase(path)) {
        builder.append("api.all.health");
      }
    }
    return builder.toString();
  }

  /**
   * Remove last value (path param) from array and return rest of request path URI.
   *
   * @param splitPathArray Request URI path split into array based on "/"
   * @return Request URI path without path param (e.g. return /v1/user/read for
   *     /v1/user/read/{userId})
   */
  private String removeLastValue(String splitPathArray[]) {

    StringBuilder builder = new StringBuilder();
    if (splitPathArray != null && splitPathArray.length > 0) {
      for (int i = 1; i < splitPathArray.length - 1; i++) {
        builder.append(Delimiter.slash.getSymbol() + splitPathArray[i]);
      }
    }
    return builder.toString();
  }
}
