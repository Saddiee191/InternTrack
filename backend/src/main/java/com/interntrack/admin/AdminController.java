package com.interntrack.admin;

import com.interntrack.common.ApiResponse;
import com.interntrack.scamdetector.ScamScan;
import com.interntrack.security.UserPrincipal;
import com.interntrack.users.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administrator", description = "Platform management, content moderation, user suspension, and audit trails")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get high-level platform statistics and moderation metrics")
    public ResponseEntity<ApiResponse<AdminDashboardMetricsDto>> getMetrics() {
        AdminDashboardMetricsDto metrics = adminService.getMetrics();
        return ResponseEntity.ok(ApiResponse.success("Admin metrics retrieved", metrics));
    }

    @GetMapping("/reports/content")
    @Operation(summary = "List community content reports submitted by students")
    public ResponseEntity<ApiResponse<List<ContentReport>>> getContentReports() {
        List<ContentReport> reports = adminService.getContentReports();
        return ResponseEntity.ok(ApiResponse.success("Content reports retrieved", reports));
    }

    @PatchMapping("/reports/content/{id}/resolve")
    @Operation(summary = "Resolve or dismiss a content report")
    public ResponseEntity<ApiResponse<Void>> resolveContentReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ResolveReportRequest req
    ) {
        adminService.resolveContentReport(id, req.getStatus(), req.getResolutionNotes(), principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Content report resolved", null));
    }

    @GetMapping("/reports/opportunities")
    @Operation(summary = "List opportunity listing reports")
    public ResponseEntity<ApiResponse<List<OpportunityReport>>> getOpportunityReports() {
        List<OpportunityReport> reports = adminService.getOpportunityReports();
        return ResponseEntity.ok(ApiResponse.success("Opportunity reports retrieved", reports));
    }

    @PatchMapping("/reports/opportunities/{id}/resolve")
    @Operation(summary = "Resolve or dismiss an opportunity report")
    public ResponseEntity<ApiResponse<Void>> resolveOpportunityReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ResolveReportRequest req
    ) {
        adminService.resolveOpportunityReport(id, req.getStatus(), principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Opportunity report resolved", null));
    }

    @GetMapping("/scam-reports")
    @Operation(summary = "Review suspicious offers flagged for administrator investigation")
    public ResponseEntity<ApiResponse<List<ScamScan>>> getReportedScamScans() {
        List<ScamScan> list = adminService.getReportedScamScans();
        return ResponseEntity.ok(ApiResponse.success("Reported scam scans", list));
    }

    @GetMapping("/users")
    @Operation(summary = "List user accounts with search and pagination")
    public ResponseEntity<ApiResponse<List<User>>> getUsers(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(required = false) String search
    ) {
        List<User> users = adminService.getUsers(limit, offset, search);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }

    @PatchMapping("/users/{id}/status")
    @Operation(summary = "Update user status (ACTIVE, SUSPENDED, DELETED)")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String status = body.getOrDefault("status", "ACTIVE");
        adminService.updateUserStatus(id, status, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("User status updated to " + status, null));
    }

    @PatchMapping("/opportunities/{id}/verification")
    @Operation(summary = "Mark company listing verification status (VERIFIED, EXTERNAL, UNVERIFIED)")
    public ResponseEntity<ApiResponse<Void>> setOpportunityVerification(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String status = body.getOrDefault("status", "VERIFIED");
        adminService.setOpportunityVerification(id, status, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Opportunity verification updated to " + status, null));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "View audit trail of sensitive administrative actions")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        List<AuditLog> logs = adminService.getAuditLogs(limit, offset);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved", logs));
    }
}
