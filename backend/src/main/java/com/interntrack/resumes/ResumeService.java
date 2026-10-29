package com.interntrack.resumes;

import com.interntrack.ai.AiService;
import com.interntrack.ai.ResumeAnalysisResult;
import com.interntrack.common.BadRequestException;
import com.interntrack.common.ResourceNotFoundException;
import com.interntrack.opportunities.Opportunity;
import com.interntrack.opportunities.OpportunityDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ResumeService {

    private final ResumeDao resumeDao;
    private final PdfExtractorService pdfExtractorService;
    private final AiService aiService;
    private final OpportunityDao opportunityDao;

    @Value("${app.storage.upload-dir:./storage/resumes}")
    private String uploadDir;

    public ResumeService(ResumeDao resumeDao, PdfExtractorService pdfExtractorService, AiService aiService, OpportunityDao opportunityDao) {
        this.resumeDao = resumeDao;
        this.pdfExtractorService = pdfExtractorService;
        this.aiService = aiService;
        this.opportunityDao = opportunityDao;
    }

    @Transactional
    public ResumeFile uploadResume(Long userId, MultipartFile file) {
        String extractedText = pdfExtractorService.extractTextFromPdf(file);

        // Secure storage outside web root
        try {
            Path targetDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "resume.pdf";
            String safeFileName = UUID.randomUUID().toString() + "_" + originalName.replaceAll("[^a-zA-Z0-9.-]", "_");
            Path targetPath = targetDir.resolve(safeFileName);

            file.transferTo(targetPath.toFile());

            Long id = resumeDao.saveResumeFile(
                    userId,
                    originalName,
                    targetPath.toString(),
                    file.getSize(),
                    file.getContentType() != null ? file.getContentType() : "application/pdf",
                    extractedText
            );

            return resumeDao.findResumeFileById(id, userId).orElseThrow();
        } catch (IOException e) {
            throw new BadRequestException("Failed to store resume securely on filesystem: " + e.getMessage());
        }
    }

    @Transactional
    public ResumeAnalysis analyzeResume(Long userId, ResumeAnalyzeRequest req) {
        ResumeFile resumeFile;
        if (req.getResumeFileId() != null) {
            resumeFile = resumeDao.findResumeFileById(req.getResumeFileId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resume file not found"));
        } else {
            resumeFile = resumeDao.findLatestResumeFileByUserId(userId)
                    .orElseThrow(() -> new BadRequestException("No resume has been uploaded yet. Please upload a PDF resume first."));
        }

        String jobTitle = req.getJobTitle();
        String jobDescription = req.getJobDescription();

        if (req.getOpportunityId() != null) {
            Opportunity opp = opportunityDao.findById(req.getOpportunityId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
            jobTitle = opp.getTitle() + " at " + opp.getCompanyName();
            jobDescription = opp.getDescription();
        }

        ResumeAnalysisResult result = aiService.analyzeResume(resumeFile.getExtractedText(), jobTitle, jobDescription);

        Long analysisId = resumeDao.saveAnalysis(userId, resumeFile.getId(), result, jobTitle, jobDescription);
        return resumeDao.findAnalysisById(analysisId, userId).orElseThrow();
    }

    public List<ResumeAnalysis> getHistory(Long userId) {
        return resumeDao.findAnalysesByUserId(userId);
    }

    public List<ResumeFile> getFiles(Long userId) {
        return resumeDao.findResumeFilesByUserId(userId);
    }

    @Transactional
    public void deleteFile(Long id, Long userId) {
        ResumeFile file = resumeDao.findResumeFileById(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume file not found"));
        if (resumeDao.deleteResumeFile(id, userId) == 0) {
            throw new ResourceNotFoundException("Resume file not found");
        }
        try {
            Files.deleteIfExists(Paths.get(file.getStoragePath()));
        } catch (IOException e) {
            throw new BadRequestException("Resume record was deleted, but its stored file could not be removed");
        }
    }

    public ResumeAnalysis getAnalysisById(Long id, Long userId) {
        return resumeDao.findAnalysisById(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume analysis not found or unauthorized"));
    }

    public ResumeFile getLatestResume(Long userId) {
        return resumeDao.findLatestResumeFileByUserId(userId)
                .orElse(null);
    }
}
