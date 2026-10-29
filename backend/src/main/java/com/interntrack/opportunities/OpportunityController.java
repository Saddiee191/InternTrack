package com.interntrack.opportunities;

import com.interntrack.applications.Application;
import com.interntrack.common.ApiResponse;
import com.interntrack.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
@Tag(name = "Opportunities", description = "Discovery board, eligibility calculation, bookmarks, and direct application")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    @Operation(summary = "Search and filter verified opportunities")
    public ResponseEntity<ApiResponse<List<Opportunity>>> listOpportunities(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String workplaceType,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String verificationStatus,
            @RequestParam(required = false, defaultValue = "true") Boolean onlyActive,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        Long userId = principal != null ? principal.getId() : null;
        List<Opportunity> ops = opportunityService.listOpportunities(
                search, location, workplaceType, employmentType, verificationStatus, onlyActive, userId, limit, offset
        );
        return ResponseEntity.ok(ApiResponse.success("Opportunities retrieved", ops));
    }

    @GetMapping("/saved")
    @Operation(summary = "List bookmarked opportunities for authenticated student")
    public ResponseEntity<ApiResponse<List<Opportunity>>> getSavedOpportunities(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<Opportunity> saved = opportunityService.getSavedOpportunities(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Saved opportunities", saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed opportunity information")
    public ResponseEntity<ApiResponse<Opportunity>> getOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        Long userId = principal != null ? principal.getId() : null;
        Opportunity opp = opportunityService.getOpportunityById(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Opportunity details retrieved", opp));
    }

    @GetMapping("/{id}/eligibility")
    @Operation(summary = "Evaluate student profile against opportunity eligibility requirements")
    public ResponseEntity<ApiResponse<EligibilityResultDto>> checkEligibility(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        EligibilityResultDto result = opportunityService.checkEligibility(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Eligibility evaluated", result));
    }

    @PostMapping("/{id}/apply")
    @Operation(summary = "Add opportunity to student's personal application tracker")
    public ResponseEntity<ApiResponse<Application>> applyFromOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        Application app = opportunityService.applyFromOpportunity(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Application created from opportunity", app));
    }

    @PostMapping("/{id}/save")
    @Operation(summary = "Bookmark an opportunity")
    public ResponseEntity<ApiResponse<Void>> saveOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        opportunityService.saveOpportunity(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Opportunity bookmarked", null));
    }

    @DeleteMapping("/{id}/save")
    @Operation(summary = "Remove bookmark from an opportunity")
    public ResponseEntity<ApiResponse<Void>> unsaveOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        opportunityService.unsaveOpportunity(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Bookmark removed", null));
    }

    @PostMapping("/{id}/report")
    @Operation(summary = "Report suspicious or inaccurate opportunity listing")
    public ResponseEntity<ApiResponse<Void>> reportOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody OpportunityReportRequest req
    ) {
        opportunityService.reportOpportunity(id, principal.getId(), req.getReason());
        return ResponseEntity.ok(ApiResponse.success("Listing reported to administrator for review", null));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create an opportunity listing (Admin only)")
    public ResponseEntity<ApiResponse<Opportunity>> createOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody OpportunityRequest req
    ) {
        Opportunity created = opportunityService.createOpportunity(req, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Opportunity created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an opportunity listing (Admin only)")
    public ResponseEntity<ApiResponse<Opportunity>> updateOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody OpportunityRequest req
    ) {
        Opportunity updated = opportunityService.updateOpportunity(id, req, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Opportunity updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an opportunity listing (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteOpportunity(
            @PathVariable Long id
    ) {
        opportunityService.deleteOpportunity(id);
        return ResponseEntity.ok(ApiResponse.success("Opportunity deleted successfully", null));
    }
}
