package com.interntrack.community;

import com.interntrack.common.ApiResponse;
import com.interntrack.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community/posts")
@Tag(name = "Community Hub", description = "Senior interview guides, roadmaps, technical rounds, Q&A, and voting")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping
    @Operation(summary = "List community posts with filters for category, search, and sorting")
    public ResponseEntity<ApiResponse<List<CommunityPost>>> listPosts(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "recent") String sortBy,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        Long userId = principal != null ? principal.getId() : null;
        List<CommunityPost> posts = communityService.listPosts(category, search, sortBy, userId, limit, offset);
        return ResponseEntity.ok(ApiResponse.success("Posts retrieved", posts));
    }

    @GetMapping("/saved")
    @Operation(summary = "List bookmarked posts for authenticated user")
    public ResponseEntity<ApiResponse<List<CommunityPost>>> getSavedPosts(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<CommunityPost> saved = communityService.getSavedPosts(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Saved posts retrieved", saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full post details by ID")
    public ResponseEntity<ApiResponse<CommunityPost>> getPost(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        Long userId = principal != null ? principal.getId() : null;
        CommunityPost post = communityService.getPostById(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Post details retrieved", post));
    }

    @PostMapping
    @Operation(summary = "Publish a new interview experience, guide, or advice post")
    public ResponseEntity<ApiResponse<CommunityPost>> createPost(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreatePostRequest req
    ) {
        CommunityPost created = communityService.createPost(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Post published successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit an existing post (Author or Admin)")
    public ResponseEntity<ApiResponse<CommunityPost>> updatePost(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody CreatePostRequest req
    ) {
        boolean isAdmin = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        CommunityPost updated = communityService.updatePost(id, principal.getId(), req, isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Post updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an existing post (Author or Admin)")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        boolean isAdmin = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        communityService.deletePost(id, principal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully", null));
    }

    @PostMapping("/{id}/vote")
    @Operation(summary = "Toggle upvote on a post")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggleVote(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        boolean hasUpvoted = communityService.toggleVote(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Vote updated", Map.of("hasUpvoted", hasUpvoted)));
    }

    @PostMapping("/{id}/bookmark")
    @Operation(summary = "Toggle bookmark on a post")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggleBookmark(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        boolean isSaved = communityService.toggleBookmark(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Bookmark updated", Map.of("isSaved", isSaved)));
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Get all comments for a community post")
    public ResponseEntity<ApiResponse<List<PostComment>>> getComments(@PathVariable Long id) {
        List<PostComment> comments = communityService.getComments(id);
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved", comments));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment to a community post")
    public ResponseEntity<ApiResponse<Long>> addComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody CreateCommentRequest req
    ) {
        Long commentId = communityService.addComment(id, principal.getId(), req.getContent());
        return ResponseEntity.ok(ApiResponse.success("Comment added successfully", commentId));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Delete user's own comment")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long commentId
    ) {
        communityService.deleteComment(commentId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Comment removed", null));
    }

    @PostMapping("/{id}/report")
    @Operation(summary = "Report post for inappropriate content or spam")
    public ResponseEntity<ApiResponse<Void>> reportPost(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ReportContentRequest req
    ) {
        communityService.reportPost(id, principal.getId(), req.getReason());
        return ResponseEntity.ok(ApiResponse.success("Report submitted to moderation queue", null));
    }
}
