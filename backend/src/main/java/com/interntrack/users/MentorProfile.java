package com.interntrack.users;

public class MentorProfile {
    private Long userId;
    private String collegeName;
    private Integer graduatingBatch;
    private String fieldOfWork;
    private String currentCompany;
    private String currentRole;
    private Integer yearsOfExperience;
    private String skillsJson;
    private String bio;
    private String linkedinUrl;
    private Boolean openForReferrals;
    private Boolean isVerifiedMentor;

    // Joined fields from users table
    private String fullName;
    private String email;
    private String avatarUrl;

    public MentorProfile() {}

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

    public String getSkillsJson() {
        return skillsJson;
    }

    public void setSkillsJson(String skillsJson) {
        this.skillsJson = skillsJson;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public Boolean getOpenForReferrals() {
        return openForReferrals;
    }

    public void setOpenForReferrals(Boolean openForReferrals) {
        this.openForReferrals = openForReferrals;
    }

    public Boolean getIsVerifiedMentor() {
        return isVerifiedMentor;
    }

    public void setIsVerifiedMentor(Boolean verifiedMentor) {
        isVerifiedMentor = verifiedMentor;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
