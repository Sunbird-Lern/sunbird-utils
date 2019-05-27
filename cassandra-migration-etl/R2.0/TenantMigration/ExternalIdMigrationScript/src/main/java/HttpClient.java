import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpClient {

  private static Logger logger = LoggerFactory.getLoggerInstance(HttpClient.class.getName());

  public String apiKey;
  RequestParms parms;

  public HttpClient(RequestParms parms) {
    this.parms = parms;
  }

  public Map<String, Object> post(Map<String, Object> requestBody, String uri, String authToken)
      throws IOException {
    logger.debug("HttpClient: post called");
    Map<String, Object> responseMap = new HashMap<>();
    try {
      CloseableHttpClient http = HttpClientBuilder.create().build();
      ObjectMapper mapper = new ObjectMapper();
      logger.info("HttpClient:post: uri = " + uri);
      StringEntity entity = new StringEntity(mapper.writeValueAsString(requestBody));
      HttpPatch updateRequest = new HttpPatch(uri);
      updateRequest.setEntity(entity);
      updateRequest.setHeader("Authorization", parms.getApiKey());
      updateRequest.setHeader("x-authenticated-user-token", authToken);
      updateRequest.setHeader("Content-Type", "application/json");
      HttpResponse response = http.execute(updateRequest);

      String json_string = EntityUtils.toString(response.getEntity());
      logger.info("HttpClient:post: statusCode = " + response.getStatusLine().getStatusCode());
      logger.info("Response From userUpdate: " + json_string);
      responseMap.put("statusCode", response.getStatusLine().getStatusCode());
      responseMap.put("errMsg", json_string);
      return responseMap;
    } catch (Exception e) {
      logger.error("HttpClient:post: Exception occurred = " + e);
    }
    return responseMap;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String generateAuthToken(String userName) {
    try {
      if (StringUtils.isBlank(parms.getClientId())) {
        parms.setClientId("admin-cli");
      }
      String url =
          parms
              .getBaseUrl()
              .concat("/auth/realms/")
              .concat(parms.getRealm())
              .concat("/protocol/openid-connect/token");
      // String url="http://localhost:8080/auth/realms/master/protocol/openid-connect/token";
      if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(url)) {
        CloseableHttpClient client = HttpClients.createDefault();
        ObjectMapper mapper = new ObjectMapper();
        HttpPost httpPost = new HttpPost(url);
        logger.info("HttpClient:post: uri = " + url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", userName));
        params.add(new BasicNameValuePair("client_id", parms.getClientId()));
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("password", "password"));
        logger.info("Httpclient: generateAuthToken: " + Arrays.asList(params.toString()));
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = client.execute(httpPost);
        logger.info("HttpClient:post: statusCode = " + response.getStatusLine().getStatusCode());
        Map<String, Object> tokenMap =
            mapper.readValue(
                response.getEntity().getContent(), new TypeReference<Map<String, Object>>() {});
        if (tokenMap != null && tokenMap.size() != 0) {
          return (String) tokenMap.get("access_token");
        }
      }
    } catch (Exception e) {
      logger.error(
          "Error Occured in  getting auth token with username "
              + userName
              + "  and the error occured is "
              + e);
    }
    return "";
  }
}
