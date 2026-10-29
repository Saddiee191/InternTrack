package com.interntrack.admin;

import jakarta.validation.constraints.NotBlank;

public class ResolveReportRequest {
    @NotBlank(message = "Resolution status is required (RESOLVED or DISMISSED)")
    private String status; // RESOLVED, DISMISSED
    private String resolutionNotes;

    public ResolveReportRequest() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
}
