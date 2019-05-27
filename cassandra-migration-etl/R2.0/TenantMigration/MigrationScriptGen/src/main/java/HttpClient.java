import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

public class HttpClient {

  private static Logger logger = LoggerFactory.getLoggerInstance(HttpClient.class.getName());

  public String apiKey;
  public String authToken;

  public HttpClient(String authToken, String apiKey) {
    this.authToken = authToken;
    this.apiKey = apiKey;
  }

  public Map<String, Object> post(Map<String, Object> requestBody, String uri) throws IOException {
    logger.debug("HttpClient: post called");
    try {
      CloseableHttpClient client = HttpClients.createDefault();
      ObjectMapper mapper = new ObjectMapper();
      HttpPost httpPost = new HttpPost(uri);
      logger.info("HttpClient:post: uri = " + uri);
      StringEntity entity = new StringEntity(mapper.writeValueAsString(requestBody));
      logger.info("HttpClient:post: request entity = " + entity.getContent());
      httpPost.setEntity(entity);
      httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
      if (StringUtils.isNotBlank(apiKey)) {
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, apiKey);
      }
      if (StringUtils.isNotBlank(authToken)) {
        httpPost.setHeader("x-authenticated-user-token", authToken);
      }
      CloseableHttpResponse response = client.execute(httpPost);
      logger.info("HttpClient:post: statusCode = " + response.getStatusLine().getStatusCode());
      return mapper.readValue(
          response.getEntity().getContent(), new TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      logger.error("HttpClient:post: Exception occurred = " + e);
    }
    return Collections.emptyMap();
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }
}
