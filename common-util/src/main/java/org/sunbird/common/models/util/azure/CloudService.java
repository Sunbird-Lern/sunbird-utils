package org.sunbird.common.models.util.azure;

import java.util.List;

/**
 * Created by arvind on 24/8/17.
 */
public interface CloudService {

  String uploadFile(String containerName , String filName , String fileLocation);

  boolean downLoadFile(String containerName , String fileName, String downloadFolder);

  boolean deleteFile(String containerName , String fileName);

  List<String> listAllFiles(String containerName);

  boolean deleteContainer(String containerName);

}
