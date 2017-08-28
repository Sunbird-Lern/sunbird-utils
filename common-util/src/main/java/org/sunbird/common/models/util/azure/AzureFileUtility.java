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
    try {
      CloudBlockBlob blob = container.getBlockBlobReference(fileName);
      blob.upload(new FileInputStream(file), file.length());
      return true;
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
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

    boolean flag = false;
    CloudBlobContainer container = AzureConnectionManager.getContainer(containerName,true);
    // Create or overwrite the "myimage.jpg" blob with contents from a local file.
    CloudBlockBlob blob = null;
    String fileUrl = null;
    try {
      blob = container.getBlockBlobReference(blobName);
      File source = new File(fileName);
      flag =true;
      blob.upload(new FileInputStream(source), source.length());
      //fileUrl = blob.getStorageUri().getPrimaryUri().getPath();
      fileUrl = blob.getUri().toString();
    } catch (URISyntaxException e) {
      ProjectLogger.log("Unable to upload file :"+fileName , e);
      e.printStackTrace();
    } catch (StorageException e) {
      ProjectLogger.log("Unable to upload file :"+fileName , e);
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      ProjectLogger.log("Unable to upload file :"+fileName , e);
      e.printStackTrace();
    } catch (IOException e) {
      ProjectLogger.log("Unable to upload file :"+fileName , e);
      e.printStackTrace();
    }

    return fileUrl;

  }

  public static String uploadFile(String containerName , File source){

    String containerPath ="";
    String filePath="";

    //processContainer(containerName , containerPath , filePath);
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
    try {
      blob = container.getBlockBlobReference(filePath+source.getName());
      //File source = new File(fileName);
      blob.upload(new FileInputStream(source), source.length());
      //fileUrl = blob.getStorageUri().getPrimaryUri().getPath();
      fileUrl = blob.getUri().toString();
    } catch (URISyntaxException e) {
      ProjectLogger.log("Unable to upload file :"+source.getName() , e);
      e.printStackTrace();
    } catch (StorageException e) {
      ProjectLogger.log("Unable to upload file :"+source.getName() , e);
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      ProjectLogger.log("Unable to upload file :"+source.getName() , e);
      e.printStackTrace();
    } catch (IOException e) {
      ProjectLogger.log("Unable to upload file :"+source.getName() , e);
      e.printStackTrace();
    }
    return fileUrl;

  }

  private static void processContainerPath(String containerName, String containerPath, String filePath) {
    if(containerName.contains("/")){
      String arr[]=containerName.split("/", 2);
      containerPath = arr[0];
      filePath=arr[1]+"/";
    }
  }

  public static boolean downloadFile(String containerName , String blobName , String downloadFolder){


    boolean flag = false;
    CloudBlobContainer container = AzureConnectionManager.getContainer(containerName,true);
    // Create or overwrite the "myimage.jpg" blob with contents from a local file.
    CloudBlockBlob blob = null;

    try {
      blob = container.getBlockBlobReference(blobName);
      if(blob.exists()) {
        if(!(downloadFolder.endsWith(("/")))){
          downloadFolder = downloadFolder+"/";
        }
        blob.download(new FileOutputStream(downloadFolder + blobName));
      }
    } catch (URISyntaxException e) {
      ProjectLogger.log("Unable to upload blobfile :"+blobName , e);
      e.printStackTrace();
    } catch (StorageException e) {
      ProjectLogger.log("Unable to upload file :"+blobName , e);
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      ProjectLogger.log("Unable to upload file :"+blobName , e);
      e.printStackTrace();
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

  public static void main(String[] args) {
    String flag = uploadFile("mycontainer" , "serchprocessor001", "/home/arvind/Desktop/SearchProcessor.java");
    System.out.println("SUCCESS");
    System.out.println(flag);
  }
  
  
}
