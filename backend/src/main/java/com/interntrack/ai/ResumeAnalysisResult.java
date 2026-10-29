package com.interntrack.ai;

import java.util.List;

public class ResumeAnalysisResult {
    private int completenessScore; // 0 to 100
    private int matchPercentage;   // 0 to 100
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String providerType;   // "RULE_BASED" or "AI_ASSISTED"
    private String summary;
    private List<ResumeFinding> findings;

    public ResumeAnalysisResult() {}

    public int getCompletenessScore() {
        return completenessScore;
    }

    public void setCompletenessScore(int completenessScore) {
        this.completenessScore = completenessScore;
    }

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<ResumeFinding> getFindings() {
        return findings;
    }

    public void setFindings(List<ResumeFinding> findings) {
        this.findings = findings;
    }
}
