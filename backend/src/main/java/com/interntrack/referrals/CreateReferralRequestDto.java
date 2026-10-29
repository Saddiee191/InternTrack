package com.interntrack.referrals;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateReferralRequestDto {
    @NotNull(message = "Mentor ID is required")
    private Long mentorId;

    @NotBlank(message = "Opportunity title is required")
    private String opportunityTitle;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private Long resumeFileId;

    @NotBlank(message = "Introductory note is required")
    private String note;

    public CreateReferralRequestDto() {}

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
}
