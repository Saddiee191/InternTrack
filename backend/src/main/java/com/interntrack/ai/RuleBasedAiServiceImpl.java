




























package com.interntrack.ai;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("ruleBasedAiService")
public class RuleBasedAiServiceImpl implements AiService {

    // Common technical skills dictionary
    private static final Set<String> KNOWN_SKILLS = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    static {
        KNOWN_SKILLS.addAll(List.of(
                "Java", "Python", "C++", "C#", "C", "JavaScript", "TypeScript", "Go", "Rust", "Kotlin", "Swift",
                "React", "Angular", "Vue", "Next.js", "Node.js", "Express", "Spring", "Spring Boot", "Django", "FastAPI",
                "HTML", "CSS", "Tailwind CSS", "Bootstrap", "Redux", "GraphQL", "REST APIs", "Microservices",
                "SQL", "MySQL", "PostgreSQL", "MongoDB", "Redis", "Elasticsearch", "Cassandra", "Oracle",
                "AWS", "Azure", "GCP", "Docker", "Kubernetes", "Linux", "Git", "GitHub", "CI/CD", "Jenkins",
                "Data Structures", "Algorithms", "Object-Oriented Programming", "System Design", "Operating Systems",
                "Machine Learning", "Deep Learning", "PyTorch", "TensorFlow", "Pandas", "NumPy", "Scikit-Learn",
                "Cybersecurity", "Networking", "Computer Networks", "Agile", "Scrum", "Unit Testing", "JUnit"
        ));
    }

    // High-impact action verbs for resume bullets
    private static final List<String> ACTION_VERBS = List.of(
            "built", "designed", "developed", "engineered", "implemented", "optimized", "architected",
            "deployed", "created", "spearheaded", "integrated", "automated", "reduced", "increased", "improved"
    );

    @Override
    public ResumeAnalysisResult analyzeResume(String resumeText, String jobTitle, String jobDescription) {
        ResumeAnalysisResult result = new ResumeAnalysisResult();
        result.setProviderType("RULE_BASED");

        String lower = resumeText.toLowerCase();
        List<ResumeFinding> findings = new ArrayList<>();
        int completeness = 0;

        // 1. Contact Information Analysis (15 points)
        boolean hasEmail = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").matcher(resumeText).find();
        boolean hasPhone = Pattern.compile("(\\+?[0-9]{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}").matcher(resumeText).find();
        boolean hasLinks = lower.contains("linkedin.com") || lower.contains("github.com") || lower.contains("http");

        int contactScore = 0;
        if (hasEmail) contactScore += 5;
        if (hasPhone) contactScore += 5;
        if (hasLinks) contactScore += 5;
        completeness += contactScore;

        if (hasEmail && hasPhone && hasLinks) {
            findings.add(new ResumeFinding("Contact Info", "STRENGTH", "Complete Contact Information",
                    "Email, phone number, and professional profile links (LinkedIn/GitHub) were clearly identified.",
                    "Keep your portfolio and profile links updated with your latest projects."));
        } else {
            List<String> missingItems = new ArrayList<>();
            if (!hasEmail) missingItems.add("Email address");
            if (!hasPhone) missingItems.add("Phone number");
            if (!hasLinks) missingItems.add("LinkedIn or GitHub profile link");
            findings.add(new ResumeFinding("Contact Info", "IMPROVEMENT", "Missing Contact Details",
                    "The resume appears to be missing: " + String.join(", ", missingItems) + ".",
                    "Ensure your professional contact header includes your full name, email, phone number, and clickable GitHub/LinkedIn links."));
        }

        // 2. Education Section (20 points)
        boolean hasEdu = lower.contains("education") || lower.contains("b.tech") || lower.contains("b.e.") ||
                lower.contains("degree") || lower.contains("university") || lower.contains("college") || lower.contains("institute");
        boolean hasCgpa = lower.contains("cgpa") || lower.contains("gpa") || lower.contains("percentage") || Pattern.compile("\\b[0-9]\\.[0-9]{1,2}\\b").matcher(resumeText).find();

        if (hasEdu) {
            completeness += 15;
            if (hasCgpa) {
                completeness += 5;
                findings.add(new ResumeFinding("Education", "STRENGTH", "Education & Academic Score Present",
                        "Degree, institution, and academic score (CGPA/GPA) are well structured.",
                        "Highlight coursework relevant to target roles (e.g., Operating Systems, DBMS, Algorithms)."));
            } else {
                findings.add(new ResumeFinding("Education", "IMPROVEMENT", "Include Academic Standing",
                        "Degree and college detected, but academic score (CGPA/GPA) was not clearly recognized.",
                        "If your CGPA is above 7.0 or 3.0/4.0, clearly state it alongside your graduation batch."));
            }
        } else {
            findings.add(new ResumeFinding("Education", "MISSING", "Education Section Unclear",
                    "No standard 'Education' header or degree keywords found.",
                    "Add an explicit 'Education' section stating degree, branch, institution name, and graduating year."));
        }

        // 3. Technical Skills Extraction (25 points)
        List<String> detectedSkills = new ArrayList<>();
        for (String skill : KNOWN_SKILLS) {
            Pattern p = Pattern.compile("\\b" + Pattern.quote(skill) + "\\b", Pattern.CASE_INSENSITIVE);
            if (p.matcher(resumeText).find()) {
                detectedSkills.add(skill);
            }
        }
        result.setMatchedSkills(detectedSkills);

        if (detectedSkills.size() >= 8) {
            completeness += 25;
            findings.add(new ResumeFinding("Skills", "STRENGTH", "Diverse Technical Skillset",
                    "Found " + detectedSkills.size() + " technical skills including: " + String.join(", ", detectedSkills.subList(0, Math.min(6, detectedSkills.size()))) + "...",
                    "Organize your skills into categorized groups such as Languages, Frameworks, Databases, and Developer Tools."));
        } else if (detectedSkills.size() >= 4) {
            completeness += 15;
            findings.add(new ResumeFinding("Skills", "IMPROVEMENT", "Expand Core Skills",
                    "Identified " + detectedSkills.size() + " skills. Recruiters look for full-stack, cloud, or tooling breadth.",
                    "Add foundational tools you use like Git, Linux, Docker, or relational databases (MySQL, PostgreSQL)."));
        } else {
            completeness += 5;
            findings.add(new ResumeFinding("Skills", "WARNING", "Low Skill Keyword Density",
                    "Only " + detectedSkills.size() + " recognized technical keywords were extracted.",
                    "Create a dedicated 'Technical Skills' section listing programming languages, frameworks, and developer tools."));
        }

        // 4. Projects and Experience (25 points)
        boolean hasProjects = lower.contains("projects") || lower.contains("project");
        boolean hasExp = lower.contains("experience") || lower.contains("internship") || lower.contains("work experience");

        int verbCount = 0;
        for (String verb : ACTION_VERBS) {
            if (lower.contains(" " + verb + " ")) verbCount++;
        }

        boolean hasMetrics = Pattern.compile("\\b(\\d+%(?:\\s*increase|\\s*decrease|\\s*reduction)?|\\d+\\s*(?:ms|users|requests|qps|stars))\\b", Pattern.CASE_INSENSITIVE).matcher(resumeText).find();

        if (hasProjects || hasExp) {
            completeness += 15;
            if (verbCount >= 3) completeness += 5;
            if (hasMetrics) completeness += 5;

            if (verbCount >= 3 && hasMetrics) {
                findings.add(new ResumeFinding("Projects & Experience", "STRENGTH", "Action-Oriented & Metric-Driven",
                        "Bullet points use strong action verbs and quantifiable results/metrics.",
                        "Continue emphasizing business or performance impact (e.g. latency reduced by 30%, served 500+ users)."));
            } else {
                findings.add(new ResumeFinding("Projects & Experience", "IMPROVEMENT", "Quantify Project Achievements",
                        "Projects are described, but lack quantifiable impact metrics or action verbs.",
                        "Use the XYZ formula: 'Accomplished [X] as measured by [Y], by doing [Z]'. Example: 'Reduced API response time by 40% using Redis caching'."));
            }
        } else {
            findings.add(new ResumeFinding("Projects & Experience", "MISSING", "No Projects or Experience Found",
                    "Could not locate an explicit 'Projects' or 'Experience' section.",
                    "Add at least two detailed software engineering or technical projects showcasing modern frameworks."));
        }

        // 5. Certifications & Achievements (15 points)
        boolean hasCert = lower.contains("certification") || lower.contains("certified") || lower.contains("achievements") || lower.contains("awards") || lower.contains("hackathon");
        if (hasCert) {
            completeness += 15;
            findings.add(new ResumeFinding("Certifications & Honors", "STRENGTH", "Recognized Achievements",
                    "Certifications, competitive programming rankings, or hackathon honors detected.",
                    "Include issuer names and credential verification IDs or links where possible."));
        } else {
            findings.add(new ResumeFinding("Certifications & Honors", "IMPROVEMENT", "Add External Validation",
                    "No certifications, hackathons, or competitive coding profiles detected.",
                    "Consider adding relevant certifications (AWS, Azure, Oracle Java) or achievements from LeetCode, Codeforces, or university hackathons."));
        }

        result.setCompletenessScore(Math.min(100, completeness));

        // 6. Job Description Keyword Matching
        List<String> missingSkills = new ArrayList<>();
        int matchPct = 0;

        if (jobDescription != null && !jobDescription.isBlank()) {
            List<String> jdSkills = new ArrayList<>();
            for (String skill : KNOWN_SKILLS) {
                Pattern p = Pattern.compile("\\b" + Pattern.quote(skill) + "\\b", Pattern.CASE_INSENSITIVE);
                if (p.matcher(jobDescription).find()) {
                    jdSkills.add(skill);
                }
            }

            int matchedCount = 0;
            for (String reqSkill : jdSkills) {
                if (detectedSkills.stream().anyMatch(s -> s.equalsIgnoreCase(reqSkill))) {
                    matchedCount++;
                } else {
                    missingSkills.add(reqSkill);
                }
            }

            if (!jdSkills.isEmpty()) {
                matchPct = (int) Math.round(((double) matchedCount / jdSkills.size()) * 100);
            } else {
                matchPct = 70; // Generic baseline if no specific skills in JD
            }

            result.setMatchPercentage(matchPct);
            result.setMissingSkills(missingSkills);

            if (!missingSkills.isEmpty()) {
                findings.add(new ResumeFinding("Target Role Relevance", "IMPROVEMENT", "Key Job Skills Missing from Resume",
                        "The target job emphasizes these skills which were not found in your resume: " + String.join(", ", missingSkills) + ".",
                        "If you have academic or project experience with these technologies, ensure they are explicitly listed in your resume."));
            } else {
                findings.add(new ResumeFinding("Target Role Relevance", "STRENGTH", "Strong Skill Alignment",
                        "Your resume covers the key technical qualifications specified in the job description.",
                        "Tailor your project bullet points to showcase how you applied these specific technologies."));
            }
        } else {
            // General ATS standard match
            result.setMatchPercentage(Math.min(100, completeness));
            result.setMissingSkills(List.of());
        }

        result.setFindings(findings);
        result.setSummary(String.format("Resume completeness score is %d/100 with a %d%% skill match for the specified role. %d actionable suggestions generated.",
                result.getCompletenessScore(), result.getMatchPercentage(), findings.size()));

        return result;
    }

    @Override
    public ScamAnalysisResult analyzeScamRisk(ScamScanInput input) {
        ScamAnalysisResult result = new ScamAnalysisResult();
        result.setProviderType("RULE_BASED");
        result.setDisclaimer("IMPORTANT SAFETY NOTICE: This automated assessment provides preliminary risk indicators based on text pattern heuristics. It does not constitute a legal determination or factual conclusion. Always independently verify offers via the company's verified careers portal.");

        StringBuilder combined = new StringBuilder();
        if (input.getCompanyName() != null) combined.append(input.getCompanyName()).append(" ");
        if (input.getJobTitle() != null) combined.append(input.getJobTitle()).append(" ");
        if (input.getRecruiterContact() != null) combined.append(input.getRecruiterContact()).append(" ");
        if (input.getApplicationUrl() != null) combined.append(input.getApplicationUrl()).append(" ");
        if (input.getStipendSalary() != null) combined.append(input.getStipendSalary()).append(" ");
        if (input.getJobDescription() != null) combined.append(input.getJobDescription()).append(" ");
        if (input.getRecruiterMessage() != null) combined.append(input.getRecruiterMessage()).append(" ");

        String text = combined.toString().toLowerCase();
        List<ScamFinding> findings = new ArrayList<>();
        int score = 0;

        // Heuristic 1: Upfront Fee / Registration / Security Deposit (Weight: 35)
        Pattern feePattern = Pattern.compile("(registration\\s+fee|security\\s+deposit|processing\\s+fee|refundable\\s+fee|pay\\s+(?:rs|inr|usd|\\$|amount)|fee\\s+required|initial\\s+deposit)");
        Matcher feeMatcher = feePattern.matcher(text);
        if (feeMatcher.find()) {
            score += 35;
            findings.add(new ScamFinding("Upfront Payment / Fee Requested", 35,
                    "Detected phrase: '" + feeMatcher.group() + "'",
                    "Legitimate companies and campus recruiters NEVER ask candidates to pay registration fees, security deposits, or processing charges for an internship or job offer.",
                    "Do NOT transfer any money. Immediately discontinue communication."));
        }

        // Heuristic 2: Paid Training Before Offer / Course Purchase (Weight: 30)
        Pattern trainingPattern = Pattern.compile("(mandatory\\s+training\\s+fee|pay\\s+for\\s+training|purchase\\s+course|training\\s+charges|buy\\s+our\\s+certificate)");
        Matcher trainingMatcher = trainingPattern.matcher(text);
        if (trainingMatcher.find()) {
            score += 30;
            findings.add(new ScamFinding("Mandatory Paid Training Scheme", 30,
                    "Detected phrase: '" + trainingMatcher.group() + "'",
                    "Fraudulent entities often disguise training course sales as guaranteed job offers. Genuine internships provide free mentorship or paid stipends.",
                    "Verify if the company is an accredited employer or an ed-tech firm marketing training programs."));
        }

        // Heuristic 3: Anonymous or Chat-Only Communication (Weight: 25)
        Pattern chatPattern = Pattern.compile("(\\btelegram\\b|\\bwhatsapp\\s+only\\b|\\bt\\.me/|\\bwa\\.me/|contact\\s+on\\s+telegram)");
        Matcher chatMatcher = chatPattern.matcher(text);
        if (chatMatcher.find()) {
            score += 25;
            findings.add(new ScamFinding("Exclusive Chat-App Communication", 25,
                    "Detected: '" + chatMatcher.group() + "'",
                    "Recruiters conducting interviews or hiring solely via Telegram or personal WhatsApp without an official corporate email domain is a major red flag.",
                    "Request official correspondence from an enterprise email domain (e.g., name@company.com)."));
        }

        // Heuristic 4: Suspicious / Free Email Domains for Named Companies (Weight: 20)
        if (input.getRecruiterContact() != null) {
            String contactLower = input.getRecruiterContact().toLowerCase();
            if ((contactLower.contains("@gmail.com") || contactLower.contains("@yahoo.com") || contactLower.contains("@hotmail.com")) &&
                input.getCompanyName() != null && !input.getCompanyName().isBlank() &&
                !input.getCompanyName().toLowerCase().contains("freelance")) {
                score += 20;
                findings.add(new ScamFinding("Public / Free Email Domain for Stated Company", 20,
                        "Recruiter contact: '" + input.getRecruiterContact() + "' for company '" + input.getCompanyName() + "'",
                        "Recruiters claiming to represent established brands using generic free email accounts (e.g., @gmail.com) is a common impersonation tactic.",
                        "Check the company's official careers site to confirm if this recruiter is listed or recognized."));
            }
        }

        // Heuristic 5: Urgent Pressure Tactics (Weight: 25)
        Pattern urgencyPattern = Pattern.compile("(within\\s+(?:2|3|4|12|24)\\s+hours|immediate\\s+payment|offer\\s+will\\s+be\\s+cancelled|urgent\\s+seat|limited\\s+slots\\s+remaining)");
        Matcher urgencyMatcher = urgencyPattern.matcher(text);
        if (urgencyMatcher.find()) {
            score += 25;
            findings.add(new ScamFinding("Urgent Pressure Tactics", 25,
                    "Detected: '" + urgencyMatcher.group() + "'",
                    "Scammers create artificial urgency to pressure students into making impulsive financial or personal information disclosures without proper verification.",
                    "Take your time. Do not be intimidated by artificial countdowns or cancellation threats."));
        }

        // Heuristic 6: Requests for Sensitive Banking or Credential Details (Weight: 35)
        Pattern sensitivePattern = Pattern.compile("(bank\\s+pin|atm\\s+pin|cvv|debit\\s+card|otp|original\\s+certificates|surrender\\s+documents|password)");
        Matcher sensitiveMatcher = sensitivePattern.matcher(text);
        if (sensitiveMatcher.find()) {
            score += 35;
            findings.add(new ScamFinding("Request for Confidential Credentials", 35,
                    "Detected: '" + sensitiveMatcher.group() + "'",
                    "No legitimate employer will ever ask for card CVVs, bank PINs, OTPs, or require surrendering original educational degree certificates.",
                    "Never share financial credentials or original certificates. Report to cybercrime authorities if demanded."));
        }

        // Heuristic 7: Unrealistic Salary Claims (Weight: 20)
        Pattern salaryPattern = Pattern.compile("((?:rs\\.?|inr|\\$)\\s*(?:[5-9]\\d|\\d{3})[,.]?\\d{3}\\s*(?:per\\s+day|daily|weekly)|earn\\s+(?:50000|100000)\\s+per\\s+week)");
        Matcher salaryMatcher = salaryPattern.matcher(text);
        if (salaryMatcher.find()) {
            score += 20;
            findings.add(new ScamFinding("Unrealistic Compensation for Minimal Qualifications", 20,
                    "Detected compensation phrase: '" + salaryMatcher.group() + "'",
                    "Offers promising disproportionately high earnings (e.g., INR 50,000/week) for basic tasks like typing, liking videos, or data entry are classic phishing lures.",
                    "Compare the compensation against industry standard internship benchmarks on Glassdoor or LinkedIn."));
        }

        // Heuristic 8: Suspicious Domain / Shortener (Weight: 20)
        if (input.getApplicationUrl() != null) {
            String url = input.getApplicationUrl().toLowerCase();
            if (url.contains("bit.ly") || url.contains("tinyurl.com") || url.contains(".xyz") || url.contains(".top") || url.contains(".buzz") || url.contains("forms.gle")) {
                score += 20;
                findings.add(new ScamFinding("Obfuscated or Suspicious Application URL", 20,
                        "Application URL: '" + input.getApplicationUrl() + "'",
                        "Use of generic URL shorteners or cheap top-level domains (.xyz, .top) instead of corporate domains suggests an unverified or transient landing page.",
                        "Do not enter passwords or sensitive information on shortlink or unverified Google Form destinations."));
            }
        }

        // Heuristic 9: Direct Offer Without Interview (Weight: 25)
        Pattern noInterviewPattern = Pattern.compile("(selected\\s+without\\s+interview|direct\\s+selection|instant\\s+offer\\s+letter|no\\s+test\\s+or\\s+interview)");
        Matcher noInterviewMatcher = noInterviewPattern.matcher(text);
        if (noInterviewMatcher.find()) {
            score += 25;
            findings.add(new ScamFinding("Offer Issued Without Formal Evaluation", 25,
                    "Detected: '" + noInterviewMatcher.group() + "'",
                    "Receiving an internship or employment offer without any technical assessment, coding test, or structured interview is a hallmark indicator of recruitment fraud.",
                    "Inquire about the technical team, role scope, and formal evaluation process before proceeding."));
        }

        // Cap risk score at 100
        int finalScore = Math.min(100, score);
        result.setRiskScore(finalScore);

        if (finalScore >= 55) {
            result.setOverallRiskLevel("HIGH");
        } else if (finalScore >= 25) {
            result.setOverallRiskLevel("MEDIUM");
        } else {
            result.setOverallRiskLevel("LOW");
        }

        // Standard precautionary steps
        List<String> steps = new ArrayList<>();
        steps.add("Search the official corporate careers portal directly via a web browser; do not follow links provided in unexpected messages.");
        steps.add("Check company registration on the Ministry of Corporate Affairs (MCA) or official national registrar.");
        steps.add("Connect with verified current employees of the organization on LinkedIn to confirm if the recruiter is legitimate.");
        steps.add("Never send money, purchase gift cards, or provide bank OTPs under any circumstances.");
        steps.add("Report any demand for money to your college placement cell and the InternTrack platform administrator.");

        result.setRecommendedSteps(steps);
        result.setFindings(findings);

        return result;
    }
}
