package com.interntrack.resumes;

import com.interntrack.common.ApiResponse;
import com.interntrack.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resume Analyzer", description = "PDF upload, text extraction, ATS completeness scoring, and JD matching")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload PDF resume and extract text using Apache PDFBox")
    public ResponseEntity<ApiResponse<ResumeFile>> uploadResume(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file
    ) {
        ResumeFile uploaded = resumeService.uploadResume(principal.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Resume uploaded and parsed successfully", uploaded));
    }

    @PostMapping("/analyze")
    @Operation(summary = "Analyze resume against ATS benchmarks or specific target job description")
    public ResponseEntity<ApiResponse<ResumeAnalysis>> analyzeResume(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ResumeAnalyzeRequest req
    ) {
        ResumeAnalysis analysis = resumeService.analyzeResume(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Resume analyzed successfully", analysis));
    }

    @GetMapping("/history")
    @Operation(summary = "Get user's previous resume analyses history")
    public ResponseEntity<ApiResponse<List<ResumeAnalysis>>> getHistory(@AuthenticationPrincipal UserPrincipal principal) {
        List<ResumeAnalysis> history = resumeService.getHistory(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Analysis history retrieved", history));
    }

    @GetMapping("/files")
    @Operation(summary = "List the authenticated user's uploaded resume metadata")
    public ResponseEntity<ApiResponse<List<ResumeFile>>> getFiles(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Resume files retrieved", resumeService.getFiles(principal.getId())));
    }

    @DeleteMapping("/files/{id}")
    @Operation(summary = "Delete an uploaded resume and its analyses")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        resumeService.deleteFile(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Resume file deleted", null));
    }

    @GetMapping("/analyses/{id}")
    @Operation(summary = "Get detailed report of a specific resume analysis")
    public ResponseEntity<ApiResponse<ResumeAnalysis>> getAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        ResumeAnalysis analysis = resumeService.getAnalysisById(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Analysis report retrieved", analysis));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get currently active uploaded resume metadata")
    public ResponseEntity<ApiResponse<ResumeFile>> getLatestResume(@AuthenticationPrincipal UserPrincipal principal) {
        ResumeFile file = resumeService.getLatestResume(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Latest resume metadata", file));
    }
}
