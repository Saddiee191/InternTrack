package com.interntrack.users;

import java.math.BigDecimal;

public class StudentProfile {
    private Long userId;
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
    private Integer profileCompletionPct;

    public StudentProfile() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Integer getProfileCompletionPct() {
        return profileCompletionPct;
    }

    public void setProfileCompletionPct(Integer profileCompletionPct) {
        this.profileCompletionPct = profileCompletionPct;
    }
}
