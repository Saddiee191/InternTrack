package com.interntrack.opportunities;

import com.interntrack.applications.Application;
import com.interntrack.applications.ApplicationDao;
import com.interntrack.applications.ApplicationRequest;
import com.interntrack.common.BadRequestException;
import com.interntrack.common.ResourceNotFoundException;
import com.interntrack.users.ProfileDao;
import com.interntrack.users.StudentProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpportunityService {

    private final OpportunityDao opportunityDao;
    private final ProfileDao profileDao;
    private final ApplicationDao applicationDao;

    public OpportunityService(OpportunityDao opportunityDao, ProfileDao profileDao, ApplicationDao applicationDao) {
        this.opportunityDao = opportunityDao;
        this.profileDao = profileDao;
        this.applicationDao = applicationDao;
    }

    public List<Opportunity> listOpportunities(
            String search,
            String location,
            String workplaceType,
            String employmentType,
            String verificationStatus,
            Boolean onlyActive,
            Long userId,
            int limit,
            int offset
    ) {
        return opportunityDao.findAll(search, location, workplaceType, employmentType, verificationStatus, onlyActive, userId, limit, offset);
    }

    public Opportunity getOpportunityById(Long id, Long userId) {
        return opportunityDao.findById(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with ID: " + id));
    }

    @Transactional
    public Opportunity createOpportunity(OpportunityRequest req, Long adminId) {
        Long id = opportunityDao.create(req, adminId);
        return getOpportunityById(id, adminId);
    }

    @Transactional
    public Opportunity updateOpportunity(Long id, OpportunityRequest req, Long adminId) {
        int rows = opportunityDao.update(id, req);
        if (rows == 0) {
            throw new ResourceNotFoundException("Opportunity not found with ID: " + id);
        }
        return getOpportunityById(id, adminId);
    }

    @Transactional
    public void deleteOpportunity(Long id) {
        int rows = opportunityDao.delete(id);
        if (rows == 0) {
            throw new ResourceNotFoundException("Opportunity not found with ID: " + id);
        }
    }

    public void saveOpportunity(Long userId, Long opportunityId) {
        getOpportunityById(opportunityId, userId);
        opportunityDao.saveOpportunity(userId, opportunityId);
    }

    public void unsaveOpportunity(Long userId, Long opportunityId) {
        opportunityDao.unsaveOpportunity(userId, opportunityId);
    }

    public List<Opportunity> getSavedOpportunities(Long userId) {
        return opportunityDao.findSavedByUserId(userId);
    }

    public void reportOpportunity(Long opportunityId, Long reporterId, String reason) {
        getOpportunityById(opportunityId, reporterId);
        opportunityDao.reportOpportunity(opportunityId, reporterId, reason);
    }

    public EligibilityResultDto checkEligibility(Long opportunityId, Long studentUserId) {
        Opportunity opp = getOpportunityById(opportunityId, studentUserId);
        StudentProfile profile = profileDao.findStudentProfileByUserId(studentUserId)
                .orElse(null);

        EligibilityResultDto res = new EligibilityResultDto();
        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        if (profile == null) {
            res.setEligible(false);
            res.setSummary("Student profile is empty. Please complete your academic profile to evaluate eligibility.");
            failed.add("Incomplete profile details (College, Degree, CGPA, Branch)");
            res.setPassedCriteria(passed);
            res.setFailedCriteria(failed);
            return res;
        }

        // 1. CGPA Check
        boolean cgpaPassed = true;
        if (opp.getMinCgpa() != null && opp.getMinCgpa().compareTo(BigDecimal.ZERO) > 0) {
            if (profile.getCgpa() != null && profile.getCgpa().compareTo(opp.getMinCgpa()) >= 0) {
                passed.add("Minimum CGPA: Required " + opp.getMinCgpa() + ", your CGPA is " + profile.getCgpa());
            } else {
                cgpaPassed = false;
                failed.add("Minimum CGPA: Required " + opp.getMinCgpa() + ", your CGPA is " + (profile.getCgpa() != null ? profile.getCgpa() : "Not provided"));
            }
        } else {
            passed.add("CGPA Requirement: Open to all CGPA ranges");
        }
        res.setCgpaEligible(cgpaPassed);

        // 2. Degree Check
        boolean degreePassed = true;
        if (opp.getEligibleDegreesJson() != null && !opp.getEligibleDegreesJson().isBlank()) {
            if (profile.getDegree() != null && opp.getEligibleDegreesJson().toLowerCase().contains(profile.getDegree().toLowerCase().trim())) {
                passed.add("Eligible Degree: " + profile.getDegree() + " satisfies criteria");
            } else {
                degreePassed = false;
                failed.add("Eligible Degree: Requires " + opp.getEligibleDegreesJson() + " (Your degree: " + (profile.getDegree() != null ? profile.getDegree() : "Not specified") + ")");
            }
        } else {
            passed.add("Degree: Open to all degrees");
        }
        res.setDegreeEligible(degreePassed);

        // 3. Branch Check
        boolean branchPassed = true;
        if (opp.getEligibleBranchesJson() != null && !opp.getEligibleBranchesJson().isBlank()) {
            if (profile.getBranch() != null && (
                    opp.getEligibleBranchesJson().toLowerCase().contains("any") ||
                    opp.getEligibleBranchesJson().toLowerCase().contains(profile.getBranch().toLowerCase().trim())
            )) {
                passed.add("Eligible Branch: " + profile.getBranch() + " satisfies criteria");
            } else {
                branchPassed = false;
                failed.add("Eligible Branch: Requires " + opp.getEligibleBranchesJson() + " (Your branch: " + (profile.getBranch() != null ? profile.getBranch() : "Not specified") + ")");
            }
        } else {
            passed.add("Branch: Open to all branches");
        }
        res.setBranchEligible(branchPassed);

        // 4. Batch Check
        boolean batchPassed = true;
        if (opp.getEligibleBatchesJson() != null && !opp.getEligibleBatchesJson().isBlank() && profile.getGraduationYear() != null) {
            if (opp.getEligibleBatchesJson().contains(String.valueOf(profile.getGraduationYear()))) {
                passed.add("Graduating Batch: " + profile.getGraduationYear() + " is eligible");
            } else {
                batchPassed = false;
                failed.add("Graduating Batch: Eligible batches " + opp.getEligibleBatchesJson() + " (Your batch: " + profile.getGraduationYear() + ")");
            }
        } else if (profile.getGraduationYear() == null) {
            batchPassed = false;
            failed.add("Graduation year missing in your student profile");
        } else {
            passed.add("Batch Requirement: Open to all graduating batches");
        }
        res.setBatchEligible(batchPassed);

        boolean overall = cgpaPassed && degreePassed && branchPassed && batchPassed;
        res.setEligible(overall);
        res.setPassedCriteria(passed);
        res.setFailedCriteria(failed);
        res.setSummary(overall
                ? "You satisfy all academic and eligibility criteria for this position!"
                : "You do not meet one or more academic eligibility criteria specified by the employer.");

        return res;
    }

    @Transactional
    public Application applyFromOpportunity(Long opportunityId, Long studentUserId) {
        Opportunity opp = getOpportunityById(opportunityId, studentUserId);

        if (opportunityDao.hasApplied(studentUserId, opportunityId)) {
            throw new BadRequestException("You have already added or tracked an application for " + opp.getCompanyName() + " (" + opp.getTitle() + ").");
        }

        ApplicationRequest req = new ApplicationRequest();
        req.setOpportunityId(opportunityId);
        req.setCompanyName(opp.getCompanyName());
        req.setRole(opp.getTitle());
        req.setJobDescription(opp.getDescription());
        req.setSourceUrl(opp.getApplyUrl());
        req.setStipendSalary(opp.getStipendSalary());
        req.setLocation(opp.getLocation());
        req.setEmploymentType(opp.getEmploymentType());
        req.setStatus("APPLIED");
        req.setAppliedDate(java.time.LocalDate.now());
        req.setDeadline(opp.getDeadline());
        req.setNotes("Created directly from InternTrack Opportunity Board.");

        Long appId = applicationDao.create(studentUserId, req);
        return applicationDao.findByIdAndUserId(appId, studentUserId).orElseThrow();
    }
}
