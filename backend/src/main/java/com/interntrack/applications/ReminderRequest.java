package com.interntrack.applications;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ReminderRequest {
    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotBlank(message = "Reminder type is required")
    private String reminderType; // ASSESSMENT, INTERVIEW, FOLLOW_UP, DEADLINE

    @NotNull(message = "Reminder date is required")
    private LocalDateTime reminderDate;

    private String notes;

    public ReminderRequest() {}

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public LocalDateTime getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDateTime reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
