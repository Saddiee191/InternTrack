package com.interntrack.users;

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
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setStatus(rs.getString("status"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return user;
    };

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, email, password_hash, full_name, role, avatar_url, status, created_at, updated_at " +
                "FROM users WHERE email = ? AND status != 'DELETED'";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT id, email, password_hash, full_name, role, avatar_url, status, created_at, updated_at " +
                "FROM users WHERE id = ? AND status != 'DELETED'";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND status != 'DELETED'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public Long createUser(User user) {
        String sql = "INSERT INTO users (email, password_hash, full_name, role, avatar_url, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail().trim().toLowerCase());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName().trim());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getAvatarUrl());
            ps.setString(6, user.getStatus() != null ? user.getStatus() : "ACTIVE");
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        throw new IllegalStateException("Failed to retrieve generated user ID");
    }

    public int updatePassword(Long id, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        return jdbcTemplate.update(sql, passwordHash, id);
    }

    public int updateProfileBasics(Long id, String fullName, String avatarUrl) {
        String sql = "UPDATE users SET full_name = ?, avatar_url = ? WHERE id = ?";
        return jdbcTemplate.update(sql, fullName, avatarUrl, id);
    }

    public int updateStatus(Long id, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    public int softDeleteUser(Long id) {
        String sql = "UPDATE users SET status = 'DELETED' WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ? AND status = 'ACTIVE'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, role);
        return count != null ? count : 0;
    }

    public List<User> findAllUsers(int limit, int offset, String search) {
        if (search != null && !search.isBlank()) {
                String sql = "SELECT id, email, NULL AS password_hash, full_name, role, avatar_url, status, created_at, updated_at " +
                    "FROM users WHERE (full_name LIKE ? OR email LIKE ?) " +
                    "ORDER BY id DESC LIMIT ? OFFSET ?";
            String pattern = "%" + search + "%";
            return jdbcTemplate.query(sql, userRowMapper, pattern, pattern, limit, offset);
        } else {
                String sql = "SELECT id, email, NULL AS password_hash, full_name, role, avatar_url, status, created_at, updated_at " +
                    "FROM users ORDER BY id DESC LIMIT ? OFFSET ?";
            return jdbcTemplate.query(sql, userRowMapper, limit, offset);
        }
    }
}
