package org.sunbird.cloud;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Locale;
import org.apache.tika.Tika;

public class AzureConnectionManager {
   private static String storageAccountString;
   private static CloudBlobClient blobClient;

   public AzureConnectionManager(String accountName, String accountKey) {
      storageAccountString = "DefaultEndpointsProtocol=https;AccountName=" + accountName + ";AccountKey=" + accountKey + ";EndpointSuffix=core.windows.net";
   }

   public CloudBlobContainer getContainer(String containerName, boolean isPublicAccess) {
      try {
         CloudBlobClient cloudBlobClient;
         if (null == blobClient) {
            cloudBlobClient = this.getBlobClient();
         } else {
            cloudBlobClient = blobClient;
         }

         CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName.toLowerCase(Locale.ENGLISH));
         boolean response = container.createIfNotExists();
         System.out.println("container creation done if not exist==" + response);
         if (isPublicAccess) {
            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
            containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
            container.uploadPermissions(containerPermissions);
         }

         return container;
      } catch (Exception var7) {
         var7.printStackTrace();
         System.out.println("Exception occurred while fetching container" + var7.getMessage());
         return null;
      }
   }

   private CloudBlobClient getBlobClient() {
      CloudBlobClient cblobClient = null;

      try {
         CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageAccountString);
         cblobClient = storageAccount.createCloudBlobClient();
      } catch (URISyntaxException var4) {
         var4.printStackTrace();
         System.out.println("CloudBlobClient:getBlobClient" + var4.getMessage());
      } catch (InvalidKeyException var5) {
         var5.printStackTrace();
         System.out.println("CloudBlobClient:getBlobClient" + var5.getMessage());
      }

      return cblobClient;
   }

   public String uploadFile(String containerName, File source) {
      String containerPath = "";
      String filePath = "";
      Tika tika = new Tika();
      if (containerName.startsWith("/")) {
         containerName = containerName.substring(1);
      }

      String[] str = containerName.split("/");
      containerPath = str[0] + "/";
      filePath = containerName.replace(containerPath, "");
      CloudBlobContainer container = this.getContainer(containerPath, true);
      CloudBlockBlob blob = null;
      String fileUrl = null;
      FileInputStream fis = null;

      try {
         blob = container.getBlockBlobReference(filePath + source.getName());
         fis = new FileInputStream(source);
         String mimeType = tika.detect(source);
         PrintStream var10000 = System.out;
         String var10001 = source.getName();
         var10000.println("File - " + var10001 + " mimeType " + mimeType);
         blob.getProperties().setContentType(mimeType);
         blob.upload(fis, source.length());
         fileUrl = blob.getUri().toString();
         System.out.println("Uploaded file URL::  " + fileUrl);
      } catch (IOException | URISyntaxException var22) {
         var22.printStackTrace();
         System.out.println("Unable to upload file :" + source.getName());
      } catch (Exception var23) {
         System.out.println(var23.getMessage());
      } finally {
         if (null != fis) {
            try {
               fis.close();
            } catch (IOException var21) {
               var21.printStackTrace();
               System.out.println(var21.getMessage());
            }
         }

      }

      return fileUrl;
   }
}