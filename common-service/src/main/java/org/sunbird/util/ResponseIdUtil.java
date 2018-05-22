package org.sunbird.util;

import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.models.util.ProjectUtil;

/**
 * This class is responsible for generating response id for request.
 *
 * @author Manzarul
 */
public class ResponseIdUtil {
  private static final String version = "v1";

  /**
   * This enum will hold different -2 delimiter
   *
   * @author Manzarul
   */
  public enum Delimiter {
    slash("/"),
    dot("."),
    escape("//"),
    question("?");
    String symbol;

    private Delimiter(String symbol) {
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
   * Method to get API response Id
   *
   * @param path api uri Ex: /v1/user/create
   * @param method api method Ex: GET,POST etc
   * @return api response id Ex: api.user.create
   */
  public String getApiResponseId(String path, String method) {
    String val = "";
    if (ProjectUtil.Method.GET.name().equalsIgnoreCase(method)) {
      val = getResponseId(path);
      if (StringUtils.isBlank(val)) {
        String[] splitedpath = path.split("[" + Delimiter.slash.getSymbol() + "]");
        String tempPath = removeLastValue(splitedpath);
        val = getResponseId(tempPath);
      }
    } else {
      val = getResponseId(path);
    }
    return val;
  }

  /**
   * Method to get the response id on basis of request path.
   *
   * @param requestPath api uri Ex: /v1/user/create
   * @return api response id Ex: api.user.create
   */
  public static String getResponseId(String requestPath) {

    String path = requestPath;
    final String ver = Delimiter.slash.getSymbol() + version;
    path = path.trim();
    StringBuilder builder = new StringBuilder("");
    if (path.startsWith(ver)) {
      String requestUrl =
          (path.split(Delimiter.escape.getSymbol() + Delimiter.question.getSymbol()))[0];
      requestUrl = requestUrl.replaceFirst(ver, "api");
      String[] list = requestUrl.split(Delimiter.escape.getSymbol());
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
   * Method to remove last value form array. example uri is /v1/user/read/{userId}
   *
   * @param splited splitted value of uri based on "/"
   * @return /v1/user/read from /v1/user/read/{userId}
   */
  private String removeLastValue(String splited[]) {

    StringBuilder builder = new StringBuilder();
    if (splited != null && splited.length > 0) {
      for (int i = 1; i < splited.length - 1; i++) {
        builder.append(Delimiter.slash.getSymbol() + splited[i]);
      }
    }
    return builder.toString();
  }
}
