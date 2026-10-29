package com.interntrack.ai;

public interface AiService {
    ResumeAnalysisResult analyzeResume(String resumeText, String jobTitle, String jobDescription);
    ScamAnalysisResult analyzeScamRisk(ScamScanInput input);
}
