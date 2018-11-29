package org.sunbird.content;

import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.util.CloudStorageUtil;
import org.sunbird.common.util.CloudStorageUtil.CloudStorageType;

import java.io.File;

import static org.sunbird.common.util.CloudStorageUtil.CloudStorageType.AZURE;

public class ContentCloudStore {

    public static String FOLDER = ProjectUtil.getConfigValue(JsonKey.CLOUD_FOLDER_CONTENT);

    public static String getUri(String prefix, boolean isDirectory) {
        prefix = File.separator + FOLDER + prefix;
        try {
            CloudStorageType storageType = storageType();
            return CloudStorageUtil.getUri(storageType(), container(storageType), prefix, isDirectory);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUri(CloudStorageType storageType, String prefix, boolean isDirectory) {
        prefix = File.separator + FOLDER + prefix;
        try {
            return CloudStorageUtil.getUri(storageType, container(storageType), prefix, isDirectory);
        } catch (Exception e) {
            return null;
        }
    }

    public static String upload(String objectKey, File file) {
        CloudStorageType storageType = storageType();
        objectKey = File.separator + JsonKey.CONTENT + File.separator + objectKey + File.separator;
        if (file.isFile()) {
            objectKey += file.getName();
            return CloudStorageUtil.upload(storageType, container(storageType), objectKey, file.getAbsolutePath());
        } else {
            return null;
        }
    }

    public static String upload(CloudStorageType storageType, String objectKey, File file) {
        objectKey = File.separator + FOLDER + File.separator + objectKey + File.separator;
        if (file.isFile()) {
            objectKey += file.getName();
            return CloudStorageUtil.upload(storageType, container(storageType), objectKey, file.getAbsolutePath());
        } else {
            return null;
        }
    }

    private static CloudStorageType storageType() {
        CloudStorageType storageType = null;
        switch (ProjectUtil.getConfigValue(JsonKey.CONTENT_CLOUD_STORAGE_TYPE)) {
            case "azure": storageType = AZURE;
                          break;
            default:      break;
        }
        return storageType;
    }

    private static String container(CloudStorageType type) {
        String container = null;
        switch (type) {
            case AZURE: container = ProjectUtil.getConfigValue(JsonKey.CONTENT_AZURE_STORAGE_CONTAINER);
                        break;
            default   : break;
        }
        return container;
    }
}
