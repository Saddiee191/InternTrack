package com.interntrack.referrals;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class ReferralDao {

    private final JdbcTemplate jdbcTemplate;

    public ReferralDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ReferralRequest> referralRowMapper = (rs, rowNum) -> {
        ReferralRequest r = new ReferralRequest();
        r.setId(rs.getLong("id"));
        r.setStudentId(rs.getLong("student_id"));
        r.setMentorId(rs.getLong("mentor_id"));
        r.setOpportunityTitle(rs.getString("opportunity_title"));
        r.setCompanyName(rs.getString("company_name"));
        long fileId = rs.getLong("resume_file_id");
        if (!rs.wasNull()) r.setResumeFileId(fileId);
        r.setNote(rs.getString("note"));
        r.setStatus(rs.getString("status"));
        r.setMentorResponse(rs.getString("mentor_response"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) r.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) r.setUpdatedAt(updatedAt.toLocalDateTime());

        try {
            r.setStudentName(rs.getString("student_name"));
            r.setStudentEmail(rs.getString("student_email"));
            r.setStudentAvatarUrl(rs.getString("student_avatar_url"));
        } catch (Exception ignored) {}

        try {
            r.setMentorName(rs.getString("mentor_name"));
            r.setMentorCompany(rs.getString("mentor_company"));
            r.setMentorRole(rs.getString("mentor_role"));
        } catch (Exception ignored) {}

        try {
            r.setResumeFileName(rs.getString("resume_file_name"));
        } catch (Exception ignored) {}

        return r;
    };

    public Long createRequest(Long studentId, CreateReferralRequestDto req) {
        String sql = "INSERT INTO referral_requests (student_id, mentor_id, opportunity_title, company_name, resume_file_id, note, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'PENDING')";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, studentId);
            ps.setLong(2, req.getMentorId());
            ps.setString(3, req.getOpportunityTitle().trim());
            ps.setString(4, req.getCompanyName().trim());
            if (req.getResumeFileId() != null) ps.setLong(5, req.getResumeFileId()); else ps.setNull(5, java.sql.Types.BIGINT);
            ps.setString(6, req.getNote());
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public Optional<ReferralRequest> findById(Long id) {
        String sql = "SELECT rr.*, " +
                "su.full_name as student_name, su.email as student_email, su.avatar_url as student_avatar_url, " +
                "mu.full_name as mentor_name, mp.current_company as mentor_company, mp.current_role as mentor_role, " +
                "rf.file_name as resume_file_name " +
                "FROM referral_requests rr " +
                "JOIN users su ON rr.student_id = su.id " +
                "JOIN users mu ON rr.mentor_id = mu.id " +
                "LEFT JOIN mentor_profiles mp ON mu.id = mp.user_id " +
                "LEFT JOIN resume_files rf ON rr.resume_file_id = rf.id " +
                "WHERE rr.id = ?";
        try {
            ReferralRequest req = jdbcTemplate.queryForObject(sql, referralRowMapper, id);
            return Optional.ofNullable(req);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<ReferralRequest> findByStudentId(Long studentId) {
        String sql = "SELECT rr.*, " +
                "su.full_name as student_name, su.email as student_email, su.avatar_url as student_avatar_url, " +
                "mu.full_name as mentor_name, mp.current_company as mentor_company, mp.current_role as mentor_role, " +
                "rf.file_name as resume_file_name " +
                "FROM referral_requests rr " +
                "JOIN users su ON rr.student_id = su.id " +
                "JOIN users mu ON rr.mentor_id = mu.id " +
                "LEFT JOIN mentor_profiles mp ON mu.id = mp.user_id " +
                "LEFT JOIN resume_files rf ON rr.resume_file_id = rf.id " +
                "WHERE rr.student_id = ? ORDER BY rr.created_at DESC";
        return jdbcTemplate.query(sql, referralRowMapper, studentId);
    }

    public List<ReferralRequest> findByMentorId(Long mentorId) {
        String sql = "SELECT rr.*, " +
                "su.full_name as student_name, su.email as student_email, su.avatar_url as student_avatar_url, " +
                "mu.full_name as mentor_name, mp.current_company as mentor_company, mp.current_role as mentor_role, " +
                "rf.file_name as resume_file_name " +
                "FROM referral_requests rr " +
                "JOIN users su ON rr.student_id = su.id " +
                "JOIN users mu ON rr.mentor_id = mu.id " +
                "LEFT JOIN mentor_profiles mp ON mu.id = mp.user_id " +
                "LEFT JOIN resume_files rf ON rr.resume_file_id = rf.id " +
                "WHERE rr.mentor_id = ? ORDER BY rr.created_at DESC";
        return jdbcTemplate.query(sql, referralRowMapper, mentorId);
    }

    public int updateStatus(Long id, Long mentorId, String status, String mentorResponse) {
        String sql = "UPDATE referral_requests SET status = ?, mentor_response = ? WHERE id = ? AND mentor_id = ?";
        return jdbcTemplate.update(sql, status.toUpperCase(), mentorResponse, id, mentorId);
    }
}
