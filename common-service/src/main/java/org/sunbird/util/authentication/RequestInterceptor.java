package org.sunbird.util.authentication;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.HeaderParam;
import play.mvc.Http;
import play.mvc.Http.Request;

/**
 * This class will do the request header validation
 *
 * @author Amit Kumar
 */
public class RequestInterceptor {

  private static List<String> restrictedUriList;
  private static List<String> publicUrlList;
  private static List<String> privateUrlList;

  public static List<String> getRestrictedUriList() {
    return restrictedUriList;
  }

  public static void setRestrictedUriList(List<String> restrictedUriList) {
    RequestInterceptor.restrictedUriList = restrictedUriList;
  }

  public static List<String> getPublicUrlList() {
    return publicUrlList;
  }

  public static void setPublicUrlList(List<String> publicUrlList) {
    RequestInterceptor.publicUrlList = publicUrlList;
  }

  public static List<String> getPrivateUrlList() {
    return privateUrlList;
  }

  public static void setPrivateUrlList(List<String> privateUrlList) {
    RequestInterceptor.privateUrlList = privateUrlList;
  }

  private RequestInterceptor() {}

  /**
   * This Method will do the request header validation.
   *
   * @param ctx Request
   * @return String
   */
  public static String verifyRequestData(Http.Context ctx) {
    Request request = ctx.request();
    if (!isRequestInExcludeList(request.path())) {
      String clientId = JsonKey.UNAUTHORIZED;
      String accessToken = request.getHeader(HeaderParam.X_Access_TokenId.getName());
      String authClientToken =
          request.getHeader(HeaderParam.X_Authenticated_Client_Token.getName());
      String authClientId = request.getHeader(HeaderParam.X_Authenticated_Client_Id.getName());

      if (StringUtils.isNotBlank(accessToken)) {
        clientId = AuthenticationHelper.verifyUserAccesToken(accessToken);
      } else if (StringUtils.isNotBlank(authClientToken) && StringUtils.isNotBlank(authClientId)) {
        clientId = AuthenticationHelper.verifyClientAccessToken(authClientId, authClientToken);
        if (!JsonKey.UNAUTHORIZED.equals(clientId)) {
          ctx.flash().put(JsonKey.AUTH_WITH_MASTER_KEY, Boolean.toString(true));
        }
      }
      return clientId;
    } else {
      return JsonKey.ANONYMOUS;
    }
  }

  /**
   * this method will check incoming request required validation or not. if this method return true
   * it means no need of validation other wise validation is required.
   *
   * @param request Stirng URI
   * @return boolean
   */
  public static boolean isRequestInExcludeList(String request) {
    boolean resp = false;
    if (!StringUtils.isBlank(request)) {
      if (publicUrlList.contains(request)) {
        resp = true;
      } else {
        String[] splitedpath = request.split("[/]");
        String tempRequest = removeLastValue(splitedpath);
        if (publicUrlList.contains(tempRequest)) {
          resp = true;
        }
      }
    }
    return resp;
  }

  /**
   * Method to remove last value
   *
   * @param splited String []
   * @return String
   */
  private static String removeLastValue(String splited[]) {

    StringBuilder builder = new StringBuilder();
    if (splited != null && splited.length > 0) {
      for (int i = 1; i < splited.length - 1; i++) {
        builder.append("/" + splited[i]);
      }
    }
    return builder.toString();
  }
}
