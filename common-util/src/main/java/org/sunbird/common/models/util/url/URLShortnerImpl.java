package org.sunbird.common.models.util.url;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;

/**
 * 
 * @author Amit Kumar
 *
 */
public class URLShortnerImpl implements URLShortner {

  private static String resUrl = null;
  private static final String SUNBIRD_WEB_URL = "sunbird_web_url";

  @Override
  public String shortUrl(String url) {
    String baseUrl = PropertiesCache.getInstance().getProperty("sunbird_url_shortner_base_url");
    String accessToken = System.getenv("url_shortner_access_token");
    if (ProjectUtil.isStringNullOREmpty(accessToken)) {
      accessToken = PropertiesCache.getInstance().getProperty("sunbird_url_shortner_access_token");
    }
    String requestURL = baseUrl + accessToken + "&longUrl=" + url;
    String response = "";
    try {
      response = HttpUtil.sendGetRequest(requestURL, null);
    } catch (IOException e) {
      ProjectLogger.log("Exception occurred while sending request for URL shortening", e);
    }
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> map = null;
    if (!ProjectUtil.isStringNullOREmpty(response)) {
      try {
        map = mapper.readValue(response, HashMap.class);
      } catch (IOException e) {
        ProjectLogger.log(e.getMessage(), e);
      }
    }
    Map<String, String> dataMap = (Map<String, String>) map.get("data");
    return dataMap.get("url");
  }

  /**
   * @return the url
   */
  public String getUrl() {
    if (ProjectUtil.isStringNullOREmpty(resUrl)) {
      String webUrl = System.getenv(SUNBIRD_WEB_URL);
      if(ProjectUtil.isStringNullOREmpty(webUrl)){
        webUrl = PropertiesCache.getInstance().getProperty(SUNBIRD_WEB_URL);
      }
      return shortUrl(webUrl);
    } else {
      return resUrl;
    }
  }

  /**
   * @param url the url to set
   */
  public void setUrl(String url) {
    this.resUrl = url;
  }

public static void main(String[] args) {
  
}

}
