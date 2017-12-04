/**
 * 
 */
package org.sunbird.common.models.util.azure;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.ListBlobItem;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;

import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

/**
 * @author Manzarul
 *
 */
public class AzureFileUtility {

  private static final String DEFAULT_CONTAINER = "default";
  
  /**
   * This method will upload the file inside Azure storage with provided filename
   * under particular container. if you won't provide the filename then it will
   * pick the file name from file object and upload the file with that name only.
   * @param fileName String
   * @param containerName String
   * @param file File
   * @return boolean
   */
  public static boolean uploadTheFile(String fileName, String containerName,
      File file) {
    if (file == null) {
      ProjectLogger.log("Upload file can not be null");
      return false;
    }
    if (ProjectUtil.isStringNullOREmpty(containerName)) {
      ProjectLogger.log("Container name can't be null or empty");
      return false;
    }
    CloudBlobContainer container =
        AzureConnectionManager.getContainer(containerName,true);
    if (container == null) {
      ProjectLogger.log("Unable to get Azure contains object");
      return false;
    }
    if (ProjectUtil.isStringNullOREmpty(fileName)) {
      fileName = file.getName();
    }
    // Create or overwrite the "myimage.jpg" blob with contents from a local file.
    FileInputStream fis =null;
    try {
      CloudBlockBlob blob = container.getBlockBlobReference(fileName);
      fis = new FileInputStream(file);
      String mimeType = Files.probeContentType(file.toPath());
      ProjectLogger.log("File - "+file.getName()+" mimeType "+mimeType);
      blob.getProperties().setContentType(mimeType);
      blob.upload(fis, file.length());
      return true;
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }finally {
      if(null != fis){
        try {
          fis.close();
        } catch (IOException e) {
          ProjectLogger.log(e.getMessage() , e);
        }
      }
    }
    return false;
  }
  
  /**
   * This method will remove the file from Azure Storage.
   * @param fileName
   * @param containerName
   * @return boolean
   */
  public static boolean deleteFile(String containerName, String fileName )
     {
    if (fileName == null) {
      ProjectLogger.log("File name can not be null");
      return false;
    }
    if (ProjectUtil.isStringNullOREmpty(containerName)) {
      ProjectLogger.log("Container name can't be null or empty");
      return false;
    }
    CloudBlobContainer container =
        AzureConnectionManager.getContainer(containerName , true);
    if (container == null) {
      ProjectLogger.log("Unable to get Azure contains object");
      return false;
    }
    try {
   // Retrieve reference to a blob named "myimage.jpg".
      CloudBlockBlob blob = container.getBlockBlobReference(fileName);
      // Delete the blob.
      boolean response = blob.deleteIfExists();
      if(!response) {
        ProjectLogger.log("Provided file not found to delete.");
      }
      return true;
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    return false;
  }
  
  
  /**
   * This method will remove the container from Azure Storage.
   * @param containerName
   * @return boolean
   */
  public static boolean deleteContainer(String containerName )
     {
    if (ProjectUtil.isStringNullOREmpty(containerName)) {
      ProjectLogger.log("Container name can't be null or empty");
      return false;
    }
    CloudBlobContainer container =
        AzureConnectionManager.getContainer(containerName, true);
    if (container == null) {
      ProjectLogger.log("Unable to get Azure contains object");
      return false;
    }
    try {
      boolean response = container.deleteIfExists();
      if(!response) {
        ProjectLogger.log("Container not found..");
      }else {
        ProjectLogger.log("Container is deleted===");
      }
      return true;
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    return false;
  }



  public static String uploadFile(String containerName , String blobName , String fileName){

    CloudBlobContainer container = AzureConnectionManager.getContainer(containerName,true);
    // Create or overwrite the "myimage.jpg" blob with contents from a local file.
    CloudBlockBlob blob = null;
    String fileUrl = null;
    FileInputStream fis =null;
    try {
      blob = container.getBlockBlobReference(blobName);
      File source = new File(fileName);
      fis = new FileInputStream(source);
      String mimeType = Files.probeContentType(source.toPath());
      ProjectLogger.log("File - "+source.getName()+" mimeType "+mimeType);
      blob.getProperties().setContentType(mimeType);
      blob.upload(fis, source.length());
      //fileUrl = blob.getStorageUri().getPrimaryUri().getPath();
      fileUrl = blob.getUri().toString();
    } catch (URISyntaxException e) {
      ProjectLogger.log("Unable to upload file :"+fileName , e);
    } catch (StorageException e) {
      ProjectLogger.log("Unable to upload file :"+fileName , e);
    } catch (FileNotFoundException e) {
      ProjectLogger.log("Unable to upload file :"+fileName , e);
    } catch (IOException e) {
      ProjectLogger.log("Unable to upload file :"+fileName , e);
    }finally {
      if(null != fis){
        try {
          fis.close();
        } catch (IOException e) {
          ProjectLogger.log(e.getMessage() , e);
        }
      }
    }

    return fileUrl;

  }

  public static String uploadFile(String containerName , File source){

    String containerPath ="";
    String filePath="";

    if(ProjectUtil.isStringNullOREmpty(containerName)){
      containerName = DEFAULT_CONTAINER;
    }else{
      containerName = containerName.toLowerCase();
    }
    if(containerName.startsWith("/")){
      containerName = containerName.substring(1);
    }
    if(containerName.contains("/")){
      String arr[]=containerName.split("/", 2);
      containerPath = arr[0];
      if(arr[1].length()>0 && arr[1].endsWith("/")){
        filePath=arr[1];
      }else if(arr[1].length()>0){
        filePath=arr[1]+"/";
      }
    }else{
      containerPath = containerName;
    }

    CloudBlobContainer container = AzureConnectionManager.getContainer(containerPath,true);
    // Create or overwrite the "myimage.jpg" blob with contents from a local file.
    CloudBlockBlob blob = null;
    String fileUrl = null;
    FileInputStream fis = null;
    try {
      blob = container.getBlockBlobReference(filePath+source.getName());
      //File source = new File(fileName);
      fis = new FileInputStream(source);
      String mimeType = Files.probeContentType(source.toPath());
      ProjectLogger.log("File - "+source.getName()+" mimeType "+mimeType);
      blob.getProperties().setContentType(mimeType);
      blob.upload(fis, source.length());
      //fileUrl = blob.getStorageUri().getPrimaryUri().getPath();
      fileUrl = blob.getUri().toString();
    } catch (URISyntaxException e) {
      ProjectLogger.log("Unable to upload file :"+source.getName() , e);
    } catch (StorageException e) {
      ProjectLogger.log("Unable to upload file :"+source.getName() , e);
    } catch (FileNotFoundException e) {
      ProjectLogger.log("Unable to upload file :"+source.getName() , e);
    } catch (IOException e) {
      ProjectLogger.log("Unable to upload file :"+source.getName() , e);
    }finally {
      if(null != fis){
        try {
          fis.close();
        } catch (IOException e) {
          ProjectLogger.log(e.getMessage() , e);
        }
      }
    }
    return fileUrl;

  }


  public static boolean downloadFile(String containerName , String blobName , String downloadFolder){


    boolean flag = false;
    CloudBlobContainer container = AzureConnectionManager.getContainer(containerName,true);
    // Create or overwrite  blob with contents .
    CloudBlockBlob blob = null;
    FileOutputStream fos = null;

    try {
      blob = container.getBlockBlobReference(blobName);
      if(blob.exists()) {
        if(!(downloadFolder.endsWith(("/")))){
          downloadFolder = downloadFolder+"/";
        }
        File file = new File(downloadFolder + blobName);
        fos = new FileOutputStream(file);
        blob.download(fos);
      }
    } catch (URISyntaxException e) {
      ProjectLogger.log("Unable to upload blobfile :"+blobName , e);
    } catch (StorageException e) {
      ProjectLogger.log("Unable to upload file :"+blobName , e);
    } catch (FileNotFoundException e) {
      ProjectLogger.log("Unable to upload file :"+blobName , e);
    }finally {
      if(null != fos){
        try {
          fos.close();
        } catch (IOException e) {
          ProjectLogger.log(e.getMessage() , e);
        }
      }
    }
    return flag;
  }

  public static List<String> listAllBlobbs(String containerName){

    List<String> blobsList = new ArrayList<>();
    CloudBlobContainer container = AzureConnectionManager.getContainer(containerName,true);
    // Loop over blobs within the container and output the URI to each of them.
    for (ListBlobItem blobItem : container.listBlobs()) {
      blobsList.add(blobItem.getUri().toString());
    }
    return blobsList;
  }
}
