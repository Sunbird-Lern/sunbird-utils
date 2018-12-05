package org.sunbird.content.textbook;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.content.util.ContentCloudStore;
import org.sunbird.content.util.TextBookTocUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.apache.commons.csv.CSVFormat.DEFAULT;
import static org.sunbird.common.responsecode.ResponseCode.SERVER_ERROR;
import static org.sunbird.common.responsecode.ResponseCode.errorProcessingRequest;
import static org.sunbird.content.textbook.FileType.Type.CSV;

public class TextBookTocUploader {

    public static final String textBookTocFolder = File.separator + "textbook" + File.separator +"toc";

    private static Map<String, Object> inputMapping =
            TextBookTocUtil.getObjectFrom(
                    ProjectUtil.getConfigValue(JsonKey.TEXTBOOK_TOC_INPUT_MAPPING), Map.class);

    private static Optional<Map<String, Object>> metadata   = Optional.ofNullable(inputMapping.get("metadata")).
                                                                map(e -> (Map<String, Object>) e);

    private static Optional<Map<String, Object>> hierarchy  = Optional.ofNullable(inputMapping.get("hierarchy")).
                                                                map(e -> (Map<String, Object>) e);

    private static boolean suppressEmptyColumns = Optional.ofNullable(
                                                    ProjectUtil.getConfigValue(
                                                            JsonKey.TEXT_TOC_FILE_SUPPRESS_COLUMN_NAMES)).
                                                    map(Boolean::parseBoolean).
                                                    orElse(false);

    private static Set<String> metadataProperties = metadata.map(Map::keySet).orElse(null);

    private static int levels       = hierarchy.map(Map::size).orElse(0);
    private static int metadataSize = metadata. map(Map::size).orElse(0);

    private static int metadataStartPos;
    private static int hierarchyStartPos;

    private static String[] keyNames;
    private static String[] columnNames;

    static {
        int currPos = 0;
        for (Entry e: inputMapping.entrySet()) {
            if ("metadata".equals(e.getKey()))
                metadataStartPos = currPos;
            if ("hierarchy".equals(e.getKey()))
                hierarchyStartPos = currPos;
            currPos = ((Map<String, Object>)e.getValue()).size();
            keyNames = keyNames();
            columnNames = columnNames();
        }
    }

    private boolean[] isColumnPresent;

    private FileType fileType;

    List<Object[]> rows = new ArrayList<>();

    private Object[] row;

    private static String[] keyNames() {
        int i = 0;
        String[] columnNames = new String[levels + metadataSize];
        for (Entry<String, Object> e : inputMapping.entrySet()) {
            for(String s: ((Map<String, String>) e.getValue()).keySet()) {
                columnNames[i++] = s;
            }
        }
        return columnNames;
    }

    private static String[] columnNames() {
        int i = 0;
        String[] columnNames = new String[levels + metadataSize];
        for (Entry<String, Object> e : inputMapping.entrySet()) {
            for(Entry entry: ((Map<String, String>) e.getValue()).entrySet()) {
                columnNames[i++] = (String) entry.getValue();
            }
        }
        return columnNames;
    }

    public TextBookTocUploader(FileType fileType) {
        this.fileType = null == fileType ? CSV.getFileType() : fileType;
        if (suppressEmptyColumns)
            isColumnPresent = new boolean[levels+metadataSize];
    }

    public String execute(Map<String, Object> content,
                             String textbookId, String versionKey) {

        if (!hierarchy.filter(h -> 0 != h.size()).isPresent())
            return "";

        ProjectLogger.
                log("Creating CSV for TextBookToC | Id: " + textbookId + "Version Key: " + versionKey);
        File file = null;
        try {
            file = new File(File.separator + "data" + textBookTocFolder + File.separator +
                    textbookId + "_" + versionKey + fileType.getExtension());
            FileUtils.deleteQuietly(file);
            FileUtils.touch(file);
            populateDataIntoFile(content, file);
            ProjectLogger.
                    log("Uploading " + fileType.getType() + " to Cloud Storage for TextBookToC | Id: " +
                            textbookId + ", Version Key: " + versionKey, LoggerEnum.INFO);
            return ContentCloudStore.upload(textBookTocFolder, file);
        } catch (IOException e) {
            ProjectLogger.
                    log("Error creating "+ fileType.getType() + " File at File Path | " +
                            file.getAbsolutePath(), LoggerEnum.ERROR);
            throw new ProjectCommonException(
                    errorProcessingRequest.getErrorCode(),
                    errorProcessingRequest.getErrorMessage(),
                    SERVER_ERROR.getResponseCode());
        } finally {
            ProjectLogger.
                    log("Deleting " + fileType.getType() + " for TextBookToC | Id: " + textbookId + ", " +
                            "Version Key: " + versionKey, LoggerEnum.INFO);
            try {
                if (null != file && file.exists())
                    file.delete();
            } catch (SecurityException e) {
                ProjectLogger.log("Error! While deleting the local csv file: " + file.getAbsolutePath(),
                        LoggerEnum.ERROR);
            } catch (Exception e) {
                ProjectLogger.log("Error! Something Went wrong while deleting csv file: "
                        + file.getAbsolutePath(), LoggerEnum.ERROR);
            }
        }
    }

    private void populateDataIntoFile(Map<String, Object> content, File file) {
        FileWriter out;
        try {
            if (suppressEmptyColumns) {
                processHierarchySuppressColumns(content);
                String[] columns = IntStream.range(0, keyNames.length).
                        mapToObj(i -> {
                            if (isColumnPresent[i] == true)
                                return columnNames[i];
                            else
                                return null;
                        }).
                        filter(Objects::nonNull).
                        toArray(String[]::new);
                out = new FileWriter(file);
                CSVPrinter printer = new CSVPrinter(out,
                        DEFAULT.withHeader(columns));
                for (Object[] row : rows) {
                    Object[] tempRow = IntStream.range(0, keyNames.length).
                            mapToObj(i -> {
                                if (isColumnPresent[i] == true)
                                    return row[i];
                                return null;
                            }).
                            filter(Objects::nonNull).
                            toArray(Object[]::new);
                    printer.printRecord(tempRow);
                }
                printer.close();
                out.close();
            } else {
                processHierarchy(content);
                out = new FileWriter(file);
                CSVPrinter printer = new CSVPrinter(out, DEFAULT.withHeader(columnNames));
                for (Object[] row : rows)
                    printer.printRecord(row);
                printer.close();
                out.close();
            }
        } catch (IOException e) {
            throw new ProjectCommonException(
                    errorProcessingRequest.getErrorCode(),
                    errorProcessingRequest.getErrorMessage(),
                    SERVER_ERROR.getResponseCode());
        }

    }

    public void initializeRow() {
        row = new Object[levels + metadataSize];
    }

    private void updateRowWithData(Map<String, Object> content, String key, int offset, int pos) {
        row[offset + pos] = null == content || null == content.get(key) ? "" : getValue(content.get(key));
    }

    private Object getValue(Object o) {
        if (o instanceof List) {
            List l = (List) o;
            return String.join(",", l);
        } else {
            return o;
        }
    }

    private void processHierarchy(Map<String, Object> contentHierarchy) {
        initializeRow();
        int level = 0;
        updateRowWithData(contentHierarchy, JsonKey.NAME, hierarchyStartPos, level);
        processHierarchyRecursive (contentHierarchy, level);
    }

    private void processHierarchyRecursive(Map<String, Object> contentHierarchy, int level) {
        List<Map<String, Object>> children =
                (List<Map<String, Object>>) contentHierarchy.get(JsonKey.CHILDREN);
        if(null != children && !children.isEmpty()) {
            if (levels == level) return;
            for (Map<String, Object> child : children) {
                if (StringUtils.equalsIgnoreCase(JsonKey.CONTENT_PROPERTY_VISIBILITY_PARENT,
                        (String) child.get(JsonKey.CONTENT_PROPERTY_VISIBILITY)) &&
                        StringUtils.equals(JsonKey.CONTENT_MIME_TYPE_COLLECTION,
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
        for (String e : metadataProperties)
            updateRowWithData(content, e, metadataStartPos, i++);
    }

    private void appendRow() {
        rows.add(Arrays.copyOf(row, row.length));
    }

    private void processHierarchySuppressColumns(Map<String, Object> contentHierarchy) {
        initializeRow();
        int level = 0;
        updateRowWithDataSuppressColumns(contentHierarchy, JsonKey.NAME, hierarchyStartPos, level);
        processHierarchyRecursiveSuppressColumns(contentHierarchy, level);
    }

    private void processHierarchyRecursiveSuppressColumns(Map<String, Object> contentHierarchy, int level) {
        List<Map<String, Object>> children =
                (List<Map<String, Object>>) contentHierarchy.get(JsonKey.CHILDREN);
        if(null != children && !children.isEmpty()) {
            if (levels == level) return;
            for (Map<String, Object> child : children) {
                if (StringUtils.equalsIgnoreCase(JsonKey.CONTENT_PROPERTY_VISIBILITY_PARENT,
                        (String) child.get(JsonKey.CONTENT_PROPERTY_VISIBILITY)) &&
                        StringUtils.equals(JsonKey.CONTENT_MIME_TYPE_COLLECTION,
                                (String) contentHierarchy.get(JsonKey.CONTENT_PROPERTY_MIME_TYPE))) {
                    updateMetadataSuppressColumns(child, ++level);
                    appendRow();
                    processHierarchyRecursiveSuppressColumns(child, level);
                    updateMetadataSuppressColumns(null, level--);
                }
            }
        }
    }

    private void updateRowWithDataSuppressColumns(Map<String, Object> content, String key, int offset, int pos) {
        row[offset + pos] = (null == content || null == content.get(key)) ? "" : getValue(content.get(key));
        if ("" != row[offset + pos].toString()) {
            if (!isColumnPresent[offset + pos])
                isColumnPresent[offset + pos] =
                        !isColumnPresent[offset + pos];
        }
    }


    private void updateMetadataSuppressColumns(Map<String, Object> content, int level) {
        updateRowWithDataSuppressColumns(content, JsonKey.NAME, hierarchyStartPos, level);
        int i = 0;
        for (String e : metadataProperties)
            updateRowWithDataSuppressColumns(content, e, metadataStartPos, i++);
    }

}

