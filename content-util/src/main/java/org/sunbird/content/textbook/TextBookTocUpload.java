package org.sunbird.content.textbook;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.content.ContentCloudStore;
import org.sunbird.models.textbook.toc.TextbookToc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TextBookTocUpload {

    public static final String textBookTocCloudFolder = "/textbook/toc";

    public static String csvToCloud(File csv, Map<String, Object> content, String textbookId, String versionKey) {
        ProjectLogger.
                log("Creating CSV for TextBookToC | Id: " + textbookId + "Version Key: " + versionKey);
        try {

            csv = new File("/data/" + textbookId + "_" + versionKey + ".csv");
            FileUtils.deleteQuietly(csv);
            FileUtils.touch(csv);
            StringBuilder s = getWritableString(textbookId, content);
            PrintWriter writer = new PrintWriter(csv);
            writer.write(s.toString());
            writer.close();
            ProjectLogger.
                    log("Uploading CSV to Cloud Storage for TextBookToC | Id: " +
                            textbookId + ", Version Key: " + versionKey, LoggerEnum.INFO);
            return ContentCloudStore.upload(textBookTocCloudFolder, csv);
        } catch (IOException e) {
            ProjectLogger.
                    log("Error creating CSV File at File Path | " + csv.getAbsolutePath(),
                            LoggerEnum.ERROR);
            throw new ProjectCommonException(
                    ResponseCode.errorCreatingFile.getErrorCode(),
                    ResponseCode.errorCreatingFile.getErrorMessage(),
                    ResponseCode.SERVER_ERROR.getResponseCode());
        } finally {
            ProjectLogger.
                    log("Deleting CSV for TextBookToC | Id: " + textbookId + ", Version Key: " + versionKey,
                            LoggerEnum.INFO);
            try {
                if (csv.exists())
                    csv.delete();
            } catch (SecurityException e) {
                ProjectLogger.log("Error! While deleting the local csv file: " + csv.getAbsolutePath(),
                        LoggerEnum.ERROR);
            } catch (Exception e) {
                ProjectLogger.log("Error! Something Went wrong while deleting csv file: "
                        + csv.getAbsolutePath(), LoggerEnum.ERROR);
            }
        }
    }

    private static StringBuilder getWritableString(String textbookId, Map<String, Object> content) {
        StringBuilder sb = new StringBuilder();
        appendCSVColumnNames(sb);
        TextbookToc textbookToc = new TextbookToc(textbookId, (String) content.get(JsonKey.CONTENT_PROPERTY_NAME));
        processHierarchy(content, textbookToc, 0, sb);
        return sb;
    }

    private static void appendCSVColumnNames(StringBuilder sb) {
        sb.append("Medium").append(JsonKey.CSV_SEPERATOR).
                append("Grade").append(JsonKey.CSV_SEPERATOR).
                append("Subject").append(JsonKey.CSV_SEPERATOR).
                append("Textbook Name").append(JsonKey.CSV_SEPERATOR).
                append("Level 1 Textbook Unit").append(JsonKey.CSV_SEPERATOR).
                append("Level 2 Textbook Unit").append(JsonKey.CSV_SEPERATOR).
                append("Level 3 Textbook Unit").append(JsonKey.CSV_SEPERATOR).
                append("Level 4 Textbook Unit").append(JsonKey.CSV_SEPERATOR).
                append("Unit Name").append(JsonKey.CSV_SEPERATOR).
                append("Description").append(JsonKey.CSV_SEPERATOR).
                append("QR Code Required?").append(JsonKey.CSV_SEPERATOR).
                append("Purpose of Content to be linked").append(JsonKey.CSV_SEPERATOR).
                append("Keywords").
                append("\n");
    }

    private static void processHierarchy(Map<String, Object> contentHierarchy, TextbookToc textbookToc,
                                         int level, StringBuilder sb) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) contentHierarchy.get("children");
        if(null != children && !children.isEmpty()) {
            if (5 == level) return;
            for (Map<String, Object> child : children) {
                if (StringUtils.equalsIgnoreCase(JsonKey.CONTENT_PROPERTY_VISIBILITY_PARENT,
                        (String) child.get(JsonKey.CONTENT_PROPERTY_VISIBILITY)) &&
                        StringUtils.equals(JsonKey.CONTENT_MIME_TYPE_COLLECTION,
                                (String) contentHierarchy.get(JsonKey.CONTENT_PROPERTY_MIME_TYPE))) {
                    updateCurrentLevelToTextbookToc(textbookToc, child, ++level);
                    sb.append(textbookToc.toCSVString()).append("\n");
                    processHierarchy(child, textbookToc, level, sb);
                    textbookToc.setUnit(level--, null);
                }
            }
        }
    }
    private static void updateCurrentLevelToTextbookToc(TextbookToc textbookToc,
                                                        Map<String, Object> content,
                                                        int level) {
        textbookToc.setMedium(      (String) content.get(JsonKey.CONTENT_PROPERTY_MEDIUM));
        textbookToc.setGrade(       (String) content.get(JsonKey.CONTENT_PROPERTY_GRADE_LEVEL));
        textbookToc.setSubject(     (String) content.get(JsonKey.CONTENT_PROPERTY_SUBJECT));
        textbookToc.setTextbookName((String) content.get(JsonKey.CONTENT_PROPERTY_NAME));
        textbookToc.setUnit(level,  (String) content.get(JsonKey.CONTENT_PROPERTY_NAME));
        textbookToc.setIdentifier(  (String) content.get("identifier"));
    }
}
