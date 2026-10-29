package com.interntrack.admin;

import com.interntrack.common.ResourceNotFoundException;
import com.interntrack.opportunities.OpportunityDao;
import com.interntrack.scamdetector.ScamDetectorDao;
import com.interntrack.scamdetector.ScamScan;
import com.interntrack.users.User;
import com.interntrack.users.UserDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final AdminDao adminDao;
    private final UserDao userDao;
    private final OpportunityDao opportunityDao;
    private final ScamDetectorDao scamDetectorDao;

    public AdminService(AdminDao adminDao, UserDao userDao, OpportunityDao opportunityDao, ScamDetectorDao scamDetectorDao) {
        this.adminDao = adminDao;
        this.userDao = userDao;
        this.opportunityDao = opportunityDao;
        this.scamDetectorDao = scamDetectorDao;
    }

    public AdminDashboardMetricsDto getMetrics() {
        return adminDao.getMetrics();
    }

    public List<ContentReport> getContentReports() {
        return adminDao.findAllContentReports();
    }

    @Transactional
    public void resolveContentReport(Long id, String status, String notes, Long adminId) {
        int rows = adminDao.resolveContentReport(id, status, notes);
        if (rows == 0) {
            throw new ResourceNotFoundException("Content report not found");
        }
        adminDao.logAudit(adminId, "RESOLVE_CONTENT_REPORT", "CONTENT_REPORT", id, "Status updated to " + status + ": " + notes, "127.0.0.1");
    }

    public List<OpportunityReport> getOpportunityReports() {
        return adminDao.findAllOpportunityReports();
    }

    @Transactional
    public void resolveOpportunityReport(Long id, String status, Long adminId) {
        int rows = adminDao.resolveOpportunityReport(id, status);
        if (rows == 0) {
            throw new ResourceNotFoundException("Opportunity report not found");
        }
        adminDao.logAudit(adminId, "RESOLVE_OPPORTUNITY_REPORT", "OPPORTUNITY_REPORT", id, "Status updated to " + status, "127.0.0.1");
    }

    public List<ScamScan> getReportedScamScans() {
        return scamDetectorDao.findReportedScans();
    }

    public List<User> getUsers(int limit, int offset, String search) {
        return userDao.findAllUsers(limit, offset, search);
    }

    @Transactional
    public void updateUserStatus(Long userId, String status, Long adminId) {
        int rows = userDao.updateStatus(userId, status.toUpperCase());
        if (rows == 0) {
            throw new ResourceNotFoundException("User not found");
        }
        adminDao.logAudit(adminId, "UPDATE_USER_STATUS", "USER", userId, "Account status changed to " + status, "127.0.0.1");
    }

    @Transactional
    public void setOpportunityVerification(Long opportunityId, String verificationStatus, Long adminId) {
        int rows = opportunityDao.setVerificationStatus(opportunityId, verificationStatus.toUpperCase());
        if (rows == 0) {
            throw new ResourceNotFoundException("Opportunity not found");
        }
        adminDao.logAudit(adminId, "SET_OPPORTUNITY_VERIFICATION", "OPPORTUNITY", opportunityId, "Verification status set to " + verificationStatus, "127.0.0.1");
    }

    public List<AuditLog> getAuditLogs(int limit, int offset) {
        return adminDao.findAllAuditLogs(limit, offset);
    }
}
