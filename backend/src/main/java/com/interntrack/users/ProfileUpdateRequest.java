package com.interntrack.users;

import java.math.BigDecimal;

public class ProfileUpdateRequest {
    private String fullName;
    private String avatarUrl;

    // Student fields
    private String collegeName;
    private String degree;
    private String branch;
    private Integer graduationYear;
    private BigDecimal cgpa;
    private String skillsJson;
    private String interestsJson;
    private String preferredRolesJson;
    private String preferredLocationsJson;
    private String portfolioUrl;
    private String githubUrl;
    private String linkedinUrl;

    // Mentor fields
    private Integer graduatingBatch;
    private String fieldOfWork;
    private String currentCompany;
    private String currentRole;
    private Integer yearsOfExperience;
    private String bio;
    private Boolean openForReferrals;

    public ProfileUpdateRequest() {}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public BigDecimal getCgpa() {
        return cgpa;
    }

    public void setCgpa(BigDecimal cgpa) {
        this.cgpa = cgpa;
    }

    public String getSkillsJson() {
        return skillsJson;
    }

    public void setSkillsJson(String skillsJson) {
        this.skillsJson = skillsJson;
    }

    public String getInterestsJson() {
        return interestsJson;
    }

    public void setInterestsJson(String interestsJson) {
        this.interestsJson = interestsJson;
    }

    public String getPreferredRolesJson() {
        return preferredRolesJson;
    }

    public void setPreferredRolesJson(String preferredRolesJson) {
        this.preferredRolesJson = preferredRolesJson;
    }

    public String getPreferredLocationsJson() {
        return preferredLocationsJson;
    }

    public void setPreferredLocationsJson(String preferredLocationsJson) {
        this.preferredLocationsJson = preferredLocationsJson;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public Integer getGraduatingBatch() {
        return graduatingBatch;
    }

    public void setGraduatingBatch(Integer graduatingBatch) {
        this.graduatingBatch = graduatingBatch;
    }

    public String getFieldOfWork() {
        return fieldOfWork;
    }

    public void setFieldOfWork(String fieldOfWork) {
        this.fieldOfWork = fieldOfWork;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Boolean getOpenForReferrals() {
        return openForReferrals;
    }

    public void setOpenForReferrals(Boolean openForReferrals) {
        this.openForReferrals = openForReferrals;
    }
}
