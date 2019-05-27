import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchUtil {

  static HttpClient client;
  static RequestParams params;

  public SearchUtil(RequestParams params, HttpClient client) {
    this.params = params;
    this.client = client;
  }

  public Map<String, Object> userSearch(String userName) throws IOException {

    try {
      Map<String, Object> userRequest = new HashMap<String, Object>();
      Map<String, Object> request = new HashMap<String, Object>();
      Map<String, String> filters = new HashMap<String, String>();
      filters.put("userName", userName);
      //        filters.put("channel",params.getChannel());
      request.put("filters", filters);
      userRequest.put("request", request);
      String searchUrl = params.getBaseUrl() + "/api/user/v1/search";
      Map<String, Object> resMap = client.post(userRequest, searchUrl);
      Map<String, Object> result = null;
      Map<String, Object> responseMap = null;
      List<Map<String, Object>> content = null;
      if (null != resMap) {
        result = (Map<String, Object>) resMap.get("result");
      }
      if (null != result) {
        responseMap = (Map<String, Object>) result.get("response");
      }
      if (null != responseMap) {
        content = (List<Map<String, Object>>) (responseMap).get("content");
      }
      if (null != content && content.size() != 0) {
        return content.get(0);
      } else {
        Collections.emptyMap();
      }
      return Collections.emptyMap();
    } catch (Exception e) {
      System.out.println("Exception occurred in userSearch " + e);
    }
    return Collections.emptyMap();
  }

  public Map<String, Object> orgSearch(String orgExtId, String channel) throws IOException {

    try {
      Map<String, Object> userRequest = new HashMap<String, Object>();
      Map<String, Object> request = new HashMap<String, Object>();
      Map<String, String> filters = new HashMap<String, String>();
      filters.put("channel", channel);
      filters.put("externalId", orgExtId);
      request.put("filters", filters);
      userRequest.put("request", request);
      String searchUrl = params.getBaseUrl() + "/api/org/v1/search";
      Map<String, Object> resMap = client.post(userRequest, searchUrl);
      Map<String, Object> result = null;
      Map<String, Object> responseMap = null;
      List<Map<String, Object>> content = null;
      if (null != resMap) {
        result = (Map<String, Object>) resMap.get("result");
      }
      if (null != result) {
        responseMap = (Map<String, Object>) result.get("response");
      }
      if (null != responseMap && responseMap.size() != 0 && responseMap.get("content") != null) {
        content = (List<Map<String, Object>>) (responseMap).get("content");
      }
      if (null != content && content.size() != 0) {
        return content.get(0);
      } else {
        Collections.emptyMap();
      }
      return Collections.emptyMap();
    } catch (Exception e) {
      System.out.println("SearchUtil: orgSearch: Exception occurred in orgSearch " + e);
    }
    return Collections.emptyMap();
  }

  //

  public Map<String, Object> validateRootOrgId(String rootOrgId, String channel)
      throws IOException {

    try {
      Map<String, Object> userRequest = new HashMap<String, Object>();
      Map<String, Object> request = new HashMap<String, Object>();
      Map<String, Object> filters = new HashMap<String, Object>();
      filters.put("id", rootOrgId);
      filters.put("channel", channel);
      filters.put("status", "1");
      filters.put("isRootOrg", true);
      request.put("filters", filters);
      userRequest.put("request", request);
      String searchUrl = params.getBaseUrl() + "/api/org/v1/search";
      Map<String, Object> resMap = client.post(userRequest, searchUrl);
      Map<String, Object> result = null;
      Map<String, Object> responseMap = null;
      List<Map<String, Object>> content = null;
      if (null != resMap) {
        result = (Map<String, Object>) resMap.get("result");
      }
      if (null != result) {
        responseMap = (Map<String, Object>) result.get("response");
      }
      if (null != responseMap && responseMap.size() != 0 && responseMap.get("content") != null) {
        content = (List<Map<String, Object>>) (responseMap).get("content");
      }
      if (null != content && content.size() != 0) {
        return content.get(0);
      } else {
        Collections.emptyMap();
      }
      return Collections.emptyMap();
    } catch (Exception e) {
      System.out.println("SearchUtil: orgSearch: Exception occurred in orgSearch " + e);
    }
    return Collections.emptyMap();
  }
}
