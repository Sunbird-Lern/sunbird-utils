package org.sunbird.cloud;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.sunbird.cloud.storage.BaseStorageService;
import org.sunbird.util.HttpClientUtil;
import org.sunbird.cloud.storage.factory.StorageConfig;
import org.sunbird.cloud.storage.factory.StorageServiceFactory;
import scala.Option;
import scala.Some;

public class FileUploader {
   public static void main(String[] args) {
      String domain = args[0];
      String offset = args[1];
      String limit = args[2];
      String accountName = args[3];
      String accountKey = args[4];
      String svgLocation = args[5];
      String cloudStorageType = "azure";
      String endpoint = "";
      String region = "";
      if (args.length > 6) {
         cloudStorageType = args[6];
      }
      if (args.length > 7) {
         endpoint = args[7];
      }
      if (args.length > 8) {
         region = args[8];
      }

      List<File> fileList = listAllFiles(svgLocation);
      Map<String, File> doidFileMap = new HashMap();
      Iterator var10 = fileList.iterator();
      BaseStorageService storeService = StorageServiceFactory.getStorageService(new StorageConfig(cloudStorageType, accountName, accountKey, new Some<String>(endpoint), new Some<String>(region)));

      String url;
      while(var10.hasNext()) {
         File file = (File)var10.next();
         System.out.println(file.getAbsolutePath());
         String filePath = file.getAbsolutePath();
         String[] strArray = filePath.split("/");
         url = strArray[strArray.length - 2];
         doidFileMap.put(url, file);
      }

      List<Map<String, String>> doidUrlMapList = getContentSearchResponse(domain, offset, limit);
      Map<String, File> urlFileMap = new HashMap();
      Iterator var22 = doidUrlMapList.iterator();

      while(var22.hasNext()) {
         Map<String, String> doidUrlMap = (Map)var22.next();
         File file = (File)doidFileMap.get(doidUrlMap.get("identifier"));
         if (file != null) {
            urlFileMap.put((String)doidUrlMap.get("artifactUrl"), file);
         }
      }

      var22 = urlFileMap.entrySet().iterator();

      while(var22.hasNext()) {
         Entry<String, File> urlFileEntry = (Entry)var22.next();
         url = (String)urlFileEntry.getKey();
         File file = (File)urlFileEntry.getValue();
         String[] container = url.split("/");
         StringBuilder containerPath = new StringBuilder();

         containerPath.append(container[4]);
         for(int i = 5; i < container.length - 1; ++i) {
            containerPath.append("/").append(container[i]);
         }
         containerPath.append("/");
         String uploadedUrl = storeService.upload(container[3], file.getAbsolutePath(), containerPath + file.getName(), Option.apply(false), Option.apply(1), Option.apply(5), Option.empty());
         System.out.println(uploadedUrl);
      }
      System.out.println("Execution finished");
      System.exit(0);
   }

   public static List<Map<String, String>> getContentSearchResponse(String domain, String offset, String limit) {
      ArrayList doidUrlMapList = new ArrayList();

      try {
         String uri = "https://" + domain + "/api/content/v1/search";
         Map<String, Object> req = new HashMap();
         Map<String, Object> request = new HashMap();
         Map<String, Object> filters = new HashMap();
         List<String> certTypes = new ArrayList();
         certTypes.add("cert template layout");
         certTypes.add("cert template");
         filters.put("certType", certTypes);
         filters.put("mediaType", "image");
         request.put("filters", filters);
         String[] fields = new String[]{"artifactUrl", "identifier"};
         request.put("fields", fields);
         request.put("offset", Integer.parseInt(offset));
         request.put("limit", Integer.parseInt(limit));
         req.put("request", request);
         Map<String, String> headers = new HashMap();
         headers.put("Content-Type", "application/json");
         headers.put("Accept", "application/json");
         Map<String, Object> response = post(req, headers, uri);
         System.out.println("Response : " + response);
         if (MapUtils.isNotEmpty(response)) {
            Map<String, Object> result = (Map)response.get("result");
            if (MapUtils.isNotEmpty(result)) {
               int count = (Integer)result.get("count");
               List<Map<String, String>> list = (List)result.get("content");
               if (count > 0 && CollectionUtils.isNotEmpty(list)) {
                  Iterator var16 = list.iterator();

                  while(var16.hasNext()) {
                     Map<String, String> map = (Map)var16.next();
                     String url = (String)map.get("artifactUrl");
                     String identifier = (String)map.get("identifier");
                     Map<String, String> doidUrlMap = new HashMap();
                     doidUrlMap.put("identifier", identifier);
                     doidUrlMap.put("artifactUrl", url);
                     doidUrlMapList.add(doidUrlMap);
                  }
               }
            }
         }
      } catch (Exception var20) {
         System.out.println("Exception while writing file");
         var20.printStackTrace();
      }

      return doidUrlMapList;
   }

   public static Map<String, Object> post(Map<String, Object> requestBody, Map<String, String> headers, String uri) {
      try {
         ObjectMapper mapper = new ObjectMapper();
         HttpClientUtil client = HttpClientUtil.getInstance();
         String reqBody = mapper.writeValueAsString(requestBody);
         System.out.println("Composite search api called.");
         String response = client.post(uri, reqBody, headers);
         System.out.println("Composite search api response." + response);
         return (Map)mapper.readValue(response, new TypeReference<Map<String, Object>>() {
         });
      } catch (Exception var7) {
         System.out.println("Composite search api call: Exception occurred = ");
         var7.printStackTrace();
         return new HashMap();
      }
   }

   public static List<File> listAllFiles(String directoryName) {
      File directory = new File(directoryName);
      List<File> resultList = new ArrayList();
      File[] fList = directory.listFiles();
      File[] var7 = fList;
      int var6 = fList.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         File file = var7[var5];
         if (file.isFile()) {
            resultList.add(file);
         } else if (file.isDirectory()) {
            resultList.addAll(listAllFiles(file.getAbsolutePath()));
         }
      }

      return resultList;
   }
}