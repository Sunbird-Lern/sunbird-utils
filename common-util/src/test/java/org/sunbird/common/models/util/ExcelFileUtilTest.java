package org.sunbird.common.models.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class ExcelFileUtilTest {

  @Test
  public void testWriteToFile() {
    String fileName = "test";
    List<List<Object>> data = new ArrayList<>();
    List<Object> dataObjects = new ArrayList<>();
    dataObjects.add("test1");
    dataObjects.add(new ArrayList<>());
    dataObjects.add(1);
    dataObjects.add(2.0D);
    data.add(dataObjects);
    ExcelFileUtil excelFileUtil = new ExcelFileUtil();
    File file = excelFileUtil.writeToFile(fileName, data);
    String[] expectedFileName = StringUtils.split(file.getName(), '.');
    Assert.assertEquals("test", expectedFileName[0]);
    Assert.assertEquals("xlsx", expectedFileName[1]);
  }
  
  @After
  public void deleteFileGenerated(){
    File  file = new File("test.xlsx");
    if(file.exists()){
      file.delete();
    }
  }

}
