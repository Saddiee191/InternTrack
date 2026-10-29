package com.interntrack.users;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProfileDao {

    private final JdbcTemplate jdbcTemplate;

    public ProfileDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<StudentProfile> studentRowMapper = (rs, rowNum) -> {
        StudentProfile p = new StudentProfile();
        p.setUserId(rs.getLong("user_id"));
        p.setCollegeName(rs.getString("college_name"));
        p.setDegree(rs.getString("degree"));
        p.setBranch(rs.getString("branch"));
        int gradYear = rs.getInt("graduation_year");
        if (!rs.wasNull()) {
            p.setGraduationYear(gradYear);
        }
        p.setCgpa(rs.getBigDecimal("cgpa"));
        p.setSkillsJson(rs.getString("skills_json"));
        p.setInterestsJson(rs.getString("interests_json"));
        p.setPreferredRolesJson(rs.getString("preferred_roles_json"));
        p.setPreferredLocationsJson(rs.getString("preferred_locations_json"));
        p.setPortfolioUrl(rs.getString("portfolio_url"));
        p.setGithubUrl(rs.getString("github_url"));
        p.setLinkedinUrl(rs.getString("linkedin_url"));
        p.setProfileCompletionPct(rs.getInt("profile_completion_pct"));
        return p;
    };

    private final RowMapper<MentorProfile> mentorRowMapper = (rs, rowNum) -> {
        MentorProfile m = new MentorProfile();
        m.setUserId(rs.getLong("user_id"));
        m.setCollegeName(rs.getString("college_name"));
        int batch = rs.getInt("graduating_batch");
        if (!rs.wasNull()) {
            m.setGraduatingBatch(batch);
        }
        m.setFieldOfWork(rs.getString("field_of_work"));
        m.setCurrentCompany(rs.getString("current_company"));
        m.setCurrentRole(rs.getString("current_role"));
        m.setYearsOfExperience(rs.getInt("years_of_experience"));
        m.setSkillsJson(rs.getString("skills_json"));
        m.setBio(rs.getString("bio"));
        m.setLinkedinUrl(rs.getString("linkedin_url"));
        m.setOpenForReferrals(rs.getBoolean("open_for_referrals"));
        m.setIsVerifiedMentor(rs.getBoolean("is_verified_mentor"));
        // Extra joined user columns if present
        try {
            m.setFullName(rs.getString("full_name"));
            m.setEmail(rs.getString("email"));
            m.setAvatarUrl(rs.getString("avatar_url"));
        } catch (Exception ignored) {}
        return m;
    };

    public Optional<StudentProfile> findStudentProfileByUserId(Long userId) {
        String sql = "SELECT * FROM student_profiles WHERE user_id = ?";
        try {
            StudentProfile profile = jdbcTemplate.queryForObject(sql, studentRowMapper, userId);
            return Optional.ofNullable(profile);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void upsertStudentProfile(StudentProfile p) {
        int completion = calculateStudentProfileCompletion(p);
        p.setProfileCompletionPct(completion);

        String sql = "INSERT INTO student_profiles (user_id, college_name, degree, branch, graduation_year, cgpa, " +
                "skills_json, interests_json, preferred_roles_json, preferred_locations_json, portfolio_url, github_url, linkedin_url, profile_completion_pct) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE college_name=VALUES(college_name), degree=VALUES(degree), branch=VALUES(branch), " +
                "graduation_year=VALUES(graduation_year), cgpa=VALUES(cgpa), skills_json=VALUES(skills_json), " +
                "interests_json=VALUES(interests_json), preferred_roles_json=VALUES(preferred_roles_json), " +
                "preferred_locations_json=VALUES(preferred_locations_json), portfolio_url=VALUES(portfolio_url), " +
                "github_url=VALUES(github_url), linkedin_url=VALUES(linkedin_url), profile_completion_pct=VALUES(profile_completion_pct)";

        jdbcTemplate.update(sql,
                p.getUserId(),
                p.getCollegeName(),
                p.getDegree(),
                p.getBranch(),
                p.getGraduationYear(),
                p.getCgpa(),
                p.getSkillsJson(),
                p.getInterestsJson(),
                p.getPreferredRolesJson(),
                p.getPreferredLocationsJson(),
                p.getPortfolioUrl(),
                p.getGithubUrl(),
                p.getLinkedinUrl(),
                completion
        );
    }

    public Optional<MentorProfile> findMentorProfileByUserId(Long userId) {
        String sql = "SELECT m.*, u.full_name, u.email, u.avatar_url FROM mentor_profiles m " +
                "JOIN users u ON m.user_id = u.id WHERE m.user_id = ?";
        try {
            MentorProfile profile = jdbcTemplate.queryForObject(sql, mentorRowMapper, userId);
            return Optional.ofNullable(profile);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void upsertMentorProfile(MentorProfile m) {
        String sql = "INSERT INTO mentor_profiles (user_id, college_name, graduating_batch, field_of_work, " +
                "current_company, current_role, years_of_experience, skills_json, bio, linkedin_url, open_for_referrals) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE college_name=VALUES(college_name), graduating_batch=VALUES(graduating_batch), " +
                "field_of_work=VALUES(field_of_work), current_company=VALUES(current_company), current_role=VALUES(current_role), " +
                "years_of_experience=VALUES(years_of_experience), skills_json=VALUES(skills_json), bio=VALUES(bio), " +
                "linkedin_url=VALUES(linkedin_url), open_for_referrals=VALUES(open_for_referrals)";

        jdbcTemplate.update(sql,
                m.getUserId(),
                m.getCollegeName(),
                m.getGraduatingBatch(),
                m.getFieldOfWork(),
                m.getCurrentCompany(),
                m.getCurrentRole(),
                m.getYearsOfExperience() != null ? m.getYearsOfExperience() : 0,
                m.getSkillsJson(),
                m.getBio(),
                m.getLinkedinUrl(),
                m.getOpenForReferrals() != null ? m.getOpenForReferrals() : true
        );
    }

    public int setMentorVerificationStatus(Long userId, boolean isVerified) {
        String sql = "UPDATE mentor_profiles SET is_verified_mentor = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql, isVerified, userId);
    }

    public List<MentorProfile> listMentors(int limit, int offset, String search) {
        String baseSql = "SELECT m.*, u.full_name, u.email, u.avatar_url FROM mentor_profiles m " +
                "JOIN users u ON m.user_id = u.id WHERE u.status = 'ACTIVE' ";
        if (search != null && !search.isBlank()) {
            String sql = baseSql + "AND (u.full_name LIKE ? OR m.current_company LIKE ? OR m.current_role LIKE ?) " +
                    "ORDER BY m.is_verified_mentor DESC, m.user_id DESC LIMIT ? OFFSET ?";
            String pattern = "%" + search + "%";
            return jdbcTemplate.query(sql, mentorRowMapper, pattern, pattern, pattern, limit, offset);
        } else {
            String sql = baseSql + "ORDER BY m.is_verified_mentor DESC, m.user_id DESC LIMIT ? OFFSET ?";
            return jdbcTemplate.query(sql, mentorRowMapper, limit, offset);
        }
    }

    public int calculateStudentProfileCompletion(StudentProfile p) {
        int score = 0;
        if (p.getCollegeName() != null && !p.getCollegeName().isBlank()) score += 15;
        if (p.getDegree() != null && !p.getDegree().isBlank()) score += 15;
        if (p.getBranch() != null && !p.getBranch().isBlank()) score += 15;
        if (p.getGraduationYear() != null && p.getGraduationYear() > 0) score += 15;
        if (p.getCgpa() != null && p.getCgpa().doubleValue() > 0.0) score += 10;
        if (p.getSkillsJson() != null && p.getSkillsJson().length() > 5) score += 15;
        if ((p.getPortfolioUrl() != null && !p.getPortfolioUrl().isBlank()) ||
            (p.getGithubUrl() != null && !p.getGithubUrl().isBlank()) ||
            (p.getLinkedinUrl() != null && !p.getLinkedinUrl().isBlank())) {
            score += 15;
        }
        return Math.min(score, 100);
    }
}
