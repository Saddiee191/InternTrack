package com.interntrack.ai;

public class ScamFinding {
    private String indicatorName;
    private int riskWeight; // 10 to 40
    private String evidenceSnippet;
    private String explanation;
    private String recommendation;

    public ScamFinding() {}

    public ScamFinding(String indicatorName, int riskWeight, String evidenceSnippet, String explanation, String recommendation) {
        this.indicatorName = indicatorName;
        this.riskWeight = riskWeight;
        this.evidenceSnippet = evidenceSnippet;
        this.explanation = explanation;
        this.recommendation = recommendation;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public int getRiskWeight() {
        return riskWeight;
    }

    public void setRiskWeight(int riskWeight) {
        this.riskWeight = riskWeight;
    }

    public String getEvidenceSnippet() {
        return evidenceSnippet;
    }

    public void setEvidenceSnippet(String evidenceSnippet) {
        this.evidenceSnippet = evidenceSnippet;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
