package com.interntrack.opportunities;

import jakarta.validation.constraints.NotBlank;

public class OpportunityReportRequest {
    @NotBlank(message = "Reason for reporting is required")
    private String reason;

    public OpportunityReportRequest() {}

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
