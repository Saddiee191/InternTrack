package com.interntrack.referrals;

import com.interntrack.common.ApiResponse;
import com.interntrack.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referrals")
@Tag(name = "Referrals", description = "Student referral inquiries to alumni and senior mentors")
public class ReferralController {

    private final ReferralService referralService;

    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit a referral interest request to an alumni mentor")
    public ResponseEntity<ApiResponse<ReferralRequest>> createRequest(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateReferralRequestDto req
    ) {
        ReferralRequest created = referralService.createRequest(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Referral request submitted successfully", created));
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get referral requests sent by the authenticated student")
    public ResponseEntity<ApiResponse<List<ReferralRequest>>> getStudentRequests(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<ReferralRequest> requests = referralService.getStudentRequests(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Student referral requests retrieved", requests));
    }

    @GetMapping("/mentor")
    @PreAuthorize("hasRole('MENTOR')")
    @Operation(summary = "Get incoming referral requests for the authenticated mentor")
    public ResponseEntity<ApiResponse<List<ReferralRequest>>> getMentorRequests(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<ReferralRequest> requests = referralService.getMentorRequests(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Mentor incoming requests retrieved", requests));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MENTOR')")
    @Operation(summary = "Update referral request status (Mentor response)")
    public ResponseEntity<ApiResponse<ReferralRequest>> updateStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateReferralStatusDto req
    ) {
        ReferralRequest updated = referralService.updateStatus(id, principal.getId(), req.getStatus(), req.getMentorResponse());
        return ResponseEntity.ok(ApiResponse.success("Referral status updated", updated));
    }
}
