package com.interntrack.referrals;

import com.interntrack.common.BadRequestException;
import com.interntrack.common.ResourceNotFoundException;
import com.interntrack.users.MentorProfile;
import com.interntrack.users.ProfileDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReferralService {

    private final ReferralDao referralDao;
    private final ProfileDao profileDao;

    public ReferralService(ReferralDao referralDao, ProfileDao profileDao) {
        this.referralDao = referralDao;
        this.profileDao = profileDao;
    }

    @Transactional
    public ReferralRequest createRequest(Long studentId, CreateReferralRequestDto req) {
        if (studentId.equals(req.getMentorId())) {
            throw new BadRequestException("You cannot request a referral from yourself");
        }

        MentorProfile mentor = profileDao.findMentorProfileByUserId(req.getMentorId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with ID: " + req.getMentorId()));

        if (Boolean.FALSE.equals(mentor.getOpenForReferrals())) {
            throw new BadRequestException("This mentor is currently not accepting new referral requests.");
        }

        Long id = referralDao.createRequest(studentId, req);
        return referralDao.findById(id).orElseThrow();
    }

    public List<ReferralRequest> getStudentRequests(Long studentId) {
        return referralDao.findByStudentId(studentId);
    }

    public List<ReferralRequest> getMentorRequests(Long mentorId) {
        return referralDao.findByMentorId(mentorId);
    }

    @Transactional
    public ReferralRequest updateStatus(Long id, Long mentorId, String status, String mentorResponse) {
        int rows = referralDao.updateStatus(id, mentorId, status, mentorResponse);
        if (rows == 0) {
            throw new ResourceNotFoundException("Referral request not found or unauthorized to update");
        }
        return referralDao.findById(id).orElseThrow();
    }
}
