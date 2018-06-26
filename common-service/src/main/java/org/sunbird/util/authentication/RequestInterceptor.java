package org.sunbird.util.authentication;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.HeaderParam;
import play.mvc.Http;
import play.mvc.Http.Request;

/**
 * Request interceptor responsible to authenticated HTTP requests
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
   * Authenticates given HTTP request context
   *
   * @param ctx HTTP play request context
   * @return User or Client ID for authenticated request. For unauthenticated requests, UNAUTHORIZED
   *     is returned
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
   * Checks if request URL is in excluded (i.e. public) URL list or not
   *
   * @param requestUrl Request URL
   * @return True if URL is in excluded (public) URLs. Otherwise, returns false
   */
  public static boolean isRequestInExcludeList(String requestUrl) {
    boolean resp = false;
    if (StringUtils.isNotBlank(requestUrl)) {
      if (publicUrlList.contains(requestUrl)) {
        resp = true;
      } else {
        String[] splitPath = requestUrl.split("[/]");
        String urlWithoutPathParam = removeLastValue(splitPath);
        if (publicUrlList.contains(urlWithoutPathParam)) {
          resp = true;
        }
      }
    }
    return resp;
  }

  /**
   * Returns URL without path and query parameters.
   *
   * @param splitPath URL path split on slash (i.e. /)
   * @return URL without path and query parameters
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
