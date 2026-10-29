package com.interntrack.admin;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class AdminDao {

    private final JdbcTemplate jdbcTemplate;

    public AdminDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ContentReport> contentReportRowMapper = (rs, rowNum) -> {
        ContentReport r = new ContentReport();
        r.setId(rs.getLong("id"));
        r.setReporterId(rs.getLong("reporter_id"));
        r.setTargetType(rs.getString("target_type"));
        r.setTargetId(rs.getLong("target_id"));
        r.setReason(rs.getString("reason"));
        r.setStatus(rs.getString("status"));
        r.setResolutionNotes(rs.getString("resolution_notes"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) r.setCreatedAt(ts.toLocalDateTime());

        try {
            r.setReporterName(rs.getString("reporter_name"));
            r.setReporterEmail(rs.getString("reporter_email"));
        } catch (Exception ignored) {}

        try {
            r.setTargetTitle(rs.getString("target_title"));
        } catch (Exception ignored) {}

        return r;
    };

    private final RowMapper<OpportunityReport> opportunityReportRowMapper = (rs, rowNum) -> {
        OpportunityReport r = new OpportunityReport();
        r.setId(rs.getLong("id"));
        r.setOpportunityId(rs.getLong("opportunity_id"));
        r.setReporterId(rs.getLong("reporter_id"));
        r.setReason(rs.getString("reason"));
        r.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) r.setCreatedAt(ts.toLocalDateTime());

        try {
            r.setOpportunityTitle(rs.getString("opportunity_title"));
            r.setCompanyName(rs.getString("company_name"));
            r.setReporterName(rs.getString("reporter_name"));
            r.setReporterEmail(rs.getString("reporter_email"));
        } catch (Exception ignored) {}

        return r;
    };

    private final RowMapper<AuditLog> auditLogRowMapper = (rs, rowNum) -> {
        AuditLog a = new AuditLog();
        a.setId(rs.getLong("id"));
        a.setAdminId(rs.getLong("admin_id"));
        a.setAction(rs.getString("action"));
        a.setTargetType(rs.getString("target_type"));
        long targetId = rs.getLong("target_id");
        if (!rs.wasNull()) a.setTargetId(targetId);
        a.setDetails(rs.getString("details"));
        a.setIpAddress(rs.getString("ip_address"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) a.setCreatedAt(ts.toLocalDateTime());

        try {
            a.setAdminName(rs.getString("admin_name"));
            a.setAdminEmail(rs.getString("admin_email"));
        } catch (Exception ignored) {}

        return a;
    };

    public AdminDashboardMetricsDto getMetrics() {
        AdminDashboardMetricsDto dto = new AdminDashboardMetricsDto();

        Integer students = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE role = 'STUDENT' AND status != 'DELETED'", Integer.class);
        dto.setTotalStudents(students != null ? students : 0);

        Integer mentors = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE role = 'MENTOR' AND status != 'DELETED'", Integer.class);
        dto.setTotalMentors(mentors != null ? mentors : 0);

        Integer opps = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM opportunities WHERE is_active = TRUE", Integer.class);
        dto.setTotalActiveOpportunities(opps != null ? opps : 0);

        Integer apps = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM applications", Integer.class);
        dto.setTotalApplicationsRecorded(apps != null ? apps : 0);

        Integer contentReports = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM content_reports WHERE status = 'PENDING'", Integer.class);
        dto.setPendingContentReports(contentReports != null ? contentReports : 0);

        Integer oppReports = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM opportunity_reports WHERE status = 'PENDING'", Integer.class);
        dto.setPendingOpportunityReports(oppReports != null ? oppReports : 0);

        Integer scamScans = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM scam_scans WHERE is_reported = TRUE", Integer.class);
        dto.setReportedScamScans(scamScans != null ? scamScans : 0);

        return dto;
    }

    public List<ContentReport> findAllContentReports() {
        String sql = "SELECT cr.*, u.full_name as reporter_name, u.email as reporter_email, " +
                "p.title as target_title " +
                "FROM content_reports cr " +
                "JOIN users u ON cr.reporter_id = u.id " +
                "LEFT JOIN community_posts p ON cr.target_type = 'POST' AND cr.target_id = p.id " +
                "ORDER BY cr.created_at DESC";
        return jdbcTemplate.query(sql, contentReportRowMapper);
    }

    public int resolveContentReport(Long id, String status, String notes) {
        String sql = "UPDATE content_reports SET status = ?, resolution_notes = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status.toUpperCase(), notes, id);
    }

    public List<OpportunityReport> findAllOpportunityReports() {
        String sql = "SELECT orp.*, u.full_name as reporter_name, u.email as reporter_email, " +
                "o.title as opportunity_title, o.company_name " +
                "FROM opportunity_reports orp " +
                "JOIN users u ON orp.reporter_id = u.id " +
                "JOIN opportunities o ON orp.opportunity_id = o.id " +
                "ORDER BY orp.created_at DESC";
        return jdbcTemplate.query(sql, opportunityReportRowMapper);
    }

    public int resolveOpportunityReport(Long id, String status) {
        String sql = "UPDATE opportunity_reports SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status.toUpperCase(), id);
    }

    public void logAudit(Long adminId, String action, String targetType, Long targetId, String details, String ipAddress) {
        String sql = "INSERT INTO administrator_audit_logs (admin_id, action, target_type, target_id, details, ip_address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, adminId, action, targetType, targetId, details, ipAddress);
    }

    public List<AuditLog> findAllAuditLogs(int limit, int offset) {
        String sql = "SELECT al.*, u.full_name as admin_name, u.email as admin_email " +
                "FROM administrator_audit_logs al " +
                "JOIN users u ON al.admin_id = u.id " +
                "ORDER BY al.created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, auditLogRowMapper, limit, offset);
    }
}
