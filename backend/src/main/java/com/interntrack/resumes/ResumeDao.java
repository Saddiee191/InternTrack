package com.interntrack.resumes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interntrack.ai.ResumeAnalysisResult;
import com.interntrack.ai.ResumeFinding;
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
public class ResumeDao {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public ResumeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }

    private final RowMapper<ResumeFile> fileRowMapper = (rs, rowNum) -> {
        ResumeFile rf = new ResumeFile();
        rf.setId(rs.getLong("id"));
        rf.setUserId(rs.getLong("user_id"));
        rf.setFileName(rs.getString("file_name"));
        rf.setStoragePath(rs.getString("storage_path"));
        rf.setFileSize(rs.getLong("file_size"));
        rf.setMimeType(rs.getString("mime_type"));
        rf.setExtractedText(rs.getString("extracted_text"));
        Timestamp ts = rs.getTimestamp("uploaded_at");
        if (ts != null) rf.setUploadedAt(ts.toLocalDateTime());
        return rf;
    };

    private final RowMapper<ResumeAnalysis> analysisRowMapper = (rs, rowNum) -> {
        ResumeAnalysis a = new ResumeAnalysis();
        a.setId(rs.getLong("id"));
        a.setUserId(rs.getLong("user_id"));
        a.setResumeFileId(rs.getLong("resume_file_id"));
        a.setJobTitle(rs.getString("job_title"));
        a.setJobDescription(rs.getString("job_description"));
        a.setCompletenessScore(rs.getInt("completeness_score"));
        a.setMatchPercentage(rs.getInt("match_percentage"));
        a.setMatchedSkillsJson(rs.getString("matched_skills_json"));
        a.setMissingSkillsJson(rs.getString("missing_skills_json"));
        a.setProviderType(rs.getString("provider_type"));
        a.setSummary(rs.getString("summary"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) a.setCreatedAt(ts.toLocalDateTime());

        try {
            a.setResumeFileName(rs.getString("file_name"));
        } catch (Exception ignored) {}

        return a;
    };

    private final RowMapper<ResumeAnalysisFinding> findingRowMapper = (rs, rowNum) -> {
        ResumeAnalysisFinding f = new ResumeAnalysisFinding();
        f.setId(rs.getLong("id"));
        f.setAnalysisId(rs.getLong("analysis_id"));
        f.setSectionName(rs.getString("section_name"));
        f.setFindingType(rs.getString("finding_type"));
        f.setTitle(rs.getString("title"));
        f.setDescription(rs.getString("description"));
        f.setSuggestion(rs.getString("suggestion"));
        return f;
    };

    public Long saveResumeFile(Long userId, String fileName, String storagePath, long fileSize, String mimeType, String extractedText) {
        String sql = "INSERT INTO resume_files (user_id, file_name, storage_path, file_size, mime_type, extracted_text) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setString(2, fileName);
            ps.setString(3, storagePath);
            ps.setLong(4, fileSize);
            ps.setString(5, mimeType);
            ps.setString(6, extractedText);
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public Optional<ResumeFile> findResumeFileById(Long id, Long userId) {
        String sql = "SELECT * FROM resume_files WHERE id = ? AND user_id = ?";
        try {
            ResumeFile file = jdbcTemplate.queryForObject(sql, fileRowMapper, id, userId);
            return Optional.ofNullable(file);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<ResumeFile> findResumeFilesByUserId(Long userId) {
        return jdbcTemplate.query(
                "SELECT * FROM resume_files WHERE user_id = ? ORDER BY uploaded_at DESC",
                fileRowMapper,
                userId
        );
    }

    public int deleteResumeFile(Long id, Long userId) {
        return jdbcTemplate.update("DELETE FROM resume_files WHERE id = ? AND user_id = ?", id, userId);
    }

    public Optional<ResumeFile> findLatestResumeFileByUserId(Long userId) {
        String sql = "SELECT * FROM resume_files WHERE user_id = ? ORDER BY uploaded_at DESC LIMIT 1";
        try {
            ResumeFile file = jdbcTemplate.queryForObject(sql, fileRowMapper, userId);
            return Optional.ofNullable(file);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Long saveAnalysis(Long userId, Long resumeFileId, ResumeAnalysisResult res, String jobTitle, String jobDescription) {
        String matchedJson = "[]";
        String missingJson = "[]";
        try {
            matchedJson = objectMapper.writeValueAsString(res.getMatchedSkills() != null ? res.getMatchedSkills() : List.of());
            missingJson = objectMapper.writeValueAsString(res.getMissingSkills() != null ? res.getMissingSkills() : List.of());
        } catch (JsonProcessingException ignored) {}

        String sql = "INSERT INTO resume_analyses (user_id, resume_file_id, job_title, job_description, " +
                "completeness_score, match_percentage, matched_skills_json, missing_skills_json, provider_type, summary) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String finalMatchedJson = matchedJson;
        String finalMissingJson = missingJson;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setLong(2, resumeFileId);
            ps.setString(3, jobTitle);
            ps.setString(4, jobDescription);
            ps.setInt(5, res.getCompletenessScore());
            ps.setInt(6, res.getMatchPercentage());
            ps.setString(7, finalMatchedJson);
            ps.setString(8, finalMissingJson);
            ps.setString(9, res.getProviderType() != null ? res.getProviderType() : "RULE_BASED");
            ps.setString(10, res.getSummary());
            return ps;
        }, keyHolder);

        Long analysisId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;

        if (analysisId != null && res.getFindings() != null) {
            String findingSql = "INSERT INTO resume_analysis_findings (analysis_id, section_name, finding_type, title, description, suggestion) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            for (ResumeFinding f : res.getFindings()) {
                jdbcTemplate.update(findingSql,
                        analysisId,
                        f.getSectionName(),
                        f.getFindingType(),
                        f.getTitle(),
                        f.getDescription(),
                        f.getSuggestion()
                );
            }
        }

        return analysisId;
    }

    public Optional<ResumeAnalysis> findAnalysisById(Long id, Long userId) {
        String sql = "SELECT ra.*, rf.file_name FROM resume_analyses ra " +
                "JOIN resume_files rf ON ra.resume_file_id = rf.id " +
                "WHERE ra.id = ? AND ra.user_id = ?";
        try {
            ResumeAnalysis a = jdbcTemplate.queryForObject(sql, analysisRowMapper, id, userId);
            if (a != null) {
                a.setFindings(getFindingsByAnalysisId(a.getId()));
            }
            return Optional.ofNullable(a);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<ResumeAnalysis> findAnalysesByUserId(Long userId) {
        String sql = "SELECT ra.*, rf.file_name FROM resume_analyses ra " +
                "JOIN resume_files rf ON ra.resume_file_id = rf.id " +
                "WHERE ra.user_id = ? ORDER BY ra.created_at DESC";
        List<ResumeAnalysis> list = jdbcTemplate.query(sql, analysisRowMapper, userId);
        for (ResumeAnalysis a : list) {
            a.setFindings(getFindingsByAnalysisId(a.getId()));
        }
        return list;
    }

    public List<ResumeAnalysisFinding> getFindingsByAnalysisId(Long analysisId) {
        String sql = "SELECT * FROM resume_analysis_findings WHERE analysis_id = ? ORDER BY id ASC";
        return jdbcTemplate.query(sql, findingRowMapper, analysisId);
    }
}
