package com.interntrack.admin;

public class AdminDashboardMetricsDto {
    private int totalStudents;
    private int totalMentors;
    private int totalActiveOpportunities;
    private int totalApplicationsRecorded;
    private int pendingContentReports;
    private int pendingOpportunityReports;
    private int reportedScamScans;

    public AdminDashboardMetricsDto() {}

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getTotalMentors() {
        return totalMentors;
    }

    public void setTotalMentors(int totalMentors) {
        this.totalMentors = totalMentors;
    }

    public int getTotalActiveOpportunities() {
        return totalActiveOpportunities;
    }

    public void setTotalActiveOpportunities(int totalActiveOpportunities) {
        this.totalActiveOpportunities = totalActiveOpportunities;
    }

    public int getTotalApplicationsRecorded() {
        return totalApplicationsRecorded;
    }

    public void setTotalApplicationsRecorded(int totalApplicationsRecorded) {
        this.totalApplicationsRecorded = totalApplicationsRecorded;
    }

    public int getPendingContentReports() {
        return pendingContentReports;
    }

    public void setPendingContentReports(int pendingContentReports) {
        this.pendingContentReports = pendingContentReports;
    }

    public int getPendingOpportunityReports() {
        return pendingOpportunityReports;
    }

    public void setPendingOpportunityReports(int pendingOpportunityReports) {
        this.pendingOpportunityReports = pendingOpportunityReports;
    }

    public int getReportedScamScans() {
        return reportedScamScans;
    }

    public void setReportedScamScans(int reportedScamScans) {
        this.reportedScamScans = reportedScamScans;
    }
}
