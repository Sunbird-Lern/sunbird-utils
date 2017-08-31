package org.sunbird.common.models.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sunbird.common.models.util.ProjectLogger;

public class ExcelFileUtil {

  public static File writeToFile(String fileName, List<List<Object>> values) {
    //Blank workbook
    XSSFWorkbook workbook = new XSSFWorkbook(); 
     
    //Create a blank sheet
    XSSFSheet sheet = workbook.createSheet("Data");
    FileOutputStream out = null;
    File file =null;
  
    int rownum = 0;
    for (Object key : values)
    {
        Row row = sheet.createRow(rownum);
        List<Object> objArr = values.get(rownum);
        int cellnum = 0;
        for (Object obj : objArr)
        {
           Cell cell = row.createCell(cellnum++);
           if(obj instanceof String){
                cell.setCellValue((String)obj);
           }else if(obj instanceof Integer){
                cell.setCellValue((Integer)obj);
           }else if(obj instanceof List){
             cell.setCellValue(getListValue(obj));
           }else {
             if(ProjectUtil.isNotNull(obj)) {
               cell.setCellValue(obj.toString());
             }
           }
        }
        rownum++;
    }
    try
    {
        //Write the workbook in file system
        file = new File(fileName + ".xlsx");
        out = new FileOutputStream(file);
        workbook.write(out);
        //out.close();
        ProjectLogger.log("File " + fileName+ " created successfully");

    } 
    catch (Exception e) 
    {
        e.printStackTrace();
    }finally{
      if(null != out){
        try {
          out.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return file;
  }
  
  @SuppressWarnings("unchecked")
  private static String getListValue(Object obj) {
    List<Object> data = (List<Object>) obj;
    if (!(data.isEmpty())) {
      StringBuffer sb = new StringBuffer();
      for (Object value : data) {
        sb.append((String) value).append(",");
      }
      sb.deleteCharAt(sb.length() - 1);
      return sb.toString();
    }
    return "";
  }
}
