package org.sunbird.models.textbook.toc;

import java.util.List;

/**
 * Model Class for Textbook Toc
 * @author gauraw
 */
public class TextbookToc {

    private String textbookId;
    private String textbookName;

    private String firstLevelUnit;
    private String secondLevelUnit;
    private String thirdLevelUnit;

    private String medium;
    private String grade;
    private String subject;

    private String leafNodeDesc;
    private String qrCodeReq;
    private String purpose;
    private List<String> keywords;

    public String getTextbookId() {
        return textbookId;
    }

    public void setTextbookId(String textbookId) {
        this.textbookId = textbookId;
    }

    public String getTextbookName() {
        return textbookName;
    }

    public void setTextbookName(String textbookName) {
        this.textbookName = textbookName;
    }

    public String getFirstLevelUnit() {
        return firstLevelUnit;
    }

    public void setFirstLevelUnit(String firstLevelUnit) {
        this.firstLevelUnit = firstLevelUnit;
    }

    public String getSecondLevelUnit() {
        return secondLevelUnit;
    }

    public void setSecondLevelUnit(String secondLevelUnit) {
        this.secondLevelUnit = secondLevelUnit;
    }

    public String getThirdLevelUnit() {
        return thirdLevelUnit;
    }

    public void setThirdLevelUnit(String thirdLevelUnit) {
        this.thirdLevelUnit = thirdLevelUnit;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLeafNodeDesc() {
        return leafNodeDesc;
    }

    public void setLeafNodeDesc(String leafNodeDesc) {
        this.leafNodeDesc = leafNodeDesc;
    }

    public String getQrCodeReq() {
        return qrCodeReq;
    }

    public void setQrCodeReq(String qrCodeReq) {
        this.qrCodeReq = qrCodeReq;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    
}
