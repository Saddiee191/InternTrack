package com.interntrack.opportunities;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

public class OpportunityRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Description is required")
    private String description;

    private String requiredSkillsJson;
    private String eligibleDegreesJson;
    private String eligibleBranchesJson;
    private BigDecimal minCgpa;
    private String eligibleBatchesJson;
    private String stipendSalary;
    private String location;
    private String workplaceType = "ON_SITE";
    private String employmentType = "INTERNSHIP";
    private LocalDate deadline;
    private String applyUrl;
    private String source = "CAMPUS";
    private String verificationStatus = "UNVERIFIED";
    private Boolean isActive = true;

    public OpportunityRequest() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredSkillsJson() {
        return requiredSkillsJson;
    }

    public void setRequiredSkillsJson(String requiredSkillsJson) {
        this.requiredSkillsJson = requiredSkillsJson;
    }

    public String getEligibleDegreesJson() {
        return eligibleDegreesJson;
    }

    public void setEligibleDegreesJson(String eligibleDegreesJson) {
        this.eligibleDegreesJson = eligibleDegreesJson;
    }

    public String getEligibleBranchesJson() {
        return eligibleBranchesJson;
    }

    public void setEligibleBranchesJson(String eligibleBranchesJson) {
        this.eligibleBranchesJson = eligibleBranchesJson;
    }

    public BigDecimal getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(BigDecimal minCgpa) {
        this.minCgpa = minCgpa;
    }

    public String getEligibleBatchesJson() {
        return eligibleBatchesJson;
    }

    public void setEligibleBatchesJson(String eligibleBatchesJson) {
        this.eligibleBatchesJson = eligibleBatchesJson;
    }

    public String getStipendSalary() {
        return stipendSalary;
    }

    public void setStipendSalary(String stipendSalary) {
        this.stipendSalary = stipendSalary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWorkplaceType() {
        return workplaceType;
    }

    public void setWorkplaceType(String workplaceType) {
        this.workplaceType = workplaceType;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getApplyUrl() {
        return applyUrl;
    }

    public void setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
