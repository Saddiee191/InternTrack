package com.interntrack.applications;

import java.util.Map;
import java.util.List;

public class ApplicationStatsDto {
    private int totalApplications;
    private int inProgressCount;
    private int assessmentsScheduled;
    private int interviewsScheduled;
    private int offersReceived;
    private int upcomingDeadlines;
    private double conversionRate; // interview to offer percentage
    private Map<String, Integer> statusDistribution;
    private List<Map<String, Object>> applicationsOverTime;

    public ApplicationStatsDto() {}

    public int getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(int totalApplications) {
        this.totalApplications = totalApplications;
    }

    public int getInProgressCount() {
        return inProgressCount;
    }

    public void setInProgressCount(int inProgressCount) {
        this.inProgressCount = inProgressCount;
    }

    public int getAssessmentsScheduled() {
        return assessmentsScheduled;
    }

    public void setAssessmentsScheduled(int assessmentsScheduled) {
        this.assessmentsScheduled = assessmentsScheduled;
    }

    public int getInterviewsScheduled() {
        return interviewsScheduled;
    }

    public void setInterviewsScheduled(int interviewsScheduled) {
        this.interviewsScheduled = interviewsScheduled;
    }

    public int getOffersReceived() {
        return offersReceived;
    }

    public void setOffersReceived(int offersReceived) {
        this.offersReceived = offersReceived;
    }

    public int getUpcomingDeadlines() {
        return upcomingDeadlines;
    }

    public void setUpcomingDeadlines(int upcomingDeadlines) {
        this.upcomingDeadlines = upcomingDeadlines;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public Map<String, Integer> getStatusDistribution() {
        return statusDistribution;
    }

    public void setStatusDistribution(Map<String, Integer> statusDistribution) {
        this.statusDistribution = statusDistribution;
    }

    public List<Map<String, Object>> getApplicationsOverTime() {
        return applicationsOverTime;
    }

    public void setApplicationsOverTime(List<Map<String, Object>> applicationsOverTime) {
        this.applicationsOverTime = applicationsOverTime;
    }
}
