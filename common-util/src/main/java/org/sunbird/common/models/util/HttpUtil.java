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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * This utility method will handle external 
 * http call 
 * @author Manzarul
 *
 */
public class HttpUtil {
  
	/**
     * Makes an HTTP request using GET method to the specified URL.
     *
     * @param requestURL
     *            the URL of the remote server
     * @param headers 
     *              the Map <String,String>           
     * @return An String object
     * @throws IOException
     *             thrown if any I/O error occurred
     */
    public static String sendGetRequest(String requestURL,Map<String,String> headers)
			throws IOException {
		URL url = new URL(requestURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setDoOutput(false);
		httpURLConnection.setRequestMethod(ProjectUtil.Method.GET.name());
		if (headers != null && headers.size() > 0) {
			setHeaders(httpURLConnection, headers);
		}
		return getResponse(httpURLConnection);
	}
     
    
    
    
    /**
     * Makes an HTTP request using POST method to the specified URL.
     *
     * @param requestURL
     *            the URL of the remote server
     * @param params
     *            A map containing POST data in form of key-value pairs
     * @return An HttpURLConnection object
     * @throws IOException
     *             thrown if any I/O error occurred
     */
    public static String sendPostRequest(String requestURL,
			Map<String, String> params,Map<String,String> headers) throws IOException {
		URL url = new URL(requestURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod(ProjectUtil.Method.POST.name());
		StringBuffer requestParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			httpURLConnection.setDoOutput(true); 
			// creates the params string, encode them using URLEncoder
			Iterator<String> paramIterator = params.keySet().iterator();
			while (paramIterator.hasNext()) {
				String key = paramIterator.next();
				String value = params.get(key);
				requestParams.append(URLEncoder.encode(key, "UTF-8"));
				requestParams.append("=").append(URLEncoder.encode(value, "UTF-8"));
				requestParams.append("&");
			}
		}
		 if(headers !=null && headers.size()>0) {
			 setHeaders(httpURLConnection, headers);
		 }
		 if(requestParams.length()>0) {
		 OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
		 writer.write(requestParams.toString());
		 writer.flush();
		 }
		return getResponse(httpURLConnection);
	}
    
    /**
     * Makes an HTTP request using POST method to the specified URL.
     *
     * @param requestURL
     *            the URL of the remote server
     * @param params
     *            A map containing POST data in form of key-value pairs
     * @return An HttpURLConnection object
     * @throws IOException
     *             thrown if any I/O error occurred
     */
    public static String sendPostRequest(String requestURL,
			String params,Map<String,String> headers) throws IOException {
		URL url = new URL(requestURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod(ProjectUtil.Method.POST.name());
			httpURLConnection.setDoOutput(true); 
		 if(headers !=null && headers.size()>0) {
			 setHeaders(httpURLConnection, headers);
		 }
		 OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
		 writer.write(params);
		 writer.flush();
		return getResponse(httpURLConnection);
	}
    
	private static String getResponse(HttpURLConnection httpURLConnection) {
		InputStream inStream = null;
		BufferedReader reader = null;
		StringBuilder builder = new StringBuilder();
		try {
			inStream = httpURLConnection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inStream));
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (IOException e) {
			ProjectLogger.log("Error in getResponse HttpUtil:",e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				  ProjectLogger.log("Error while closing the reader:",e);
				}
			}
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
				  ProjectLogger.log("Error while closing the stream:",e);
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
     * @param requestURL
     *            the URL of the remote server
     * @param params
     *            A map containing POST data in form of key-value pairs
     * @return An HttpURLConnection object
     * @throws IOException
     *             thrown if any I/O error occurred
     */
    public static String sendPatchRequest(String requestURL,
      String params, Map<String, String> headers)
      throws IOException {
    HttpPatch patch = new HttpPatch(requestURL);
    setHeaders(patch, headers);
    CloseableHttpClient httpClient = HttpClients.createDefault();
    StringEntity entity = new StringEntity(params);
    patch.setEntity(entity);
    try {
      CloseableHttpResponse response = httpClient.execute(patch);
      ProjectLogger.log("response code for Patch Resques");
      if (response.getStatusLine().getStatusCode() == ResponseCode.OK
          .getResponseCode()) {
        return ResponseCode.success.getErrorCode();
      }
      return "Failure";
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    return "Failure";
  }
	
	
	/**
     * Set the header for request.
     * @param httpURLConnection  HttpURLConnection
     * @param headers Map<String,String>
     */
  private static void setHeaders(HttpPatch httpPatch,
      Map<String, String> headers) {
    Iterator<Entry<String, String>> itr = headers.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<String, String> entry = itr.next();
      httpPatch.setHeader(entry.getKey(), entry.getValue());
    }
  }
    
    
    /**
     * Set the header for request.
     * @param httpURLConnection  HttpURLConnection
     * @param headers Map<String,String>
     */
    private static void setHeaders( HttpURLConnection httpURLConnection ,Map<String,String> headers ) {
         Iterator<Entry<String,String>> itr = headers.entrySet().iterator();
         while ( itr.hasNext() ) {
             Entry<String,String> entry = itr.next();
             httpURLConnection.setRequestProperty(entry.getKey(),entry.getValue());
         }
    } 
    
    
    
    public static void main(String[] args) {
      Map<String,String> headermap = new HashMap<String, String>();
      headermap.put("Content-Type", "application/json");
      headermap.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2MzExMTYwNTMzOGY0Zjc5YTgwZTM3YjcyZjVjMmUwZiJ9.azmj_AHmndeJz0h6yIkOJz1XjeZR6Gzd-OrZzR66I0A");
      try {
       String response = sendPatchRequest("https://qa.ekstep.in/api/system/v3/content/update/LP_FT_25", "{\"request\": {\"content\": {\"name\": \"Testing Name\"}}}", headermap);
       System.out.println(response);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
}
