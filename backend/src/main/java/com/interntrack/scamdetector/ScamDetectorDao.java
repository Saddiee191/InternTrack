package com.interntrack.scamdetector;

import com.interntrack.ai.ScamAnalysisResult;
import com.interntrack.ai.ScamFinding;
import com.interntrack.ai.ScamScanInput;
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
public class ScamDetectorDao {

    private final JdbcTemplate jdbcTemplate;

    public ScamDetectorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ScamScan> scanRowMapper = (rs, rowNum) -> {
        ScamScan s = new ScamScan();
        s.setId(rs.getLong("id"));
        s.setUserId(rs.getLong("user_id"));
        s.setCompanyName(rs.getString("company_name"));
        s.setJobTitle(rs.getString("job_title"));
        s.setRecruiterContact(rs.getString("recruiter_contact"));
        s.setApplicationUrl(rs.getString("application_url"));
        s.setStipendSalary(rs.getString("stipend_salary"));
        s.setJobDescription(rs.getString("job_description"));
        s.setRecruiterMessage(rs.getString("recruiter_message"));
        s.setOverallRiskLevel(rs.getString("overall_risk_level"));
        s.setRiskScore(rs.getInt("risk_score"));
        s.setProviderType(rs.getString("provider_type"));
        s.setIsReported(rs.getBoolean("is_reported"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) s.setCreatedAt(ts.toLocalDateTime());
        return s;
    };

    private final RowMapper<ScamScanFinding> findingRowMapper = (rs, rowNum) -> {
        ScamScanFinding f = new ScamScanFinding();
        f.setId(rs.getLong("id"));
        f.setScanId(rs.getLong("scan_id"));
        f.setIndicatorName(rs.getString("indicator_name"));
        f.setRiskWeight(rs.getInt("risk_weight"));
        f.setEvidenceSnippet(rs.getString("evidence_snippet"));
        f.setExplanation(rs.getString("explanation"));
        f.setRecommendation(rs.getString("recommendation"));
        return f;
    };

    public Long saveScan(Long userId, ScamScanInput input, ScamAnalysisResult result) {
        String sql = "INSERT INTO scam_scans (user_id, company_name, job_title, recruiter_contact, " +
                "application_url, stipend_salary, job_description, recruiter_message, overall_risk_level, risk_score, provider_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setString(2, input.getCompanyName());
            ps.setString(3, input.getJobTitle());
            ps.setString(4, input.getRecruiterContact());
            ps.setString(5, input.getApplicationUrl());
            ps.setString(6, input.getStipendSalary());
            ps.setString(7, input.getJobDescription());
            ps.setString(8, input.getRecruiterMessage());
            ps.setString(9, result.getOverallRiskLevel());
            ps.setInt(10, result.getRiskScore());
            ps.setString(11, result.getProviderType() != null ? result.getProviderType() : "RULE_BASED");
            return ps;
        }, keyHolder);

        Long scanId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;

        if (scanId != null && result.getFindings() != null) {
            String fSql = "INSERT INTO scam_scan_findings (scan_id, indicator_name, risk_weight, evidence_snippet, explanation, recommendation) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            for (ScamFinding f : result.getFindings()) {
                jdbcTemplate.update(fSql,
                        scanId,
                        f.getIndicatorName(),
                        f.getRiskWeight(),
                        f.getEvidenceSnippet(),
                        f.getExplanation(),
                        f.getRecommendation()
                );
            }
        }

        return scanId;
    }

    public Optional<ScamScan> findByIdAndUserId(Long id, Long userId) {
        String sql = "SELECT * FROM scam_scans WHERE id = ? AND user_id = ?";
        try {
            ScamScan scan = jdbcTemplate.queryForObject(sql, scanRowMapper, id, userId);
            if (scan != null) {
                scan.setFindings(getFindingsByScanId(scan.getId()));
            }
            return Optional.ofNullable(scan);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<ScamScan> findByUserId(Long userId) {
        String sql = "SELECT * FROM scam_scans WHERE user_id = ? ORDER BY created_at DESC";
        List<ScamScan> list = jdbcTemplate.query(sql, scanRowMapper, userId);
        for (ScamScan s : list) {
            s.setFindings(getFindingsByScanId(s.getId()));
        }
        return list;
    }

    public int markReported(Long id, Long userId) {
        String sql = "UPDATE scam_scans SET is_reported = TRUE WHERE id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, id, userId);
    }

    public List<ScamScan> findReportedScans() {
        String sql = "SELECT * FROM scam_scans WHERE is_reported = TRUE ORDER BY created_at DESC";
        List<ScamScan> list = jdbcTemplate.query(sql, scanRowMapper);
        for (ScamScan s : list) {
            s.setFindings(getFindingsByScanId(s.getId()));
        }
        return list;
    }

    public List<ScamScanFinding> getFindingsByScanId(Long scanId) {
        String sql = "SELECT * FROM scam_scan_findings WHERE scan_id = ? ORDER BY risk_weight DESC";
        return jdbcTemplate.query(sql, findingRowMapper, scanId);
    }

    public int countReportedScamScans() {
        String sql = "SELECT COUNT(*) FROM scam_scans WHERE is_reported = TRUE";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}
