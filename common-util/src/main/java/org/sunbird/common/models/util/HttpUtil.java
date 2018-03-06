/**
 * 
 */
package org.sunbird.common.models.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.sunbird.common.request.ExecutionContext;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.telemetry.util.lmaxdisruptor.LMAXWriter;
import org.sunbird.telemetry.util.lmaxdisruptor.TelemetryEvents;

/**
 * This utility method will handle external http call
 * 
 * @author Manzarul
 *
 */
public class HttpUtil {


    private static LMAXWriter lmaxWriter = LMAXWriter.getInstance();

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
        Map<String, Object> logInfo = genarateLogInfo(JsonKey.API_CALL, "API CALL : " + requestURL);
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

        telemetryProcessingCall(logInfo);
        ProjectLogger.log(
                "HttpUtil sendGetRequest method end at ==" + stopTime + " for requestURL "
                        + requestURL + " ,Total time elapsed = " + elapsedTime,
                LoggerEnum.PERF_LOG);
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
        Map<String, Object> logInfo = genarateLogInfo(JsonKey.API_CALL, "API CALL : " + requestURL);
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
                writer = new OutputStreamWriter(httpURLConnection.getOutputStream(),
                        StandardCharsets.UTF_8);
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
        telemetryProcessingCall(logInfo);
        ProjectLogger.log(
                "HttpUtil sendPostRequest method end at ==" + stopTime + " for requestURL "
                        + requestURL + " ,Total time elapsed = " + elapsedTime,
                LoggerEnum.PERF_LOG);
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
        Map<String, Object> logInfo = genarateLogInfo(JsonKey.API_CALL, "API CALL : " + requestURL);
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
            writer = new OutputStreamWriter(httpURLConnection.getOutputStream(),
                    StandardCharsets.UTF_8);
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
        telemetryProcessingCall(logInfo);
        ProjectLogger.log(
                "HttpUtil sendPostRequest method end at ==" + stopTime + " for requestURL "
                        + requestURL + " ,Total time elapsed = " + elapsedTime,
                LoggerEnum.PERF_LOG);
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
        Map<String, Object> logInfo = genarateLogInfo(JsonKey.API_CALL, "API CALL : " + requestURL);
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
                ProjectLogger.log(
                        "HttpUtil sendPatchRequest method end at ==" + stopTime + " for requestURL "
                                + requestURL + " ,Total time elapsed = " + elapsedTime,
                        LoggerEnum.PERF_LOG);
                return ResponseCode.success.getErrorCode();
            }
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            ProjectLogger.log(
                    "HttpUtil sendPatchRequest method end at ==" + stopTime + " for requestURL "
                            + requestURL + " ,Total time elapsed = " + elapsedTime,
                    LoggerEnum.PERF_LOG);
            return "Failure";
        } catch (Exception e) {
            ProjectLogger.log(e.getMessage(), e);
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        telemetryProcessingCall(logInfo);
        ProjectLogger.log(
                "HttpUtil sendPatchRequest method end at ==" + stopTime + " for requestURL "
                        + requestURL + " ,Total time elapsed = " + elapsedTime,
                LoggerEnum.PERF_LOG);
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
    private static void setHeaders(HttpURLConnection httpURLConnection,
            Map<String, String> headers) {
        Iterator<Entry<String, String>> itr = headers.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, String> entry = itr.next();
            httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private static Map<String, Object> genarateLogInfo(String logType, String message) {

        Map<String, Object> info = new HashMap<>();
        info.put(JsonKey.LOG_TYPE, logType);
        long startTime = System.currentTimeMillis();
        info.put(JsonKey.START_TIME, startTime);
        info.put(JsonKey.MESSAGE, message);
        info.put(JsonKey.LOG_LEVEL, JsonKey.INFO);
        return info;
    }

    public static void telemetryProcessingCall(Map<String, Object> request) {

        Map<String, Object> logInfo = request;
        long endTime = System.currentTimeMillis();
        logInfo.put(JsonKey.END_TIME, endTime);
        Request req = new Request();
        req.setRequest(generateTelemetryRequest(TelemetryEvents.LOG.getName(), logInfo));
        lmaxWriter.submitMessage(req);

    }

    private static Map<String, Object> generateTelemetryRequest(String eventType,
            Map<String, Object> params) {

        Map<String, Object> context = new HashMap<>();
        context.putAll(ExecutionContext.getCurrent().getRequestContext());
        context.putAll(ExecutionContext.getCurrent().getGlobalContext());
        Map<String, Object> map = new HashMap<>();
        map.put(JsonKey.TELEMETRY_EVENT_TYPE, eventType);
        map.put(JsonKey.CONTEXT, context);
        map.put(JsonKey.PARAMS, params);
        return map;
    }

    /**
     * @description this metod will post the form data
     * @param reqData (Map<String,String>)
     * @param fileData (Map<fileName,FileObject>)
     * @param headers (Map<fileName,String>)
     * @param url
     * @return String
     * @throws IOException
     */
    public static String postFormData(Map<String, String> reqData, Map<String, byte[]> fileData,
            Map<String, String> headers, String url) throws IOException {
        long startTime = System.currentTimeMillis();
        Map<String, Object> logInfo = genarateLogInfo(JsonKey.API_CALL, "API CALL : " + url);
        ProjectLogger.log(
                "HttpUtil postFormData method started at ==" + startTime + " for requestURL " + url,
                LoggerEnum.PERF_LOG);
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            Set<Entry<String, String>> headerEntry = headers.entrySet();
            for (Entry<String, String> headerObj : headerEntry) {
                httpPost.addHeader(headerObj.getKey(), headerObj.getValue());
            }

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            Set<Entry<String, String>> entry = reqData.entrySet();
            for (Entry<String, String> entryObj : entry) {
                builder.addTextBody(entryObj.getKey(), entryObj.getValue());
            }
            Set<Entry<String, byte[]>> fileEntry = fileData.entrySet();
            for (Entry<String, byte[]> entryObj : fileEntry) {
                if (!ProjectUtil.isStringNullOREmpty(entryObj.getKey())
                        && null != entryObj.getValue()) {
                    builder.addBinaryBody(entryObj.getKey(), entryObj.getValue(),
                            ContentType.APPLICATION_OCTET_STREAM, entryObj.getKey());
                }
            }
            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            ProjectLogger.log("HttpUtil postFormData method end at ==" + stopTime
                    + " for requestURL " + url + " ,Total time elapsed = " + elapsedTime,
                    LoggerEnum.PERF_LOG);
            HttpResponse httpResponse = client.execute(httpPost);
            String res = generateResponse(httpResponse);
            telemetryProcessingCall(logInfo);
            return res;
        } catch (Exception ex) {
            ProjectLogger.log("Exception occurred while calling postFormData method.", ex);
            throw ex;
        } finally {
            client.close();
        }

    }

 

    private static String generateResponse(HttpResponse httpResponse) throws IOException {
        StringBuilder builder1 = new StringBuilder();
        BufferedReader br = new BufferedReader(
            new InputStreamReader((httpResponse.getEntity().getContent())));
        String output;
        while ((output = br.readLine()) != null) {
            builder1.append(output);
        }
        return builder1.toString();
    }

    /**
     *
     * @param headers
     * @param url
     * @return String
     * @throws IOException
     */
    public static String sendDeleteRequest(Map<String, String> headers, String url)
            throws IOException {
        long startTime = System.currentTimeMillis();
        ProjectLogger.log("HttpUtil sendDeleteRequest method started at ==" + startTime
                + " for requestURL " + url, LoggerEnum.PERF_LOG);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpDelete httpDelete = new HttpDelete(url);
            ProjectLogger.log("Executing sendDeleteRequest " + httpDelete.getRequestLine());
            Map<String, Object> logInfo = genarateLogInfo(JsonKey.API_CALL, "API CALL : " + url);
            Set<Entry<String, String>> headerEntry = headers.entrySet();
            for (Entry<String, String> headerObj : headerEntry) {
                httpDelete.addHeader(headerObj.getKey(), headerObj.getValue());
            }
            // Create a custom response handler
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Status: " + status);
                }
            };
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            ProjectLogger.log("HttpUtil sendDeleteRequest method end at ==" + stopTime
                    + " for requestURL " + url + " ,Total time elapsed = " + elapsedTime,
                    LoggerEnum.PERF_LOG);
          String res = httpclient.execute(httpDelete, responseHandler);
          telemetryProcessingCall(logInfo);
          return res;
        } catch (Exception ex) {
            ProjectLogger.log("Exception occurred while calling sendDeleteRequest method.", ex);
            throw ex;

        }
    }
    
    public static void main(String[] args) {
  	 Map<String,String> headermap = new HashMap<>();
  	 headermap.put("Content-Type", "application/json");
  	 headermap.put("Authorization", "Token c6d0bdb8ce2425b26c2840bdca0f7b64e39be5fe");
  	 try {
  		String response = sendPostRequest("http://localhost:8000/v1/issuer/issuers/haque/badges/db-design-expert/assertions?format=json", "{\"recipient_identifier\":\"manzarul.haque@tarento.com\",\"evidence\":\"http://localhost:8000/public/badges/db-design-expert\",\"create_notification\":false}", headermap);
  	   System.out.println(response);
  	 } catch (IOException e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();
  	}
  }
    
}
