/**
 * 
 */
package org.sunbird.common.models.util.azure;

import java.io.File;
import java.io.FileInputStream;

import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;

import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

/**
 * @author Manzarul
 *
 */
public class FileUtility {
  
  /**
   * This method will upload the file inside Azure storage with provided filename
   * under particular container. if you won't provide the filename then it will
   * pick the file name from file object and upload the file with that name only.
   * @param fileName String
   * @param containerName String
   * @param file File
   * @return boolean
   */
  public static boolean uploadFile(String fileName, String containerName,
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
        ConnectionManager.getContainer(containerName);
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
  public static boolean deleteFile(String fileName, String containerName )
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
        ConnectionManager.getContainer(containerName);
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
        ConnectionManager.getContainer(containerName);
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
  
  
}
