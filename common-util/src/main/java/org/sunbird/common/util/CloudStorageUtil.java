package org.sunbird.common.util;

import org.sunbird.cloud.storage.IStorageService;
import org.sunbird.cloud.storage.factory.StorageConfig;
import org.sunbird.cloud.storage.factory.StorageServiceFactory;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;
import scala.Option;
import scala.Some;

public class CloudStorageUtil {
  private static final int STORAGE_SERVICE_API_RETRY_COUNT = 3;

  public enum CloudStorageType {
    AZURE("azure");
    private String type;

    private CloudStorageType(String type) {
      this.type = type;
    }

    public String getType() {
      return this.type;
    }

    public static CloudStorageType getByName(String type) {
      if (AZURE.type.equals(type)) {
        return CloudStorageType.AZURE;
      } else {
        ProjectCommonException.throwClientErrorException(
            ResponseCode.errorUnsupportedCloudStorage,
            ProjectUtil.formatMessage(
                ResponseCode.errorUnsupportedCloudStorage.getErrorMessage(), type));
        return null;
      }
    }
  }

  public static String upload(
      CloudStorageType storageType, String container, String objectKey, String filePath) {

    IStorageService storageService = getStorageService(storageType);

    return storageService.upload(
        container,
        filePath,
        objectKey,
        Option.apply(false),
        Option.apply(false),
        Option.empty(),
        Option.apply(STORAGE_SERVICE_API_RETRY_COUNT),
        1);
  }

  public static String getSignedUrl(
      CloudStorageType storageType, String container, String objectKey) {
    IStorageService storageService = getStorageService(storageType);
    int timeoutInSeconds = getTimeoutInSeconds();
    return storageService.getSignedURL(
        container, objectKey, Some.apply(timeoutInSeconds), Some.apply("r"));
  }

  private static IStorageService getStorageService(CloudStorageType storageType) {
    String storageKey = PropertiesCache.getInstance().getProperty(JsonKey.ACCOUNT_NAME);
    String storageSecret = PropertiesCache.getInstance().getProperty(JsonKey.ACCOUNT_KEY);

    StorageConfig storageConfig =
        new StorageConfig(storageType.getType(), storageKey, storageSecret);
    IStorageService storageService = StorageServiceFactory.getStorageService(storageConfig);
    return storageService;
  }

  private static int getTimeoutInSeconds() {
    String timeoutInSecondsStr = ProjectUtil.getConfigValue(JsonKey.DOWNLOAD_LINK_EXPIRY_TIMEOUT);
    return Integer.parseInt(timeoutInSecondsStr);
  }

  public static String getUri(
      CloudStorageType storageType, String container, String prefix, boolean isDirectory) {
    IStorageService storageService = getStorageService(storageType);
    return storageService.getUri(container, prefix, Option.apply(isDirectory));
  }
}
