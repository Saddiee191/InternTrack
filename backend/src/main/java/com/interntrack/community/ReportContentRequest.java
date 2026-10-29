package com.interntrack.community;

import jakarta.validation.constraints.NotBlank;

public class ReportContentRequest {
    @NotBlank(message = "Reason is required")
    private String reason;

    public ReportContentRequest() {}

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
