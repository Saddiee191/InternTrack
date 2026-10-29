package com.interntrack;

import com.interntrack.ai.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiServiceTests {

    private RuleBasedAiServiceImpl aiService;

    @BeforeEach
    void setUp() {
        aiService = new RuleBasedAiServiceImpl();
    }

    @Test
    @DisplayName("Scam detector flags upfront registration fee and Telegram recruiter as HIGH risk")
    void testScamDetectorHighRisk() {
        ScamScanInput input = new ScamScanInput();
        input.setCompanyName("Global Tech Solutions");
        input.setJobTitle("Data Entry Intern");
        input.setRecruiterContact("recruiter@gmail.com on Telegram @globaltech");
        input.setJobDescription("Work from home. Immediate selection without interview. You must pay a mandatory registration fee of Rs. 1500 before offer letter is generated. Contact on Telegram within 24 hours.");
        input.setRecruiterMessage("Kindly pay the security deposit via UPI to confirm your seat.");

        ScamAnalysisResult result = aiService.analyzeScamRisk(input);

        assertNotNull(result);
        assertEquals("HIGH", result.getOverallRiskLevel());
        assertTrue(result.getRiskScore() >= 55, "Risk score should be >= 55 for upfront fee and telegram pressure");
        assertFalse(result.getFindings().isEmpty(), "Findings should highlight indicators");

        boolean foundFeeIndicator = result.getFindings().stream()
                .anyMatch(f -> f.getIndicatorName().contains("Upfront Payment"));
        assertTrue(foundFeeIndicator, "Should detect upfront payment indicator");

        boolean foundTelegramIndicator = result.getFindings().stream()
                .anyMatch(f -> f.getIndicatorName().contains("Chat-App"));
        assertTrue(foundTelegramIndicator, "Should detect Telegram communication indicator");
    }

    @Test
    @DisplayName("Scam detector evaluates legitimate enterprise opportunity as LOW risk")
    void testScamDetectorLowRisk() {
        ScamScanInput input = new ScamScanInput();
        input.setCompanyName("Microsoft India");
        input.setJobTitle("Software Engineer Intern");
        input.setRecruiterContact("careers@microsoft.com");
        input.setApplicationUrl("https://careers.microsoft.com/us/en/job/12345");
        input.setStipendSalary("INR 1,25,000 / month");
        input.setJobDescription("Join our cloud platform team. Design scalable backend services with Java, C++, and Azure microservices. Requires online coding assessment followed by 2 technical interview rounds.");

        ScamAnalysisResult result = aiService.analyzeScamRisk(input);

        assertNotNull(result);
        assertEquals("LOW", result.getOverallRiskLevel());
        assertTrue(result.getRiskScore() < 25, "Legitimate opportunity should have risk score < 25");
        assertTrue(result.getFindings().isEmpty(), "No fraudulent indicators should trigger for normal enterprise listing");
    }

    @Test
    @DisplayName("Resume analyzer extracts skills, detects sections, and scores completeness")
    void testResumeCompletenessAndSkillExtraction() {
        String resumeText = """
                Aravind Nair
                Email: aravind.nair@student.edu | Phone: +91 9876543210
                LinkedIn: https://linkedin.com/in/aravindnair | GitHub: https://github.com/aravindnair
                
                EDUCATION
                National Institute of Technology, B.Tech Computer Science and Engineering, 2026. CGPA: 8.85
                
                TECHNICAL SKILLS
                Languages: Java, Python, C++, TypeScript, SQL
                Frameworks & Tools: Spring Boot, React, Docker, MySQL, Redis, Git, Linux
                Core: Data Structures, Algorithms, REST APIs, Microservices
                
                PROJECTS & EXPERIENCE
                InternTrack - Full-Stack Application Tracker
                - Engineered a resilient placement tracker with Spring Boot, MySQL, and React, serving 500+ active users.
                - Optimized database queries using JDBC indexed queries, resulting in 40% reduction in API response times.
                - Deployed microservices using Docker containers on AWS cloud infrastructure.
                
                CERTIFICATIONS & ACHIEVEMENTS
                - AWS Certified Cloud Practitioner
                - Ranked top 5% in Global Codeforces Round 850
                """;

        String targetJd = "Looking for a Backend Intern with Java, Spring Boot, MySQL, Docker, and Kubernetes experience.";

        ResumeAnalysisResult result = aiService.analyzeResume(resumeText, "Backend Intern", targetJd);

        assertNotNull(result);
        assertTrue(result.getCompletenessScore() >= 80, "Well-structured resume should score >= 80");
        assertTrue(result.getMatchedSkills().contains("Java"));
        assertTrue(result.getMatchedSkills().contains("Spring Boot"));
        assertTrue(result.getMatchedSkills().contains("Docker"));

        // Kubernetes is in target JD but missing in resume
        assertTrue(result.getMissingSkills().contains("Kubernetes"), "Kubernetes should be flagged as missing skill");
    }
}
