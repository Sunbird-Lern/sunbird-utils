package org.sunbird.common.util;

import org.sunbird.cloud.storage.IStorageService;
import org.sunbird.cloud.storage.factory.StorageConfig;
import org.sunbird.cloud.storage.factory.StorageServiceFactory;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;

import scala.Some;

public class CloudUploadUtil {

  public enum CloudStorageTypes {
    AZURE("azure");
    private String type;

    private CloudStorageTypes(String type) {
      this.type = type;
    }

    public String getType() {
      return this.type;
    }

    public static CloudStorageTypes getByName(String type) {
      if (AZURE.type.equals(type)) {
        return CloudStorageTypes.AZURE;
      } else {
        ProjectCommonException.throwClientErrorException(
            ResponseCode.unSupportedCloudStorage,
            ProjectUtil.formatMessage(
                ResponseCode.unSupportedCloudStorage.getErrorMessage(), type));
        return null;
      }
    }
  }

  public static String upload(
      CloudStorageTypes storageType, String container, String objectKey, String filePath) {

    IStorageService storageService = getStorageService(storageType);
    int timeoutInSeconds = getTimeoutInSeconds();
    return storageService.upload(
        container,
        filePath,
        objectKey,
        Some.apply(true),
        Some.apply(false),
        Some.apply(300),
        Some.apply(1));
  }

  public static String getSignedUrl(
      CloudStorageTypes storageType, String container, String objectKey) {
    IStorageService storageService = getStorageService(storageType);
    int timeoutInSeconds = getTimeoutInSeconds();
    return storageService.getSignedURL(
        container, objectKey, Some.apply(timeoutInSeconds), Some.apply("r"));
  }

  private static IStorageService getStorageService(CloudStorageTypes storageType) {
    String storageKey = PropertiesCache.getInstance().getProperty(JsonKey.ACCOUNT_NAME);
    String storageSecret = PropertiesCache.getInstance().getProperty(JsonKey.ACCOUNT_KEY);

    StorageConfig storageConfig =
        new StorageConfig(storageType.getType(), storageKey, storageSecret);
    IStorageService storageService = StorageServiceFactory.getStorageService(storageConfig);
    return storageService;
  }

  private static int getTimeoutInSeconds() {
    String timeoutInSecondsStr =
        PropertiesCache.getInstance().getProperty(JsonKey.BULK_UPLOAD_STORAGE_TIMEOUT_SECONDS);
    return Integer.parseInt(timeoutInSecondsStr);
  }

  public static void main(String[] args) {

    CloudUploadUtil.upload(
        CloudStorageTypes.AZURE,
        "orgcontainer",
        "org1.csv",
        "/Users/amolgolvelkar/Downloads/org1.csv");
    System.out.println(
        CloudUploadUtil.getSignedUrl(CloudStorageTypes.AZURE, "orgcontainer", "org1.csv"));
  }
}
