package org.sunbird.content.textbook;

import static org.sunbird.content.textbook.FileType.Type.CSV;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.content.util.ContentCloudStore;
import org.sunbird.content.util.TextBookTocUtil;

public class TextBookTocUploader {

  public static final String textBookTocFolder =
      File.separator + "textbook" + File.separator + "toc";

  private static Map<String, Object> inputMapping =
      TextBookTocUtil.getObjectFrom(
          ProjectUtil.getConfigValue(JsonKey.TEXTBOOK_TOC_INPUT_MAPPING), Map.class);

  private static Optional<Map<String, Object>> metadata =
      Optional.ofNullable(inputMapping.get("metadata")).map(e -> (Map<String, Object>) e);

  private static Optional<Map<String, Object>> hierarchy =
      Optional.ofNullable(inputMapping.get("hierarchy")).map(e -> (Map<String, Object>) e);

  private static boolean suppressEmptyColumns =
      Optional.ofNullable(ProjectUtil.getConfigValue(JsonKey.TEXT_TOC_FILE_SUPPRESS_COLUMN_NAMES))
          .map(Boolean::parseBoolean)
          .orElse(false);

  private static Set<String> metadataProperties = metadata.map(Map::keySet).orElse(null);

  private static int levels = hierarchy.map(Map::size).orElse(0);
  private static int metadataSize = metadata.map(Map::size).orElse(0);

  private static int metadataStartPos;
  private static int hierarchyStartPos;

  private String columnNamesRow;

  static {
    int currPos = 0;
    for (Entry e : inputMapping.entrySet()) {
      if ("metadata".equals(e.getKey())) metadataStartPos = currPos;
      if ("hierarchy".equals(e.getKey())) hierarchyStartPos = currPos;
      currPos = ((Map<String, Object>) e.getValue()).size();
      columnNames = columnNames();
    }
  }

  private static String[] columnNames;

  private boolean[] isColumnPresent;

  private StringBuilder sb = new StringBuilder();

  private FileType fileType;

  List<String[]> rows = new ArrayList<>();

  private String[] row;

  private static String[] columnNames() {
    int i = 0;
    String[] columnNames = new String[levels + metadataSize];
    for (Entry<String, Object> e : inputMapping.entrySet()) {
      for (String s : ((Map<String, Object>) e.getValue()).keySet()) {
        columnNames[i++] = s;
      }
    }
    return columnNames;
  }

  public TextBookTocUploader(FileType fileType) {
    this.fileType = null == fileType ? CSV.getFileType() : fileType;
    if (suppressEmptyColumns) {
      isColumnPresent = new boolean[levels + metadataSize];
    } else {
      columnNamesRow = String.join(this.fileType.getSeperator(), columnNames) + "\n";
    }
  }

  public String execute(Map<String, Object> content, String textbookId, String versionKey) {

    if (!hierarchy.filter(h -> 0 != h.size()).isPresent()) return "";

    ProjectLogger.log(
        "Creating CSV for TextBookToC | Id: " + textbookId + "Version Key: " + versionKey);
    File file = null;
    try {
      file =
          new File(
              File.separator
                  + "data"
                  + textBookTocFolder
                  + File.separator
                  + textbookId
                  + "_"
                  + versionKey
                  + fileType.getExtension());
      FileUtils.deleteQuietly(file);
      FileUtils.touch(file);
      String s = getWritableString(content);
      PrintWriter writer = new PrintWriter(file);
      writer.write(s);
      writer.close();
      ProjectLogger.log(
          "Uploading "
              + fileType.getType()
              + " to Cloud Storage for TextBookToC | Id: "
              + textbookId
              + ", Version Key: "
              + versionKey,
          LoggerEnum.INFO);
      return ContentCloudStore.upload(textBookTocFolder, file);
    } catch (IOException e) {
      ProjectLogger.log(
          "Error creating " + fileType.getType() + " File at File Path | " + file.getAbsolutePath(),
          LoggerEnum.ERROR);
      throw new ProjectCommonException(
          ResponseCode.errorProcessingRequest.getErrorCode(),
          ResponseCode.errorProcessingRequest.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    } finally {
      ProjectLogger.log(
          "Deleting "
              + fileType.getType()
              + " for TextBookToC | Id: "
              + textbookId
              + ", "
              + "Version Key: "
              + versionKey,
          LoggerEnum.INFO);
      try {
        if (null != file && file.exists()) file.delete();
      } catch (SecurityException e) {
        ProjectLogger.log(
            "Error! While deleting the local csv file: " + file.getAbsolutePath(),
            LoggerEnum.ERROR);
      } catch (Exception e) {
        ProjectLogger.log(
            "Error! Something Went wrong while deleting csv file: " + file.getAbsolutePath(),
            LoggerEnum.ERROR);
      }
    }
  }

  private String getWritableString(Map<String, Object> content) {
    if (suppressEmptyColumns) {
      processHierarchySuppressColumns(content);
      return processRowsAndGetString();
    } else {
      processHierarchy(content);
      return columnNames + sb.toString();
    }
  }

  public String[] initializeRow() {
    return row = new String[levels + metadataSize];
  }

  private void updateRowWithData(Map<String, Object> content, String key, int offset, int pos) {
    row[offset + pos] =
        null == content || null == content.get(key) ? null : (String) content.get(key);
  }

  private void processHierarchy(Map<String, Object> contentHierarchy) {
    row = initializeRow();
    int level = 0;
    updateRowWithData(contentHierarchy, JsonKey.NAME, hierarchyStartPos, level);
    processHierarchyRecursive(contentHierarchy, level);
  }

  private void processHierarchyRecursive(Map<String, Object> contentHierarchy, int level) {
    List<Map<String, Object>> children =
        (List<Map<String, Object>>) contentHierarchy.get("children");
    if (null != children && !children.isEmpty()) {
      if (levels == level) return;
      for (Map<String, Object> child : children) {
        if (StringUtils.equalsIgnoreCase(
                JsonKey.CONTENT_PROPERTY_VISIBILITY_PARENT,
                (String) child.get(JsonKey.CONTENT_PROPERTY_VISIBILITY))
            && StringUtils.equals(
                JsonKey.CONTENT_MIME_TYPE_COLLECTION,
                (String) contentHierarchy.get(JsonKey.CONTENT_PROPERTY_MIME_TYPE))) {
          updateMetadata(child, ++level);
          appendRow();
          processHierarchyRecursive(child, level);
          updateMetadata(null, level--);
        }
      }
    }
  }

  private void updateMetadata(Map<String, Object> content, int level) {
    updateRowWithData(content, JsonKey.NAME, hierarchyStartPos, level);
    int i = 0;
    for (String e : metadataProperties) updateRowWithData(content, e, metadataStartPos, i++);
  }

  private void appendRow() {
    Arrays.stream(row)
        .forEach(
            e -> {
              if (StringUtils.isBlank(e)) sb.append(fileType.getSeperator());
              else sb.append(e).append(fileType.getSeperator());
            });
    sb.deleteCharAt(sb.lastIndexOf(fileType.getSeperator()));
    sb.append("\n");
  }

  private void processHierarchySuppressColumns(Map<String, Object> contentHierarchy) {
    row = initializeRow();
    int level = 0;
    updateRowWithDataSuppressColumns(contentHierarchy, JsonKey.NAME, hierarchyStartPos, level);
    processHierarchyRecursiveSuppressColumns(contentHierarchy, level);
  }

  private void processHierarchyRecursiveSuppressColumns(
      Map<String, Object> contentHierarchy, int level) {
    List<Map<String, Object>> children =
        (List<Map<String, Object>>) contentHierarchy.get("children");
    if (null != children && !children.isEmpty()) {
      if (levels == level) return;
      for (Map<String, Object> child : children) {
        if (StringUtils.equalsIgnoreCase(
                JsonKey.CONTENT_PROPERTY_VISIBILITY_PARENT,
                (String) child.get(JsonKey.CONTENT_PROPERTY_VISIBILITY))
            && StringUtils.equals(
                JsonKey.CONTENT_MIME_TYPE_COLLECTION,
                (String) contentHierarchy.get(JsonKey.CONTENT_PROPERTY_MIME_TYPE))) {
          updateMetadataSuppressColumns(child, ++level);
          appendRowSuppressedColumns();
          processHierarchyRecursiveSuppressColumns(child, level);
          updateMetadataSuppressColumns(null, level--);
        }
      }
    }
  }

  private void updateRowWithDataSuppressColumns(
      Map<String, Object> content, String key, int offset, int pos) {
    row[offset + pos] =
        (null == content || null == content.get(key)) ? null : (String) content.get(key);
    if (null != row[offset + pos]) {
      if (!isColumnPresent[offset + pos])
        isColumnPresent[offset + pos] = !isColumnPresent[offset + pos];
    }
  }

  private void updateMetadataSuppressColumns(Map<String, Object> content, int level) {
    updateRowWithDataSuppressColumns(content, JsonKey.NAME, hierarchyStartPos, level);
    int i = 0;
    for (String e : metadataProperties)
      updateRowWithDataSuppressColumns(content, e, metadataStartPos, i++);
  }

  private void appendRowSuppressedColumns() {
    String[] tempRow = Arrays.copyOf(row, row.length);
    rows.add(tempRow);
  }

  private String processRowsAndGetString() {
    for (int i = 0; i < columnNames.length; i++) {
      if (isColumnPresent[i]) {
        sb.append(columnNames[i]).append(fileType.getSeperator());
      }
    }
    sb.deleteCharAt(sb.lastIndexOf(fileType.getSeperator()));
    sb.append("\n");
    rows.forEach(
        row -> {
          for (int i = 0; i < row.length; i++) {
            if (!StringUtils.isBlank(row[i])) sb.append(row[i]);
            if (isColumnPresent[i] && i != row.length - 1) sb.append(fileType.getSeperator());
          }
        });
    sb.append("\n");
    return sb.toString();
  }
}
