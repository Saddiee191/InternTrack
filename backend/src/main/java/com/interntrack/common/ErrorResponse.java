package com.interntrack.common;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {
    private boolean success;
    private String error;
    private List<String> details;
    private int status;
    private long timestamp;

    public ErrorResponse() {
        this.success = false;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public ErrorResponse(String error, int status) {
        this.success = false;
        this.error = error;
        this.status = status;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public ErrorResponse(String error, List<String> details, int status) {
        this.success = false;
        this.error = error;
        this.details = details;
        this.status = status;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
