package com.interntrack.opportunities;

import java.util.List;

public class EligibilityResultDto {
    private boolean eligible;
    private boolean cgpaEligible;
    private boolean degreeEligible;
    private boolean branchEligible;
    private boolean batchEligible;
    private List<String> passedCriteria;
    private List<String> failedCriteria;
    private String summary;

    public EligibilityResultDto() {}

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public boolean isCgpaEligible() {
        return cgpaEligible;
    }

    public void setCgpaEligible(boolean cgpaEligible) {
        this.cgpaEligible = cgpaEligible;
    }

    public boolean isDegreeEligible() {
        return degreeEligible;
    }

    public void setDegreeEligible(boolean degreeEligible) {
        this.degreeEligible = degreeEligible;
    }

    public boolean isBranchEligible() {
        return branchEligible;
    }

    public void setBranchEligible(boolean branchEligible) {
        this.branchEligible = branchEligible;
    }

    public boolean isBatchEligible() {
        return batchEligible;
    }

    public void setBatchEligible(boolean batchEligible) {
        this.batchEligible = batchEligible;
    }

    public List<String> getPassedCriteria() {
        return passedCriteria;
    }

    public void setPassedCriteria(List<String> passedCriteria) {
        this.passedCriteria = passedCriteria;
    }

    public List<String> getFailedCriteria() {
        return failedCriteria;
    }

    public void setFailedCriteria(List<String> failedCriteria) {
        this.failedCriteria = failedCriteria;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
