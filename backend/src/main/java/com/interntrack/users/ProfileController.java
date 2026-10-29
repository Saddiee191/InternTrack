package com.interntrack.users;

import com.interntrack.common.ApiResponse;
import com.interntrack.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Profiles", description = "Endpoints for managing user, student, and mentor profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get currently authenticated user's profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        ProfileResponse profile = profileService.getProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", profile));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update currently authenticated user's profile details")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ProfileUpdateRequest request
    ) {
        ProfileResponse updated = profileService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @GetMapping("/mentors")
    @Operation(summary = "Public directory of alumni and senior mentors")
    public ResponseEntity<ApiResponse<List<MentorProfile>>> listMentors(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(required = false) String search
    ) {
        List<MentorProfile> mentors = profileService.listMentors(limit, offset, search);
        return ResponseEntity.ok(ApiResponse.success("Mentors retrieved", mentors));
    }

    @GetMapping("/mentors/{id}")
    @Operation(summary = "Get mentor profile details by ID")
    public ResponseEntity<ApiResponse<MentorProfile>> getMentorById(@PathVariable Long id) {
        MentorProfile mentor = profileService.getMentorById(id);
        return ResponseEntity.ok(ApiResponse.success("Mentor details retrieved", mentor));
    }
}
