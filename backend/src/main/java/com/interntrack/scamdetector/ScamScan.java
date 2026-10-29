package com.interntrack.scamdetector;

import java.time.LocalDateTime;
import java.util.List;

public class ScamScan {
    private Long id;
    private Long userId;
    private String companyName;
    private String jobTitle;
    private String recruiterContact;
    private String applicationUrl;
    private String stipendSalary;
    private String jobDescription;
    private String recruiterMessage;
    private String overallRiskLevel; // LOW, MEDIUM, HIGH
    private Integer riskScore;       // 0 to 100
    private String providerType;
    private Boolean isReported;
    private LocalDateTime createdAt;
    private List<ScamScanFinding> findings;
    private List<String> recommendedSteps;

    public ScamScan() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getRecruiterContact() {
        return recruiterContact;
    }

    public void setRecruiterContact(String recruiterContact) {
        this.recruiterContact = recruiterContact;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public String getStipendSalary() {
        return stipendSalary;
    }

    public void setStipendSalary(String stipendSalary) {
        this.stipendSalary = stipendSalary;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getRecruiterMessage() {
        return recruiterMessage;
    }

    public void setRecruiterMessage(String recruiterMessage) {
        this.recruiterMessage = recruiterMessage;
    }

    public String getOverallRiskLevel() {
        return overallRiskLevel;
    }

    public void setOverallRiskLevel(String overallRiskLevel) {
        this.overallRiskLevel = overallRiskLevel;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public Boolean getIsReported() {
        return isReported;
    }

    public void setIsReported(Boolean reported) {
        isReported = reported;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ScamScanFinding> getFindings() {
        return findings;
    }

    public void setFindings(List<ScamScanFinding> findings) {
        this.findings = findings;
    }

    public List<String> getRecommendedSteps() {
        return recommendedSteps;
    }

    public void setRecommendedSteps(List<String> recommendedSteps) {
        this.recommendedSteps = recommendedSteps;
    }
}
