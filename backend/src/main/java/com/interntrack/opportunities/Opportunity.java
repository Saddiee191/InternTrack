package com.interntrack.opportunities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Opportunity {
    private Long id;
    private String title;
    private String companyName;
    private String description;
    private String requiredSkillsJson;
    private String eligibleDegreesJson;
    private String eligibleBranchesJson;
    private BigDecimal minCgpa;
    private String eligibleBatchesJson;
    private String stipendSalary;
    private String location;
    private String workplaceType; // ON_SITE, HYBRID, REMOTE
    private String employmentType; // INTERNSHIP, FULL_TIME, CO_OP
    private LocalDate deadline;
    private String applyUrl;
    private String source;
    private String verificationStatus; // VERIFIED, EXTERNAL, UNVERIFIED
    private Boolean isActive;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient attributes for authenticated student
    private Boolean isSaved = false;
    private Boolean hasApplied = false;

    public Opportunity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(Boolean saved) {
        isSaved = saved;
    }

    public Boolean getHasApplied() {
        return hasApplied;
    }

    public void setHasApplied(Boolean hasApplied) {
        this.hasApplied = hasApplied;
    }
}
