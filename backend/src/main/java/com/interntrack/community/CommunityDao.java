package com.interntrack.community;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CommunityDao {

    private final JdbcTemplate jdbcTemplate;

    public CommunityDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CommunityPost> postRowMapper = (rs, rowNum) -> {
        CommunityPost p = new CommunityPost();
        p.setId(rs.getLong("id"));
        p.setAuthorId(rs.getLong("author_id"));
        p.setTitle(rs.getString("title"));
        p.setCategory(rs.getString("category"));
        p.setCompanyName(rs.getString("company_name"));
        p.setRoleTitle(rs.getString("role_title"));
        p.setContent(rs.getString("content"));
        p.setUpvoteCount(rs.getInt("upvote_count"));
        p.setCommentCount(rs.getInt("comment_count"));
        p.setIsPinned(rs.getBoolean("is_pinned"));
        p.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) p.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) p.setUpdatedAt(updatedAt.toLocalDateTime());

        // Author details
        try {
            p.setAuthorName(rs.getString("author_name"));
            p.setAuthorRole(rs.getString("author_role"));
            p.setAuthorCompany(rs.getString("author_company"));
            p.setAuthorAvatarUrl(rs.getString("author_avatar_url"));
            p.setIsVerifiedMentor(rs.getBoolean("is_verified_mentor"));
        } catch (Exception ignored) {}

        try {
            p.setHasUpvoted(rs.getBoolean("has_upvoted"));
        } catch (Exception ignored) {}

        try {
            p.setIsSaved(rs.getBoolean("is_saved"));
        } catch (Exception ignored) {}

        return p;
    };

    private final RowMapper<PostComment> commentRowMapper = (rs, rowNum) -> {
        PostComment c = new PostComment();
        c.setId(rs.getLong("id"));
        c.setPostId(rs.getLong("post_id"));
        c.setAuthorId(rs.getLong("author_id"));
        c.setContent(rs.getString("content"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) c.setCreatedAt(ts.toLocalDateTime());

        try {
            c.setAuthorName(rs.getString("author_name"));
            c.setAuthorRole(rs.getString("author_role"));
            c.setAuthorAvatarUrl(rs.getString("author_avatar_url"));
        } catch (Exception ignored) {}

        return c;
    };

    public List<CommunityPost> findPosts(String category, String search, String sortBy, Long userId, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, u.full_name as author_name, u.role as author_role, u.avatar_url as author_avatar_url, " +
                "m.current_company as author_company, COALESCE(m.is_verified_mentor, FALSE) as is_verified_mentor, "
        );

        if (userId != null) {
            sql.append("(CASE WHEN pv.post_id IS NOT NULL THEN TRUE ELSE FALSE END) as has_upvoted, ");
            sql.append("(CASE WHEN sp.post_id IS NOT NULL THEN TRUE ELSE FALSE END) as is_saved ");
        } else {
            sql.append("FALSE as has_upvoted, FALSE as is_saved ");
        }

        sql.append("FROM community_posts p ")
           .append("JOIN users u ON p.author_id = u.id ")
           .append("LEFT JOIN mentor_profiles m ON p.author_id = m.user_id ");

        if (userId != null) {
            sql.append("LEFT JOIN post_votes pv ON p.id = pv.post_id AND pv.user_id = ? ");
            sql.append("LEFT JOIN saved_posts sp ON p.id = sp.post_id AND sp.user_id = ? ");
        }

        sql.append("WHERE p.status = 'PUBLISHED' ");
        List<Object> params = new ArrayList<>();
        if (userId != null) {
            params.add(userId);
            params.add(userId);
        }

        if (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) {
            sql.append("AND p.category = ? ");
            params.add(category);
        }

        if (search != null && !search.isBlank()) {
            sql.append("AND (p.title LIKE ? OR p.content LIKE ? OR p.company_name LIKE ? OR p.role_title LIKE ?) ");
            String pattern = "%" + search + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if ("upvotes".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.is_pinned DESC, p.upvote_count DESC, p.created_at DESC LIMIT ? OFFSET ?");
        } else {
            sql.append("ORDER BY p.is_pinned DESC, p.created_at DESC LIMIT ? OFFSET ?");
        }
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), postRowMapper, params.toArray());
    }

    public Optional<CommunityPost> findPostById(Long id, Long userId) {
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, u.full_name as author_name, u.role as author_role, u.avatar_url as author_avatar_url, " +
                "m.current_company as author_company, COALESCE(m.is_verified_mentor, FALSE) as is_verified_mentor, "
        );

        if (userId != null) {
            sql.append("(CASE WHEN pv.post_id IS NOT NULL THEN TRUE ELSE FALSE END) as has_upvoted, ");
            sql.append("(CASE WHEN sp.post_id IS NOT NULL THEN TRUE ELSE FALSE END) as is_saved ");
        } else {
            sql.append("FALSE as has_upvoted, FALSE as is_saved ");
        }

        sql.append("FROM community_posts p ")
           .append("JOIN users u ON p.author_id = u.id ")
           .append("LEFT JOIN mentor_profiles m ON p.author_id = m.user_id ");

        if (userId != null) {
            sql.append("LEFT JOIN post_votes pv ON p.id = pv.post_id AND pv.user_id = ? ");
            sql.append("LEFT JOIN saved_posts sp ON p.id = sp.post_id AND sp.user_id = ? ");
        }

        sql.append("WHERE p.id = ?");

        try {
            CommunityPost post;
            if (userId != null) {
                post = jdbcTemplate.queryForObject(sql.toString(), postRowMapper, userId, userId, id);
            } else {
                post = jdbcTemplate.queryForObject(sql.toString(), postRowMapper, id);
            }
            return Optional.ofNullable(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Long createPost(Long authorId, CreatePostRequest req) {
        String sql = "INSERT INTO community_posts (author_id, title, category, company_name, role_title, content) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, authorId);
            ps.setString(2, req.getTitle().trim());
            ps.setString(3, req.getCategory().trim());
            ps.setString(4, req.getCompanyName());
            ps.setString(5, req.getRoleTitle());
            ps.setString(6, req.getContent());
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public int updatePost(Long id, CreatePostRequest req) {
        String sql = "UPDATE community_posts SET title = ?, category = ?, company_name = ?, role_title = ?, content = ? " +
                "WHERE id = ?";
        return jdbcTemplate.update(sql, req.getTitle().trim(), req.getCategory().trim(), req.getCompanyName(), req.getRoleTitle(), req.getContent(), id);
    }

    public int deletePost(Long id) {
        String sql = "DELETE FROM community_posts WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public boolean toggleVote(Long postId, Long userId) {
        String checkSql = "SELECT COUNT(*) FROM post_votes WHERE post_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, postId, userId);
        boolean alreadyVoted = count != null && count > 0;

        if (alreadyVoted) {
            jdbcTemplate.update("DELETE FROM post_votes WHERE post_id = ? AND user_id = ?", postId, userId);
            jdbcTemplate.update("UPDATE community_posts SET upvote_count = GREATEST(0, upvote_count - 1) WHERE id = ?", postId);
            return false;
        } else {
            jdbcTemplate.update("INSERT INTO post_votes (post_id, user_id) VALUES (?, ?)", postId, userId);
            jdbcTemplate.update("UPDATE community_posts SET upvote_count = upvote_count + 1 WHERE id = ?", postId);
            return true;
        }
    }

    public boolean toggleBookmark(Long postId, Long userId) {
        String checkSql = "SELECT COUNT(*) FROM saved_posts WHERE post_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, postId, userId);
        boolean alreadySaved = count != null && count > 0;

        if (alreadySaved) {
            jdbcTemplate.update("DELETE FROM saved_posts WHERE post_id = ? AND user_id = ?", postId, userId);
            return false;
        } else {
            jdbcTemplate.update("INSERT INTO saved_posts (post_id, user_id) VALUES (?, ?)", postId, userId);
            return true;
        }
    }

    public List<CommunityPost> findSavedPosts(Long userId) {
        String sql = "SELECT p.*, u.full_name as author_name, u.role as author_role, u.avatar_url as author_avatar_url, " +
                "m.current_company as author_company, COALESCE(m.is_verified_mentor, FALSE) as is_verified_mentor, " +
                "TRUE as is_saved, (CASE WHEN pv.post_id IS NOT NULL THEN TRUE ELSE FALSE END) as has_upvoted " +
                "FROM saved_posts sp " +
                "JOIN community_posts p ON sp.post_id = p.id " +
                "JOIN users u ON p.author_id = u.id " +
                "LEFT JOIN mentor_profiles m ON p.author_id = m.user_id " +
                "LEFT JOIN post_votes pv ON p.id = pv.post_id AND pv.user_id = ? " +
                "WHERE sp.user_id = ? ORDER BY sp.created_at DESC";
        return jdbcTemplate.query(sql, postRowMapper, userId, userId);
    }

    public List<PostComment> getComments(Long postId) {
        String sql = "SELECT c.*, u.full_name as author_name, u.role as author_role, u.avatar_url as author_avatar_url " +
                "FROM post_comments c " +
                "JOIN users u ON c.author_id = u.id " +
                "WHERE c.post_id = ? ORDER BY c.created_at ASC";
        return jdbcTemplate.query(sql, commentRowMapper, postId);
    }

    public Long addComment(Long postId, Long authorId, String content) {
        String sql = "INSERT INTO post_comments (post_id, author_id, content) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, postId);
            ps.setLong(2, authorId);
            ps.setString(3, content.trim());
            return ps;
        }, keyHolder);

        jdbcTemplate.update("UPDATE community_posts SET comment_count = comment_count + 1 WHERE id = ?", postId);
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public int deleteComment(Long commentId, Long authorId) {
        String fetchSql = "SELECT post_id FROM post_comments WHERE id = ? AND author_id = ?";
        try {
            Long postId = jdbcTemplate.queryForObject(fetchSql, Long.class, commentId, authorId);
            if (postId != null) {
                int rows = jdbcTemplate.update("DELETE FROM post_comments WHERE id = ? AND author_id = ?", commentId, authorId);
                if (rows > 0) {
                    jdbcTemplate.update("UPDATE community_posts SET comment_count = GREATEST(0, comment_count - 1) WHERE id = ?", postId);
                }
                return rows;
            }
        } catch (EmptyResultDataAccessException ignored) {}
        return 0;
    }

    public void reportPost(Long postId, Long reporterId, String reason) {
        String sql = "INSERT INTO content_reports (reporter_id, target_type, target_id, reason) VALUES (?, 'POST', ?, ?)";
        jdbcTemplate.update(sql, reporterId, postId, reason);
    }
}
