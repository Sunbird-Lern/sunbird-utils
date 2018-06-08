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

  private static List<String> restrictedUrlList;
  private static List<String> publicUrlList;
  private static List<String> privateUrlList;

  public static List<String> getRestrictedUrlList() {
    return restrictedUrlList;
  }

  public static void setRestrictedUrlList(List<String> restrictedUrlList) {
    RequestInterceptor.restrictedUrlList = restrictedUrlList;
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
      String userToken = request.getHeader(HeaderParam.X_Authenticated_User_Token.getName());
      String authClientToken =
          request.getHeader(HeaderParam.X_Authenticated_Client_Token.getName());
      String authClientId = request.getHeader(HeaderParam.X_Authenticated_Client_Id.getName());

      if (StringUtils.isNotBlank(userToken)) {
        clientId = AuthenticationHelper.verifyUserToken(userToken);
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
   * @param requestUrl Request URI
   * @return whether URI part of public URL or not ,If URI is public URI return true else false.
   */
  public static boolean isRequestInExcludeList(String requestUrl) {
    boolean resp = false;
    if (StringUtils.isNotBlank(requestUrl)) {
      if (publicUrlList.contains(requestUrl)) {
        resp = true;
      } else {
        String[] splitPath = requestUrl.split("[/]");
        String url = removeLastValue(splitPath);
        if (publicUrlList.contains(url)) {
          resp = true;
        }
      }
    }
    return resp;
  }

  /**
   * Method to remove last value
   *
   * @param splitPath String []
   * @return String
   */
  private static String removeLastValue(String splitPath[]) {

    StringBuilder builder = new StringBuilder();
    if (splitPath != null && splitPath.length > 0) {
      for (int i = 1; i < splitPath.length - 1; i++) {
        builder.append("/" + splitPath[i]);
      }
    }
    return builder.toString();
  }
}
