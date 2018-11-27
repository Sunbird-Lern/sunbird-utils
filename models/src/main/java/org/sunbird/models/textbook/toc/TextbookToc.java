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

    private String description;
    private String qrCodeReq;
    private String purpose;
    private List<String> keywords;

    public TextbookToc() {
    }

    public TextbookToc(String textbookId, String textbookName, String firstLevelUnit, String secondLevelUnit, String thirdLevelUnit, String medium, String grade, String subject) {
        this.textbookId = textbookId;
        this.textbookName = textbookName;
        this.firstLevelUnit = firstLevelUnit;
        this.secondLevelUnit = secondLevelUnit;
        this.thirdLevelUnit = thirdLevelUnit;
        this.medium = medium;
        this.grade = grade;
        this.subject = subject;
    }

    public TextbookToc(String textbookId, String textbookName, String firstLevelUnit, String secondLevelUnit, String thirdLevelUnit, String medium, String grade, String subject, String description, String qrCodeReq, String purpose, List<String> keywords) {
        this.textbookId = textbookId;
        this.textbookName = textbookName;
        this.firstLevelUnit = firstLevelUnit;
        this.secondLevelUnit = secondLevelUnit;
        this.thirdLevelUnit = thirdLevelUnit;
        this.medium = medium;
        this.grade = grade;
        this.subject = subject;
        this.description = description;
        this.qrCodeReq = qrCodeReq;
        this.purpose = purpose;
        this.keywords = keywords;
    }

    public void setUnit(int level, String data) {
        switch (level) {
            case 1:
                this.firstLevelUnit = data;
                break;
            case 2:
                this.secondLevelUnit = data;
                break;
            case 3:
                this.thirdLevelUnit = data;
                break;
        }
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
