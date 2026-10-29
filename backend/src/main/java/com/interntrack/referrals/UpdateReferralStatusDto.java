package com.interntrack.referrals;

import jakarta.validation.constraints.NotBlank;

public class UpdateReferralStatusDto {
    @NotBlank(message = "Status is required")
    private String status; // ACCEPTED, DECLINED, CLOSED

    private String mentorResponse;

    public UpdateReferralStatusDto() {}

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
}
