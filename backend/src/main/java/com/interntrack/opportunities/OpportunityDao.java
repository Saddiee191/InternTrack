package com.interntrack.opportunities;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OpportunityDao {

    private final JdbcTemplate jdbcTemplate;

    public OpportunityDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Opportunity> opportunityRowMapper = (rs, rowNum) -> {
        Opportunity op = new Opportunity();
        op.setId(rs.getLong("id"));
        op.setTitle(rs.getString("title"));
        op.setCompanyName(rs.getString("company_name"));
        op.setDescription(rs.getString("description"));
        op.setRequiredSkillsJson(rs.getString("required_skills_json"));
        op.setEligibleDegreesJson(rs.getString("eligible_degrees_json"));
        op.setEligibleBranchesJson(rs.getString("eligible_branches_json"));
        op.setMinCgpa(rs.getBigDecimal("min_cgpa"));
        op.setEligibleBatchesJson(rs.getString("eligible_batches_json"));
        op.setStipendSalary(rs.getString("stipend_salary"));
        op.setLocation(rs.getString("location"));
        op.setWorkplaceType(rs.getString("workplace_type"));
        op.setEmploymentType(rs.getString("employment_type"));

        Date deadline = rs.getDate("deadline");
        if (deadline != null) op.setDeadline(deadline.toLocalDate());

        op.setApplyUrl(rs.getString("apply_url"));
        op.setSource(rs.getString("source"));
        op.setVerificationStatus(rs.getString("verification_status"));
        op.setIsActive(rs.getBoolean("is_active"));

        long createdBy = rs.getLong("created_by");
        if (!rs.wasNull()) op.setCreatedBy(createdBy);

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) op.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) op.setUpdatedAt(updatedAt.toLocalDateTime());

        try {
            op.setIsSaved(rs.getBoolean("is_saved"));
        } catch (Exception ignored) {}

        try {
            op.setHasApplied(rs.getBoolean("has_applied"));
        } catch (Exception ignored) {}

        return op;
    };

    public List<Opportunity> findAll(
            String search,
            String location,
            String workplaceType,
            String employmentType,
            String verificationStatus,
            Boolean onlyActive,
            Long userId,
            int limit,
            int offset
    ) {
        StringBuilder sql = new StringBuilder("SELECT o.*, ");
        if (userId != null) {
            sql.append("(CASE WHEN so.opportunity_id IS NOT NULL THEN TRUE ELSE FALSE END) AS is_saved, ");
            sql.append("(CASE WHEN app.id IS NOT NULL THEN TRUE ELSE FALSE END) AS has_applied ");
        } else {
            sql.append("FALSE AS is_saved, FALSE AS has_applied ");
        }
        sql.append("FROM opportunities o ");

        if (userId != null) {
            sql.append("LEFT JOIN saved_opportunities so ON o.id = so.opportunity_id AND so.user_id = ? ");
            sql.append("LEFT JOIN applications app ON o.id = app.opportunity_id AND app.user_id = ? ");
        }

        sql.append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (userId != null) {
            params.add(userId);
            params.add(userId);
        }

        if (onlyActive != null && onlyActive) {
            sql.append("AND o.is_active = TRUE ");
        }

        if (search != null && !search.isBlank()) {
            sql.append("AND (o.title LIKE ? OR o.company_name LIKE ? OR o.description LIKE ? OR o.required_skills_json LIKE ?) ");
            String pattern = "%" + search + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (location != null && !location.isBlank()) {
            sql.append("AND o.location LIKE ? ");
            params.add("%" + location + "%");
        }

        if (workplaceType != null && !workplaceType.isBlank() && !"ALL".equalsIgnoreCase(workplaceType)) {
            sql.append("AND o.workplace_type = ? ");
            params.add(workplaceType.toUpperCase());
        }

        if (employmentType != null && !employmentType.isBlank() && !"ALL".equalsIgnoreCase(employmentType)) {
            sql.append("AND o.employment_type = ? ");
            params.add(employmentType.toUpperCase());
        }

        if (verificationStatus != null && !verificationStatus.isBlank() && !"ALL".equalsIgnoreCase(verificationStatus)) {
            sql.append("AND o.verification_status = ? ");
            params.add(verificationStatus.toUpperCase());
        }

        sql.append("ORDER BY o.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), opportunityRowMapper, params.toArray());
    }

    public Optional<Opportunity> findById(Long id, Long userId) {
        StringBuilder sql = new StringBuilder("SELECT o.*, ");
        if (userId != null) {
            sql.append("(CASE WHEN so.opportunity_id IS NOT NULL THEN TRUE ELSE FALSE END) AS is_saved, ");
            sql.append("(CASE WHEN app.id IS NOT NULL THEN TRUE ELSE FALSE END) AS has_applied ");
        } else {
            sql.append("FALSE AS is_saved, FALSE AS has_applied ");
        }
        sql.append("FROM opportunities o ");
        if (userId != null) {
            sql.append("LEFT JOIN saved_opportunities so ON o.id = so.opportunity_id AND so.user_id = ? ");
            sql.append("LEFT JOIN applications app ON o.id = app.opportunity_id AND app.user_id = ? ");
        }
        sql.append("WHERE o.id = ?");

        try {
            Opportunity op;
            if (userId != null) {
                op = jdbcTemplate.queryForObject(sql.toString(), opportunityRowMapper, userId, userId, id);
            } else {
                op = jdbcTemplate.queryForObject(sql.toString(), opportunityRowMapper, id);
            }
            return Optional.ofNullable(op);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Long create(OpportunityRequest req, Long createdBy) {
        String sql = "INSERT INTO opportunities (title, company_name, description, required_skills_json, " +
                "eligible_degrees_json, eligible_branches_json, min_cgpa, eligible_batches_json, stipend_salary, " +
                "location, workplace_type, employment_type, deadline, apply_url, source, verification_status, is_active, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, req.getTitle().trim());
            ps.setString(2, req.getCompanyName().trim());
            ps.setString(3, req.getDescription());
            ps.setString(4, req.getRequiredSkillsJson());
            ps.setString(5, req.getEligibleDegreesJson());
            ps.setString(6, req.getEligibleBranchesJson());
            ps.setBigDecimal(7, req.getMinCgpa() != null ? req.getMinCgpa() : java.math.BigDecimal.ZERO);
            ps.setString(8, req.getEligibleBatchesJson());
            ps.setString(9, req.getStipendSalary());
            ps.setString(10, req.getLocation());
            ps.setString(11, req.getWorkplaceType() != null ? req.getWorkplaceType() : "ON_SITE");
            ps.setString(12, req.getEmploymentType() != null ? req.getEmploymentType() : "INTERNSHIP");
            ps.setDate(13, req.getDeadline() != null ? Date.valueOf(req.getDeadline()) : null);
            ps.setString(14, req.getApplyUrl());
            ps.setString(15, req.getSource() != null ? req.getSource() : "CAMPUS");
            ps.setString(16, req.getVerificationStatus() != null ? req.getVerificationStatus() : "UNVERIFIED");
            ps.setBoolean(17, req.getIsActive() != null ? req.getIsActive() : true);
            if (createdBy != null) ps.setLong(18, createdBy); else ps.setNull(18, java.sql.Types.BIGINT);
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public int update(Long id, OpportunityRequest req) {
        String sql = "UPDATE opportunities SET title = ?, company_name = ?, description = ?, required_skills_json = ?, " +
                "eligible_degrees_json = ?, eligible_branches_json = ?, min_cgpa = ?, eligible_batches_json = ?, " +
                "stipend_salary = ?, location = ?, workplace_type = ?, employment_type = ?, deadline = ?, " +
                "apply_url = ?, source = ?, verification_status = ?, is_active = ? WHERE id = ?";

        return jdbcTemplate.update(sql,
                req.getTitle().trim(),
                req.getCompanyName().trim(),
                req.getDescription(),
                req.getRequiredSkillsJson(),
                req.getEligibleDegreesJson(),
                req.getEligibleBranchesJson(),
                req.getMinCgpa(),
                req.getEligibleBatchesJson(),
                req.getStipendSalary(),
                req.getLocation(),
                req.getWorkplaceType(),
                req.getEmploymentType(),
                req.getDeadline() != null ? Date.valueOf(req.getDeadline()) : null,
                req.getApplyUrl(),
                req.getSource(),
                req.getVerificationStatus(),
                req.getIsActive(),
                id
        );
    }

    public int delete(Long id) {
        String sql = "DELETE FROM opportunities WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int setVerificationStatus(Long id, String status) {
        String sql = "UPDATE opportunities SET verification_status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status.toUpperCase(), id);
    }

    public void saveOpportunity(Long userId, Long opportunityId) {
        String sql = "INSERT IGNORE INTO saved_opportunities (user_id, opportunity_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, opportunityId);
    }

    public void unsaveOpportunity(Long userId, Long opportunityId) {
        String sql = "DELETE FROM saved_opportunities WHERE user_id = ? AND opportunity_id = ?";
        jdbcTemplate.update(sql, userId, opportunityId);
    }

    public List<Opportunity> findSavedByUserId(Long userId) {
        String sql = "SELECT o.*, TRUE as is_saved, (CASE WHEN app.id IS NOT NULL THEN TRUE ELSE FALSE END) as has_applied " +
                "FROM saved_opportunities so " +
                "JOIN opportunities o ON so.opportunity_id = o.id " +
                "LEFT JOIN applications app ON o.id = app.opportunity_id AND app.user_id = ? " +
                "WHERE so.user_id = ? ORDER BY so.created_at DESC";
        return jdbcTemplate.query(sql, opportunityRowMapper, userId, userId);
    }

    public boolean hasApplied(Long userId, Long opportunityId) {
        String sql = "SELECT COUNT(*) FROM applications WHERE user_id = ? AND opportunity_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, opportunityId);
        return count != null && count > 0;
    }

    public void reportOpportunity(Long opportunityId, Long reporterId, String reason) {
        String sql = "INSERT INTO opportunity_reports (opportunity_id, reporter_id, reason) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, opportunityId, reporterId, reason);
    }

    public int countActiveOpportunities() {
        String sql = "SELECT COUNT(*) FROM opportunities WHERE is_active = TRUE";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}
