package org.sunbird.common.models.util;

import java.io.File;
import java.util.List;

public abstract class FileUtil {
  
  public abstract File writeToFile(String fileName,List<List<Object>> dataValues);
  
  @SuppressWarnings("unchecked")
  protected static String getListValue(Object obj) {
    List<Object> data = (List<Object>) obj;
    StringBuffer sb = new StringBuffer();
    for (Object value : data) {
      sb.append((String) value).append(",");
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

}
