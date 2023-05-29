package org.sunbird.util;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
   private static CloseableHttpClient httpclient = null;
   private static HttpClientUtil httpClientUtil;

   private HttpClientUtil() {
      ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> {
         BasicHeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));

         String param;
         String value;
         do {
            if (!it.hasNext()) {
               return 180000L;
            }

            HeaderElement he = it.nextElement();
            param = he.getName();
            value = he.getValue();
         } while(value == null || !param.equalsIgnoreCase("timeout"));

         return Long.parseLong(value) * 1000L;
      };
      PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
      connectionManager.setMaxTotal(200);
      connectionManager.setDefaultMaxPerRoute(150);
      connectionManager.closeIdleConnections(180L, TimeUnit.SECONDS);
      httpclient = HttpClients.custom().setConnectionManager(connectionManager).useSystemProperties().setKeepAliveStrategy(keepAliveStrategy).build();
   }

   public static HttpClientUtil getInstance() {
      if (httpClientUtil == null) {
         Class var0 = HttpClientUtil.class;
         synchronized(HttpClientUtil.class) {
            if (httpClientUtil == null) {
               httpClientUtil = new HttpClientUtil();
            }
         }
      }

      return httpClientUtil;
   }

   public String post(String requestURL, String params, Map<String, String> headers) {
      CloseableHttpResponse response = null;

      String var6;
      try {
         HttpPost httpPost = new HttpPost(requestURL);
         if (MapUtils.isNotEmpty(headers)) {
            Iterator var24 = headers.entrySet().iterator();

            while(var24.hasNext()) {
               Entry<String, String> entry = (Entry)var24.next();
               httpPost.addHeader((String)entry.getKey(), (String)entry.getValue());
            }
         }

         StringEntity entity = new StringEntity(params);
         httpPost.setEntity(entity);
         response = httpclient.execute(httpPost);
         int status = response.getStatusLine().getStatusCode();
         if (status >= 200 && status < 300) {
            HttpEntity httpEntity = response.getEntity();
            byte[] bytes = EntityUtils.toByteArray(httpEntity);
            StatusLine sl = response.getStatusLine();
            PrintStream var10000 = System.out;
            int var10001 = sl.getStatusCode();
            var10000.println("Response from post call : " + var10001 + " - " + sl.getReasonPhrase());
            String var11 = new String(bytes);
            return var11;
         }

         String var8 = "";
         return var8;
      } catch (Exception var22) {
         System.out.println("Exception occurred while calling post method");
         var22.printStackTrace();
         var6 = "";
      } finally {
         if (null != response) {
            try {
               response.close();
            } catch (Exception var21) {
               System.out.println("Exception occurred while closing post response object");
            }
         }

      }

      return var6;
   }
}