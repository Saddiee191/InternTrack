package com.interntrack.community;

import com.interntrack.common.ResourceNotFoundException;
import com.interntrack.common.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityService {

    private final CommunityDao communityDao;

    public CommunityService(CommunityDao communityDao) {
        this.communityDao = communityDao;
    }

    public List<CommunityPost> listPosts(String category, String search, String sortBy, Long userId, int limit, int offset) {
        return communityDao.findPosts(category, search, sortBy, userId, limit, offset);
    }

    public CommunityPost getPostById(Long id, Long userId) {
        return communityDao.findPostById(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Community post not found with ID: " + id));
    }

    @Transactional
    public CommunityPost createPost(Long authorId, CreatePostRequest req) {
        Long id = communityDao.createPost(authorId, req);
        return getPostById(id, authorId);
    }

    @Transactional
    public CommunityPost updatePost(Long id, Long authorId, CreatePostRequest req, boolean isAdmin) {
        CommunityPost existing = getPostById(id, authorId);
        if (!isAdmin && !existing.getAuthorId().equals(authorId)) {
            throw new UnauthorizedException("You do not have permission to edit this post");
        }
        communityDao.updatePost(id, req);
        return getPostById(id, authorId);
    }

    @Transactional
    public void deletePost(Long id, Long authorId, boolean isAdmin) {
        CommunityPost existing = getPostById(id, authorId);
        if (!isAdmin && !existing.getAuthorId().equals(authorId)) {
            throw new UnauthorizedException("You do not have permission to delete this post");
        }
        communityDao.deletePost(id);
    }

    public boolean toggleVote(Long postId, Long userId) {
        getPostById(postId, userId);
        return communityDao.toggleVote(postId, userId);
    }

    public boolean toggleBookmark(Long postId, Long userId) {
        getPostById(postId, userId);
        return communityDao.toggleBookmark(postId, userId);
    }

    public List<CommunityPost> getSavedPosts(Long userId) {
        return communityDao.findSavedPosts(userId);
    }

    public List<PostComment> getComments(Long postId) {
        return communityDao.getComments(postId);
    }

    @Transactional
    public Long addComment(Long postId, Long authorId, String content) {
        getPostById(postId, authorId);
        return communityDao.addComment(postId, authorId, content);
    }

    public void deleteComment(Long commentId, Long authorId) {
        int rows = communityDao.deleteComment(commentId, authorId);
        if (rows == 0) {
            throw new ResourceNotFoundException("Comment not found or unauthorized to delete");
        }
    }

    public void reportPost(Long postId, Long reporterId, String reason) {
        getPostById(postId, reporterId);
        communityDao.reportPost(postId, reporterId, reason);
    }
}
