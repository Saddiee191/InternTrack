package com.interntrack.ai;

import jakarta.validation.constraints.NotBlank;

public class ScamScanInput {
    private String companyName;
    private String jobTitle;
    private String recruiterContact;
    private String applicationUrl;
    private String stipendSalary;

    @NotBlank(message = "Job description or recruiter communication text is required")
    private String jobDescription;
    private String recruiterMessage;

    public ScamScanInput() {}

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getRecruiterContact() {
        return recruiterContact;
    }

    public void setRecruiterContact(String recruiterContact) {
        this.recruiterContact = recruiterContact;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public String getStipendSalary() {
        return stipendSalary;
    }

    public void setStipendSalary(String stipendSalary) {
        this.stipendSalary = stipendSalary;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getRecruiterMessage() {
        return recruiterMessage;
    }

    public void setRecruiterMessage(String recruiterMessage) {
        this.recruiterMessage = recruiterMessage;
    }
}
