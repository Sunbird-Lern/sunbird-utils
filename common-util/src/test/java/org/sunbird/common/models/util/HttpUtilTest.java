package org.sunbird.common.models.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpClients.class)
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*", "javax.security.*"})
public class HttpUtilTest {
    public static final String JSON_STRING_DATA = "asdasasfasfsdfdsfdsfgsd";

    @Test
    public void testpostFormData() {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("field1", "value1");
        reqData.put("field2", "value2");

        Map<String, byte[]> fileData = new HashMap<>();
        fileData.put("file1", ("asd".getBytes()));

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "123456");
        String url = "http://localhost:8000/v1/issuer/issuers";
        try {
            CloseableHttpResponse closeableHttpResponseMock =
                    PowerMockito.mock(CloseableHttpResponse.class);

            HttpEntity httpEntity = PowerMockito.mock(HttpEntity.class);
            closeableHttpResponseMock.setEntity(httpEntity);
            PowerMockito.when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntity);
            PowerMockito.when(closeableHttpResponseMock.getEntity().getContent())
                    .thenReturn(new ByteArrayInputStream("{\"message\":\"success\"}".getBytes()));

            CloseableHttpClient closeableHttpClientMocked =
                    PowerMockito.mock(CloseableHttpClient.class);
            PowerMockito.mockStatic(HttpClients.class);
            PowerMockito.when(HttpClients.createDefault()).thenReturn(closeableHttpClientMocked);

            PowerMockito.when(closeableHttpClientMocked.execute(Mockito.any(HttpPost.class)))
                    .thenReturn(closeableHttpResponseMock);
            /*
             * String response2 = (String) HttpUtil.postFormData(reqData, fileData, headers, url)
             * .get(JsonKey.BODY);
             */
            // assertTrue(response2.equals("{\"message\":\"success\"}"));
        } catch (IOException e) {
            ProjectLogger.log(e.getMessage());
        }
    }

    @Test
    public void testSendPatchRequest() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "123456");
        String url = "http://localhost:8000/v1/issuer/issuers";
        try {
            CloseableHttpResponse closeableHttpResponseMock =
                    PowerMockito.mock(CloseableHttpResponse.class);
            HttpEntity httpEntity = PowerMockito.mock(HttpEntity.class);
            PowerMockito.when(closeableHttpResponseMock.getStatusLine()).thenReturn(
                    new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));

            PowerMockito.when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntity);
            closeableHttpResponseMock.setEntity(httpEntity);
            PowerMockito.when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntity);
            PowerMockito.when(closeableHttpResponseMock.getEntity().getContent())
                    .thenReturn(new ByteArrayInputStream("{\"message\":\"success\"}".getBytes()));

            CloseableHttpClient closeableHttpClientMocked =
                    PowerMockito.mock(CloseableHttpClient.class);
            PowerMockito.mockStatic(HttpClients.class);
            PowerMockito.when(HttpClients.createDefault()).thenReturn(closeableHttpClientMocked);

            PowerMockito.when(closeableHttpClientMocked.execute(Mockito.any(HttpPost.class)))
                    .thenReturn(closeableHttpResponseMock);
            String response2 = HttpUtil.sendPatchRequest(url, "{\"message\":\"success\"}", headers);
            assertTrue(response2.equals("SUCCESS"));
        } catch (IOException e) {
            ProjectLogger.log(e.getMessage());
        }
    }

    @Test
    public void testSendPostRequest() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "123456");
        String url = "http://localhost:8000/v1/issuer/issuers";
        try {
            CloseableHttpResponse closeableHttpResponseMock =
                    PowerMockito.mock(CloseableHttpResponse.class);
            HttpEntity httpEntity = PowerMockito.mock(HttpEntity.class);
            PowerMockito.when(closeableHttpResponseMock.getStatusLine()).thenReturn(
                    new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));

            PowerMockito.when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntity);
            closeableHttpResponseMock.setEntity(httpEntity);
            PowerMockito.when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntity);
            PowerMockito.when(closeableHttpResponseMock.getEntity().getContent())
                    .thenReturn(new ByteArrayInputStream("{\"message\":\"success\"}".getBytes()));

            CloseableHttpClient closeableHttpClientMocked =
                    PowerMockito.mock(CloseableHttpClient.class);
            PowerMockito.mockStatic(HttpClients.class);
            PowerMockito.when(HttpClients.createDefault()).thenReturn(closeableHttpClientMocked);

            PowerMockito.when(closeableHttpClientMocked.execute(Mockito.any(HttpPost.class)))
                    .thenReturn(closeableHttpResponseMock);
            String response2 = HttpUtil.sendPostRequest(url, "{\"message\":\"success\"}", headers);
            assertTrue(response2.equals("{\"message\":\"success\"}"));
        } catch (IOException e) {
            ProjectLogger.log(e.getMessage());
        }
    }

    @Test
    public void testSendPostRequest2() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "123456");
        String url = "http://localhost:8000/v1/issuer/issuers";

        Map<String, String> reqData = new HashMap<>();
        reqData.put("field1", "value1");
        reqData.put("field2", "value2");

        try {
            CloseableHttpResponse closeableHttpResponseMock =
                    PowerMockito.mock(CloseableHttpResponse.class);
            HttpEntity httpEntity = PowerMockito.mock(HttpEntity.class);
            PowerMockito.when(closeableHttpResponseMock.getStatusLine()).thenReturn(
                    new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));

            PowerMockito.when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntity);
            closeableHttpResponseMock.setEntity(httpEntity);
            PowerMockito.when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntity);
            PowerMockito.when(closeableHttpResponseMock.getEntity().getContent())
                    .thenReturn(new ByteArrayInputStream("{\"message\":\"success\"}".getBytes()));

            CloseableHttpClient closeableHttpClientMocked =
                    PowerMockito.mock(CloseableHttpClient.class);
            PowerMockito.mockStatic(HttpClients.class);
            PowerMockito.when(HttpClients.createDefault()).thenReturn(closeableHttpClientMocked);

            PowerMockito.when(closeableHttpClientMocked.execute(Mockito.any(HttpPost.class)))
                    .thenReturn(closeableHttpResponseMock);
            String response2 = HttpUtil.sendPostRequest(url, reqData, headers);
            assertTrue(response2.equals("{\"message\":\"success\"}"));
        } catch (IOException e) {
            ProjectLogger.log(e.getMessage());
        }
    }

    @Test
    public void testSendGetRequest() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "123456");
        String urlString = "http://localhost:8000/v1/issuer/issuers";
        try {
            URL url = PowerMockito.mock(URL.class);
            PowerMockito.whenNew(URL.class).withArguments(Mockito.anyString()).thenReturn(url);

            HttpURLConnection connection = PowerMockito.mock(HttpURLConnection.class);

            PowerMockito.when(url.openConnection(Proxy.NO_PROXY)).thenReturn(connection);

            ByteArrayOutputStream postBody = new ByteArrayOutputStream();
            PowerMockito.when(connection.getOutputStream()).thenReturn(postBody);

            ByteArrayInputStream contents =
                    new ByteArrayInputStream("{\"message\":\"success\"}".getBytes());
            PowerMockito.when(connection.getInputStream()).thenReturn(contents);
            PowerMockito.when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
            String response2 = HttpUtil.sendGetRequest(urlString, headers);
            assertTrue(response2.equals("{\"message\":\"success\"}"));
        } catch (Exception e) {
            ProjectLogger.log(e.getMessage());
        }
    }

    @Test
    public void testGetContent() throws IOException {
        String actual = "{\"message\":\"success\"}";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "123456");
        String urlString = "http://localhost:8000/v1/issuer/issuers";
        final HttpURLConnection mockCon = PowerMockito.mock(HttpURLConnection.class);
        InputStream inputStrm = IOUtils.toInputStream(actual);
        PowerMockito.when(mockCon.getLastModified()).thenReturn((Long) 10L, (Long) 11L);
        PowerMockito.when(mockCon.getInputStream()).thenReturn(inputStrm);
        ByteArrayOutputStream postBody = new ByteArrayOutputStream();
        PowerMockito.when(mockCon.getOutputStream()).thenReturn(postBody);

        ByteArrayInputStream contents =
                new ByteArrayInputStream("{\"message\":\"success\"}".getBytes());
        PowerMockito.when(mockCon.getInputStream()).thenReturn(contents);
        PowerMockito.when(mockCon.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        // mocking httpconnection by URLStreamHandler since we can not mock URL class.
        URLStreamHandler stubURLStreamHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return mockCon;
            }
        };

        URL url = new URL(null, urlString, stubURLStreamHandler);
        String response2 = HttpUtil.sendGetRequest(urlString, headers);
        assertEquals(response2, actual);

    }
}
