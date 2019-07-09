package org.sunbird.common.models.util;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

public class HttpUtilTest extends BaseHttpTest {
  public static final String JSON_STRING_DATA = "asdasasfasfsdfdsfdsfgsd";

  @Test
  public void testPostFormDataSuccess() {
    Map<String, String> reqData = new HashMap<>();
    reqData.put("field1", "value1");
    reqData.put("field2", "value2");

    Map<String, byte[]> fileData = new HashMap<>();
    fileData.put("file1", ("asd".getBytes()));

    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "123456");
    String url = "http://localhost:8000/v1/issuer/issuers";
    try {
      String response = (String) HttpUtil.postFormData(reqData, fileData, headers, url).getBody();
      assertTrue("{\"message\":\"success\"}".equals(response));
    } catch (IOException e) {
      ProjectLogger.log(e.getMessage());
    }
  }

  @Test
  public void testSendPatchRequestSuccess() {

    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "123456");
    String url = "http://localhost:8000/v1/issuer/issuers";
    try {
      CloseableHttpResponse closeableHttpResponseMock =
          PowerMockito.mock(CloseableHttpResponse.class);
      HttpEntity httpEntity = PowerMockito.mock(HttpEntity.class);
      PowerMockito.when(closeableHttpResponseMock.getStatusLine())
          .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));

      PowerMockito.when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntity);
      closeableHttpResponseMock.setEntity(httpEntity);
      PowerMockito.when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntity);
      PowerMockito.when(closeableHttpResponseMock.getEntity().getContent())
          .thenReturn(new ByteArrayInputStream("{\"message\":\"success\"}".getBytes()));

      CloseableHttpClient closeableHttpClientMocked = PowerMockito.mock(CloseableHttpClient.class);
      PowerMockito.mockStatic(HttpClients.class);
      PowerMockito.when(HttpClients.createDefault()).thenReturn(closeableHttpClientMocked);

      PowerMockito.when(closeableHttpClientMocked.execute(Mockito.any(HttpPost.class)))
          .thenReturn(closeableHttpResponseMock);

      String response = HttpUtil.sendPatchRequest(url, "{\"message\":\"success\"}", headers);
      assertTrue("SUCCESS".equals(response));
    } catch (IOException e) {
      ProjectLogger.log(e.getMessage());
    }
  }

  @Test
  public void testSendPostRequestSuccess() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "123456");
    String url = "http://localhost:8000/v1/issuer/issuers";
    try {
      String response = HttpUtil.sendPostRequest(url, "{\"message\":\"success\"}", headers);
      assertTrue("{\"message\":\"success\"}".equals(response));
    } catch (IOException e) {
      ProjectLogger.log(e.getMessage());
    }
  }

  @Test
  public void testSendGetRequestSuccess() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "123456");
    String urlString = "http://localhost:8000/v1/issuer/issuers";
    try {
      String response = HttpUtil.sendGetRequest(urlString, headers);
      assertTrue("{\"message\":\"success\"}".equals(response));
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage());
    }
  }
}
