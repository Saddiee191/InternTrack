package com.interntrack.applications;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class ApplicationDao {

    private final JdbcTemplate jdbcTemplate;

    public ApplicationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Application> applicationRowMapper = (rs, rowNum) -> {
        Application app = new Application();
        app.setId(rs.getLong("id"));
        app.setUserId(rs.getLong("user_id"));
        long oppId = rs.getLong("opportunity_id");
        if (!rs.wasNull()) {
            app.setOpportunityId(oppId);
        }
        app.setCompanyName(rs.getString("company_name"));
        app.setRole(rs.getString("role"));
        app.setJobDescription(rs.getString("job_description"));
        app.setSourceUrl(rs.getString("source_url"));
        app.setStipendSalary(rs.getString("stipend_salary"));
        app.setLocation(rs.getString("location"));
        app.setEmploymentType(rs.getString("employment_type"));
        app.setStatus(rs.getString("status"));

        Date appliedDate = rs.getDate("applied_date");
        if (appliedDate != null) app.setAppliedDate(appliedDate.toLocalDate());

        Date deadline = rs.getDate("deadline");
        if (deadline != null) app.setDeadline(deadline.toLocalDate());

        Timestamp interviewDate = rs.getTimestamp("interview_date");
        if (interviewDate != null) app.setInterviewDate(interviewDate.toLocalDateTime());

        Timestamp assessmentDate = rs.getTimestamp("assessment_date");
        if (assessmentDate != null) app.setAssessmentDate(assessmentDate.toLocalDateTime());

        Date followUpDate = rs.getDate("follow_up_date");
        if (followUpDate != null) app.setFollowUpDate(followUpDate.toLocalDate());

        app.setNotes(rs.getString("notes"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) app.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) app.setUpdatedAt(updatedAt.toLocalDateTime());

        return app;
    };

    private final RowMapper<ApplicationStatusHistory> historyRowMapper = (rs, rowNum) -> {
        ApplicationStatusHistory h = new ApplicationStatusHistory();
        h.setId(rs.getLong("id"));
        h.setApplicationId(rs.getLong("application_id"));
        h.setPreviousStatus(rs.getString("previous_status"));
        h.setNewStatus(rs.getString("new_status"));
        h.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("changed_at");
        if (ts != null) h.setChangedAt(ts.toLocalDateTime());
        return h;
    };

    private final RowMapper<ApplicationReminder> reminderRowMapper = (rs, rowNum) -> {
        ApplicationReminder r = new ApplicationReminder();
        r.setId(rs.getLong("id"));
        r.setUserId(rs.getLong("user_id"));
        r.setApplicationId(rs.getLong("application_id"));
        r.setReminderType(rs.getString("reminder_type"));
        Timestamp date = rs.getTimestamp("reminder_date");
        if (date != null) r.setReminderDate(date.toLocalDateTime());
        r.setNotes(rs.getString("notes"));
        r.setIsDismissed(rs.getBoolean("is_dismissed"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) r.setCreatedAt(created.toLocalDateTime());

        try {
            r.setCompanyName(rs.getString("company_name"));
            r.setRole(rs.getString("role"));
        } catch (Exception ignored) {}

        return r;
    };

    public List<Application> findByUserId(Long userId, String status, String search, String sortBy) {
        StringBuilder sql = new StringBuilder("SELECT * FROM applications WHERE user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND status = ? ");
            params.add(status.toUpperCase());
        }

        if (search != null && !search.isBlank()) {
            sql.append("AND (company_name LIKE ? OR role LIKE ? OR location LIKE ?) ");
            String pattern = "%" + search + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if ("deadline".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY deadline ASC, id DESC");
        } else if ("company".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY company_name ASC, id DESC");
        } else if ("appliedDate".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY applied_date DESC, id DESC");
        } else {
            sql.append("ORDER BY updated_at DESC, id DESC");
        }

        return jdbcTemplate.query(sql.toString(), applicationRowMapper, params.toArray());
    }

    public Optional<Application> findByIdAndUserId(Long id, Long userId) {
        String sql = "SELECT * FROM applications WHERE id = ? AND user_id = ?";
        try {
            Application app = jdbcTemplate.queryForObject(sql, applicationRowMapper, id, userId);
            return Optional.ofNullable(app);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Long create(Long userId, ApplicationRequest req) {
        String sql = "INSERT INTO applications (user_id, opportunity_id, company_name, role, job_description, " +
                "source_url, stipend_salary, location, employment_type, status, applied_date, deadline, " +
                "interview_date, assessment_date, follow_up_date, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            if (req.getOpportunityId() != null) ps.setLong(2, req.getOpportunityId()); else ps.setNull(2, java.sql.Types.BIGINT);
            ps.setString(3, req.getCompanyName().trim());
            ps.setString(4, req.getRole().trim());
            ps.setString(5, req.getJobDescription());
            ps.setString(6, req.getSourceUrl());
            ps.setString(7, req.getStipendSalary());
            ps.setString(8, req.getLocation());
            ps.setString(9, req.getEmploymentType() != null ? req.getEmploymentType() : "INTERNSHIP");
            ps.setString(10, req.getStatus() != null ? req.getStatus().toUpperCase() : "SAVED");
            ps.setDate(11, req.getAppliedDate() != null ? Date.valueOf(req.getAppliedDate()) : null);
            ps.setDate(12, req.getDeadline() != null ? Date.valueOf(req.getDeadline()) : null);
            ps.setTimestamp(13, req.getInterviewDate() != null ? Timestamp.valueOf(req.getInterviewDate()) : null);
            ps.setTimestamp(14, req.getAssessmentDate() != null ? Timestamp.valueOf(req.getAssessmentDate()) : null);
            ps.setDate(15, req.getFollowUpDate() != null ? Date.valueOf(req.getFollowUpDate()) : null);
            ps.setString(16, req.getNotes());
            return ps;
        }, keyHolder);

        Long newId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        if (newId != null) {
            recordStatusChange(newId, null, req.getStatus() != null ? req.getStatus().toUpperCase() : "SAVED", "Initial application creation");
        }
        return newId;
    }

    public int update(Long id, Long userId, ApplicationRequest req) {
        String sql = "UPDATE applications SET company_name = ?, role = ?, job_description = ?, source_url = ?, " +
                "stipend_salary = ?, location = ?, employment_type = ?, status = ?, applied_date = ?, deadline = ?, " +
                "interview_date = ?, assessment_date = ?, follow_up_date = ?, notes = ? " +
                "WHERE id = ? AND user_id = ?";

        return jdbcTemplate.update(sql,
                req.getCompanyName().trim(),
                req.getRole().trim(),
                req.getJobDescription(),
                req.getSourceUrl(),
                req.getStipendSalary(),
                req.getLocation(),
                req.getEmploymentType(),
                req.getStatus().toUpperCase(),
                req.getAppliedDate() != null ? Date.valueOf(req.getAppliedDate()) : null,
                req.getDeadline() != null ? Date.valueOf(req.getDeadline()) : null,
                req.getInterviewDate() != null ? Timestamp.valueOf(req.getInterviewDate()) : null,
                req.getAssessmentDate() != null ? Timestamp.valueOf(req.getAssessmentDate()) : null,
                req.getFollowUpDate() != null ? Date.valueOf(req.getFollowUpDate()) : null,
                req.getNotes(),
                id,
                userId
        );
    }

    public int updateStatus(Long id, Long userId, String newStatus, String notes) {
        Optional<Application> existing = findByIdAndUserId(id, userId);
        if (existing.isEmpty()) return 0;

        String prevStatus = existing.get().getStatus();
        String sql = "UPDATE applications SET status = ? WHERE id = ? AND user_id = ?";
        int updated = jdbcTemplate.update(sql, newStatus.toUpperCase(), id, userId);

        if (updated > 0) {
            recordStatusChange(id, prevStatus, newStatus.toUpperCase(), notes);
        }
        return updated;
    }

    public int delete(Long id, Long userId) {
        String sql = "DELETE FROM applications WHERE id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, id, userId);
    }

    public void recordStatusChange(Long applicationId, String prevStatus, String newStatus, String notes) {
        String sql = "INSERT INTO application_status_history (application_id, previous_status, new_status, notes) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, applicationId, prevStatus, newStatus, notes);
    }

    public List<ApplicationStatusHistory> getStatusHistory(Long applicationId) {
        String sql = "SELECT * FROM application_status_history WHERE application_id = ? ORDER BY changed_at DESC, id DESC";
        return jdbcTemplate.query(sql, historyRowMapper, applicationId);
    }

    public List<ApplicationReminder> getReminders(Long userId) {
        String sql = "SELECT r.*, a.company_name, a.role FROM application_reminders r " +
                "JOIN applications a ON r.application_id = a.id " +
                "WHERE r.user_id = ? AND r.is_dismissed = FALSE " +
                "ORDER BY r.reminder_date ASC";
        return jdbcTemplate.query(sql, reminderRowMapper, userId);
    }

    public Long createReminder(Long userId, ReminderRequest req) {
        String sql = "INSERT INTO application_reminders (user_id, application_id, reminder_type, reminder_date, notes) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setLong(2, req.getApplicationId());
            ps.setString(3, req.getReminderType().toUpperCase());
            ps.setTimestamp(4, Timestamp.valueOf(req.getReminderDate()));
            ps.setString(5, req.getNotes());
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public int dismissReminder(Long id, Long userId) {
        String sql = "UPDATE application_reminders SET is_dismissed = TRUE WHERE id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, id, userId);
    }

    public ApplicationStatsDto getStats(Long userId) {
        ApplicationStatsDto stats = new ApplicationStatsDto();

        // 1. Total count
        String totalSql = "SELECT COUNT(*) FROM applications WHERE user_id = ?";
        Integer total = jdbcTemplate.queryForObject(totalSql, Integer.class, userId);
        stats.setTotalApplications(total != null ? total : 0);

        // 2. Status distribution
        String distSql = "SELECT status, COUNT(*) as cnt FROM applications WHERE user_id = ? GROUP BY status";
        Map<String, Integer> distribution = new LinkedHashMap<>();
        for (String st : List.of("SAVED", "APPLIED", "ASSESSMENT", "INTERVIEW", "OFFER_RECEIVED", "REJECTED", "WITHDRAWN")) {
            distribution.put(st, 0);
        }
        jdbcTemplate.query(distSql, rs -> {
            distribution.put(rs.getString("status"), rs.getInt("cnt"));
        }, userId);
        stats.setStatusDistribution(distribution);

        // 3. Computed buckets
        int inProgress = distribution.getOrDefault("APPLIED", 0) +
                distribution.getOrDefault("ASSESSMENT", 0) +
                distribution.getOrDefault("INTERVIEW", 0);
        stats.setInProgressCount(inProgress);
        stats.setAssessmentsScheduled(distribution.getOrDefault("ASSESSMENT", 0));
        stats.setInterviewsScheduled(distribution.getOrDefault("INTERVIEW", 0));
        stats.setOffersReceived(distribution.getOrDefault("OFFER_RECEIVED", 0));

        // 4. Upcoming Deadlines (within 14 days)
        String deadlineSql = "SELECT COUNT(*) FROM applications WHERE user_id = ? AND deadline >= CURRENT_DATE AND deadline <= DATE_ADD(CURRENT_DATE, INTERVAL 14 DAY) AND status NOT IN ('OFFER_RECEIVED', 'REJECTED', 'WITHDRAWN')";
        Integer upcoming = jdbcTemplate.queryForObject(deadlineSql, Integer.class, userId);
        stats.setUpcomingDeadlines(upcoming != null ? upcoming : 0);

        // 5. Conversion Rate (Offers / (Interviews + Offers)) * 100
        int offers = distribution.getOrDefault("OFFER_RECEIVED", 0);
        int interviews = distribution.getOrDefault("INTERVIEW", 0);
        if (offers + interviews > 0) {
            double rate = ((double) offers / (offers + interviews)) * 100.0;
            stats.setConversionRate(Math.round(rate * 10.0) / 10.0);
        } else {
            stats.setConversionRate(0.0);
        }

        // 6. Applications over time (grouped by month)
        String timeSql = "SELECT DATE_FORMAT(COALESCE(applied_date, created_at), '%Y-%m') as m, COUNT(*) as cnt " +
                "FROM applications WHERE user_id = ? " +
                "GROUP BY m ORDER BY m ASC LIMIT 12";
        List<Map<String, Object>> timeData = new ArrayList<>();
        jdbcTemplate.query(timeSql, rs -> {
            Map<String, Object> point = new HashMap<>();
            point.put("month", rs.getString("m"));
            point.put("count", rs.getInt("cnt"));
            timeData.add(point);
        }, userId);
        stats.setApplicationsOverTime(timeData);

        return stats;
    }
}
