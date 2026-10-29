package com.interntrack;

import com.interntrack.applications.ApplicationDao;
import com.interntrack.common.BadRequestException;
import com.interntrack.opportunities.*;
import com.interntrack.users.ProfileDao;
import com.interntrack.users.StudentProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceTests {

    @Mock
    private OpportunityDao opportunityDao;

    @Mock
    private ProfileDao profileDao;

    @Mock
    private ApplicationDao applicationDao;

    @InjectMocks
    private OpportunityService opportunityService;

    @Test
    @DisplayName("Eligibility check correctly evaluates student profile against criteria")
    void testEligibilityCheck() {
        Long oppId = 1L;
        Long studentId = 4L;

        Opportunity opp = new Opportunity();
        opp.setId(oppId);
        opp.setTitle("Software Engineer Intern");
        opp.setMinCgpa(BigDecimal.valueOf(8.00));
        opp.setEligibleDegreesJson("[\"B.Tech\", \"M.Tech\"]");
        opp.setEligibleBranchesJson("[\"Computer Science and Engineering\"]");
        opp.setEligibleBatchesJson("[2026]");

        StudentProfile profile = new StudentProfile();
        profile.setUserId(studentId);
        profile.setCgpa(BigDecimal.valueOf(8.85));
        profile.setDegree("B.Tech");
        profile.setBranch("Computer Science and Engineering");
        profile.setGraduationYear(2026);

        when(opportunityDao.findById(oppId, studentId)).thenReturn(Optional.of(opp));
        when(profileDao.findStudentProfileByUserId(studentId)).thenReturn(Optional.of(profile));

        EligibilityResultDto res = opportunityService.checkEligibility(oppId, studentId);

        assertNotNull(res);
        assertTrue(res.isEligible(), "Student should be marked eligible");
        assertTrue(res.isCgpaEligible());
        assertTrue(res.isDegreeEligible());
        assertTrue(res.isBranchEligible());
        assertTrue(res.isBatchEligible());
        assertTrue(res.getFailedCriteria().isEmpty());
    }

    @Test
    @DisplayName("Prevent accidental duplicate application submission")
    void testPreventDuplicateApplication() {
        Long oppId = 1L;
        Long studentId = 4L;

        Opportunity opp = new Opportunity();
        opp.setId(oppId);
        opp.setTitle("Software Engineer Intern");
        opp.setCompanyName("Microsoft");

        when(opportunityDao.findById(oppId, studentId)).thenReturn(Optional.of(opp));
        when(opportunityDao.hasApplied(studentId, oppId)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> opportunityService.applyFromOpportunity(oppId, studentId));
        verify(applicationDao, never()).create(any(), any());
    }
}
