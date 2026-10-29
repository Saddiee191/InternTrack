package com.interntrack.ai;

import java.util.List;

public class ScamAnalysisResult {
    private String overallRiskLevel; // "LOW", "MEDIUM", "HIGH"
    private int riskScore;           // 0 to 100
    private String providerType;     // "RULE_BASED" or "AI_ASSISTED"
    private String disclaimer;
    private List<ScamFinding> findings;
    private List<String> recommendedSteps;

    public ScamAnalysisResult() {}

    public String getOverallRiskLevel() {
        return overallRiskLevel;
    }

    public void setOverallRiskLevel(String overallRiskLevel) {
        this.overallRiskLevel = overallRiskLevel;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public List<ScamFinding> getFindings() {
        return findings;
    }

    public void setFindings(List<ScamFinding> findings) {
        this.findings = findings;
    }

    public List<String> getRecommendedSteps() {
        return recommendedSteps;
    }

    public void setRecommendedSteps(List<String> recommendedSteps) {
        this.recommendedSteps = recommendedSteps;
    }
}
