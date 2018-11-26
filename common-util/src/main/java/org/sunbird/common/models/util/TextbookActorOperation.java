package org.sunbird.common.models.util;

/**
 * This enum holds all common constant for Textbook Toc API
 * @author gauraw
 */
public enum TextbookActorOperation {

    TEXTBOOK_TOC_UPLOAD("textbookTocUpload"),
    TEXTBOOK_TOC_DOWNLOAD("textbookTocDownload");

    private String value;

    TextbookActorOperation(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
