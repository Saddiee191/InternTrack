package com.interntrack.referrals;

import java.time.LocalDateTime;

public class ReferralRequest {
    private Long id;
    private Long studentId;
    private Long mentorId;
    private String opportunityTitle;
    private String companyName;
    private Long resumeFileId;
    private String note;
    private String status; // PENDING, ACCEPTED, DECLINED, CLOSED
    private String mentorResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Joined student info
    private String studentName;
    private String studentEmail;
    private String studentAvatarUrl;

    // Joined mentor info
    private String mentorName;
    private String mentorCompany;
    private String mentorRole;

    // Joined resume info
    private String resumeFileName;

    public ReferralRequest() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public String getOpportunityTitle() {
        return opportunityTitle;
    }

    public void setOpportunityTitle(String opportunityTitle) {
        this.opportunityTitle = opportunityTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getResumeFileId() {
        return resumeFileId;
    }

    public void setResumeFileId(Long resumeFileId) {
        this.resumeFileId = resumeFileId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMentorResponse() {
        return mentorResponse;
    }

    public void setMentorResponse(String mentorResponse) {
        this.mentorResponse = mentorResponse;
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentAvatarUrl() {
        return studentAvatarUrl;
    }

    public void setStudentAvatarUrl(String studentAvatarUrl) {
        this.studentAvatarUrl = studentAvatarUrl;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getMentorCompany() {
        return mentorCompany;
    }

    public void setMentorCompany(String mentorCompany) {
        this.mentorCompany = mentorCompany;
    }

    public String getMentorRole() {
        return mentorRole;
    }

    public void setMentorRole(String mentorRole) {
        this.mentorRole = mentorRole;
    }

    public String getResumeFileName() {
        return resumeFileName;
    }

    public void setResumeFileName(String resumeFileName) {
        this.resumeFileName = resumeFileName;
    }
}
