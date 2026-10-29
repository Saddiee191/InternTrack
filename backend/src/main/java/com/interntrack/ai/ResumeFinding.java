package com.interntrack.ai;

public class ResumeFinding {
    private String sectionName;
    private String findingType; // STRENGTH, IMPROVEMENT, MISSING, WARNING
    private String title;
    private String description;
    private String suggestion;

    public ResumeFinding() {}

    public ResumeFinding(String sectionName, String findingType, String title, String description, String suggestion) {
        this.sectionName = sectionName;
        this.findingType = findingType;
        this.title = title;
        this.description = description;
        this.suggestion = suggestion;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getFindingType() {
        return findingType;
    }

    public void setFindingType(String findingType) {
        this.findingType = findingType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
