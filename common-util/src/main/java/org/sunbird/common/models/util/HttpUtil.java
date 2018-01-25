/**
 * 
 */
package org.sunbird.common.models.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * This utility method will handle external http call
 * 
 * @author Manzarul
 *
 */
public class HttpUtil {

  /**
   * Makes an HTTP request using GET method to the specified URL.
   *
   * @param requestURL the URL of the remote server
   * @param headers the Map <String,String>
   * @return An String object
   * @throws IOException thrown if any I/O error occurred
   */
  public static String sendGetRequest(String requestURL, Map<String, String> headers)
      throws IOException {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("HttpUtil sendGetRequest method started at ==" + startTime
        + " for requestURL " + requestURL, LoggerEnum.PERF_LOG);
    URL url = new URL(requestURL);
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setUseCaches(false);
    httpURLConnection.setDoInput(true);
    httpURLConnection.setDoOutput(false);
    httpURLConnection.setRequestMethod(ProjectUtil.Method.GET.name());
    if (headers != null && headers.size() > 0) {
      setHeaders(httpURLConnection, headers);
    }

    String str = getResponse(httpURLConnection);
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("HttpUtil sendGetRequest method end at ==" + stopTime + " for requestURL "
        + requestURL + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return str;
  }



  /**
   * Makes an HTTP request using POST method to the specified URL.
   *
   * @param requestURL the URL of the remote server
   * @param params A map containing POST data in form of key-value pairs
   * @return An HttpURLConnection object
   * @throws IOException thrown if any I/O error occurred
   */
  public static String sendPostRequest(String requestURL, Map<String, String> params,
      Map<String, String> headers) throws IOException {
    long startTime = System.currentTimeMillis();
    HttpURLConnection httpURLConnection = null;
    OutputStreamWriter writer = null;
    ProjectLogger.log("HttpUtil sendPostRequest method started at ==" + startTime
        + " for requestURL " + requestURL, LoggerEnum.PERF_LOG);
    try {
      URL url = new URL(requestURL);
      httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setDoInput(true);
      httpURLConnection.setRequestMethod(ProjectUtil.Method.POST.name());
      StringBuilder requestParams = new StringBuilder();
      if (params != null && params.size() > 0) {
        httpURLConnection.setDoOutput(true);
        // creates the params string, encode them using URLEncoder
        for (Map.Entry<String, String> entry : params.entrySet()) {
          String key = entry.getKey();
          String value = entry.getValue();
          requestParams.append(URLEncoder.encode(key, "UTF-8"));
          requestParams.append("=").append(URLEncoder.encode(value, "UTF-8"));
          requestParams.append("&");
        }
      }
      if (headers != null && headers.size() > 0) {
        setHeaders(httpURLConnection, headers);
      }
      if (requestParams.length() > 0) {
        writer =
            new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8);
        writer.write(requestParams.toString());
        writer.flush();
      }
    } catch (IOException ex) {
      ProjectLogger.log(ex.getMessage(), ex);
      throw ex;
    } finally {
      if (null != writer) {
        try {
          writer.close();
        } catch (IOException e) {
          ProjectLogger.log(e.getMessage(), e);
        }
      }
    }

    String str = getResponse(httpURLConnection);
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("HttpUtil sendPostRequest method end at ==" + stopTime + " for requestURL "
        + requestURL + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return str;
  }

  /**
   * Makes an HTTP request using POST method to the specified URL.
   *
   * @param requestURL the URL of the remote server
   * @param params A map containing POST data in form of key-value pairs
   * @return An HttpURLConnection object
   * @throws IOException thrown if any I/O error occurred
   */
  public static String sendPostRequest(String requestURL, String params,
      Map<String, String> headers) throws IOException {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("HttpUtil sendPostRequest method started at ==" + startTime
        + " for requestURL " + requestURL, LoggerEnum.PERF_LOG);
    HttpURLConnection httpURLConnection = null;
    OutputStreamWriter writer = null;
    try {
      URL url = new URL(requestURL);
      httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setDoInput(true);
      httpURLConnection.setRequestMethod(ProjectUtil.Method.POST.name());
      httpURLConnection.setDoOutput(true);
      if (headers != null && headers.size() > 0) {
        setHeaders(httpURLConnection, headers);
      }
      writer = new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8);
      writer.write(params);
      writer.flush();
    } catch (IOException e) {
      ProjectLogger.log(e.getMessage(), e);
      throw e;
    } finally {
      if (null != writer) {
        try {
          writer.close();
        } catch (IOException e) {
          ProjectLogger.log(e.getMessage(), e);
        }
      }
    }

    String str = getResponse(httpURLConnection);
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("HttpUtil sendPostRequest method end at ==" + stopTime + " for requestURL "
        + requestURL + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return str;
  }

  private static String getResponse(HttpURLConnection httpURLConnection) throws IOException {
    InputStream inStream = null;
    BufferedReader reader = null;
    StringBuilder builder = new StringBuilder();
    try {
      inStream = httpURLConnection.getInputStream();
      reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
      String line = null;
      while ((line = reader.readLine()) != null) {
        builder.append(line);
      }
    } catch (IOException e) {
      ProjectLogger.log("Error in getResponse HttpUtil:", e);
      throw e;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          ProjectLogger.log("Error while closing the reader:", e);
        }
      }
      if (inStream != null) {
        try {
          inStream.close();
        } catch (IOException e) {
          ProjectLogger.log("Error while closing the stream:", e);
        }
      }
      if (httpURLConnection != null) {
        httpURLConnection.disconnect();
      }
    }
    return builder.toString();
  }


  /**
   * Makes an HTTP request using PATCH method to the specified URL.
   *
   * @param requestURL the URL of the remote server
   * @param params A map containing POST data in form of key-value pairs
   * @return An HttpURLConnection object
   * @throws IOException thrown if any I/O error occurred
   */
  public static String sendPatchRequest(String requestURL, String params,
      Map<String, String> headers) throws IOException {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("HttpUtil sendPatchRequest method started at ==" + startTime
        + " for requestURL " + requestURL, LoggerEnum.PERF_LOG);

    HttpPatch patch = new HttpPatch(requestURL);
    setHeaders(patch, headers);
    CloseableHttpClient httpClient = HttpClients.createDefault();
    StringEntity entity = new StringEntity(params);
    patch.setEntity(entity);
    try {
      CloseableHttpResponse response = httpClient.execute(patch);
      ProjectLogger.log("response code for Patch Resques");
      if (response.getStatusLine().getStatusCode() == ResponseCode.OK.getResponseCode()) {
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        ProjectLogger.log("HttpUtil sendPatchRequest method end at ==" + stopTime
            + " for requestURL " + requestURL + " ,Total time elapsed = " + elapsedTime,
            LoggerEnum.PERF_LOG);
        return ResponseCode.success.getErrorCode();
      }
      long stopTime = System.currentTimeMillis();
      long elapsedTime = stopTime - startTime;
      ProjectLogger.log("HttpUtil sendPatchRequest method end at ==" + stopTime + " for requestURL "
          + requestURL + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
      return "Failure";
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("HttpUtil sendPatchRequest method end at ==" + stopTime + " for requestURL "
        + requestURL + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return "Failure";
  }


  /**
   * Set the header for request.
   * 
   * @param httpPatch HttpURLConnection
   * @param headers Map<String,String>
   */
  private static void setHeaders(HttpPatch httpPatch, Map<String, String> headers) {
    Iterator<Entry<String, String>> itr = headers.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<String, String> entry = itr.next();
      httpPatch.setHeader(entry.getKey(), entry.getValue());
    }
  }


  /**
   * Set the header for request.
   * 
   * @param httpURLConnection HttpURLConnection
   * @param headers Map<String,String>
   */
  private static void setHeaders(HttpURLConnection httpURLConnection, Map<String, String> headers) {
    Iterator<Entry<String, String>> itr = headers.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<String, String> entry = itr.next();
      httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
    }
  }
}
