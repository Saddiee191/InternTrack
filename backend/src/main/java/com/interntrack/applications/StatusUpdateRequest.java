package com.interntrack.applications;

import jakarta.validation.constraints.NotBlank;

public class StatusUpdateRequest {
    @NotBlank(message = "Status is required")
    private String status;
    private String notes;

    public StatusUpdateRequest() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
