package com.interntrack.scamdetector;

import com.interntrack.ai.ScamScanInput;
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
@RequestMapping("/api/scam-scans")
@Tag(name = "Scam Detector", description = "Evaluate suspicious internship or job offers for fraudulent indicators")
public class ScamDetectorController {

    private final ScamDetectorService scamDetectorService;

    public ScamDetectorController(ScamDetectorService scamDetectorService) {
        this.scamDetectorService = scamDetectorService;
    }

    @PostMapping
    @Operation(summary = "Analyze an offer or message for fraudulent risk indicators")
    public ResponseEntity<ApiResponse<ScamScan>> scanOffer(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ScamScanInput input
    ) {
        ScamScan scan = scamDetectorService.scanOffer(principal.getId(), input);
        return ResponseEntity.ok(ApiResponse.success("Scam analysis completed", scan));
    }

    @GetMapping("/history")
    @Operation(summary = "Get user's previous private scam scan history")
    public ResponseEntity<ApiResponse<List<ScamScan>>> getHistory(@AuthenticationPrincipal UserPrincipal principal) {
        List<ScamScan> history = scamDetectorService.getHistory(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Scan history retrieved", history));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed report of a previous scan")
    public ResponseEntity<ApiResponse<ScamScan>> getScan(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        ScamScan scan = scamDetectorService.getScanById(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Scan report retrieved", scan));
    }

    @PostMapping("/{id}/report")
    @Operation(summary = "Flag this suspicious offer for administrator review")
    public ResponseEntity<ApiResponse<Void>> reportToAdmin(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        scamDetectorService.reportToAdmin(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Report submitted to administrator for investigation", null));
    }
}
