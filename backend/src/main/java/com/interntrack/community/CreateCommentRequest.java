package com.interntrack.community;

import jakarta.validation.constraints.NotBlank;

public class CreateCommentRequest {
    @NotBlank(message = "Comment text is required")
    private String content;

    public CreateCommentRequest() {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
