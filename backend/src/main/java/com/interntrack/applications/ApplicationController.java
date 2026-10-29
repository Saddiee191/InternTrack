package com.interntrack.applications;

import com.interntrack.common.ApiResponse;
import com.interntrack.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Applications", description = "Student personal application tracker, status updates, and stats")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    @Operation(summary = "List all applications of the authenticated student with optional filters")
    public ResponseEntity<ApiResponse<List<Application>>> listApplications(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy
    ) {
        List<Application> apps = applicationService.listApplications(principal.getId(), status, search, sortBy);
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved", apps));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get aggregated application tracking statistics and chart data")
    public ResponseEntity<ApiResponse<ApplicationStatsDto>> getStats(@AuthenticationPrincipal UserPrincipal principal) {
        ApplicationStatsDto stats = applicationService.getStats(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Application statistics retrieved", stats));
    }

    @GetMapping("/reminders")
    @Operation(summary = "Get active application reminders for upcoming deadlines, assessments, and interviews")
    public ResponseEntity<ApiResponse<List<ApplicationReminder>>> getReminders(@AuthenticationPrincipal UserPrincipal principal) {
        List<ApplicationReminder> reminders = applicationService.getReminders(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Reminders retrieved", reminders));
    }

    @PostMapping("/reminders")
    @Operation(summary = "Create a reminder for an application")
    public ResponseEntity<ApiResponse<Long>> createReminder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ReminderRequest req
    ) {
        Long reminderId = applicationService.createReminder(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Reminder scheduled", reminderId));
    }

    @PatchMapping("/reminders/{id}/dismiss")
    @Operation(summary = "Dismiss an upcoming reminder")
    public ResponseEntity<ApiResponse<Void>> dismissReminder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        applicationService.dismissReminder(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Reminder dismissed", null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application details by ID")
    public ResponseEntity<ApiResponse<Application>> getApplication(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        Application app = applicationService.getApplication(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Application details retrieved", app));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get timeline of status changes for an application")
    public ResponseEntity<ApiResponse<List<ApplicationStatusHistory>>> getStatusHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        List<ApplicationStatusHistory> history = applicationService.getStatusHistory(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Status history retrieved", history));
    }

    @PostMapping
    @Operation(summary = "Create a new internship or job application")
    public ResponseEntity<ApiResponse<Application>> createApplication(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ApplicationRequest req
    ) {
        Application created = applicationService.createApplication(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Application created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing application")
    public ResponseEntity<ApiResponse<Application>> updateApplication(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ApplicationRequest req
    ) {
        Application updated = applicationService.updateApplication(id, principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Application updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Quick update status of an application")
    public ResponseEntity<ApiResponse<Application>> updateStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest req
    ) {
        Application updated = applicationService.updateStatus(id, principal.getId(), req.getStatus(), req.getNotes());
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an application")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        applicationService.deleteApplication(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Application deleted successfully", null));
    }
}
