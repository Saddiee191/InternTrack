package com.interntrack.users;

import com.interntrack.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfileService {

    private final UserDao userDao;
    private final ProfileDao profileDao;

    public ProfileService(UserDao userDao, ProfileDao profileDao) {
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    public ProfileResponse getProfile(Long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        StudentProfile studentProfile = null;
        MentorProfile mentorProfile = null;

        if ("STUDENT".equals(user.getRole())) {
            studentProfile = profileDao.findStudentProfileByUserId(userId).orElse(null);
        } else if ("MENTOR".equals(user.getRole())) {
            mentorProfile = profileDao.findMentorProfileByUserId(userId).orElse(null);
        }

        return new ProfileResponse(user, studentProfile, mentorProfile);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            userDao.updateProfileBasics(userId, request.getFullName().trim(), request.getAvatarUrl());
            user.setFullName(request.getFullName().trim());
            user.setAvatarUrl(request.getAvatarUrl());
        }

        StudentProfile studentProfile = null;
        MentorProfile mentorProfile = null;

        if ("STUDENT".equals(user.getRole())) {
            studentProfile = profileDao.findStudentProfileByUserId(userId).orElse(new StudentProfile());
            studentProfile.setUserId(userId);
            if (request.getCollegeName() != null) studentProfile.setCollegeName(request.getCollegeName());
            if (request.getDegree() != null) studentProfile.setDegree(request.getDegree());
            if (request.getBranch() != null) studentProfile.setBranch(request.getBranch());
            if (request.getGraduationYear() != null) studentProfile.setGraduationYear(request.getGraduationYear());
            if (request.getCgpa() != null) studentProfile.setCgpa(request.getCgpa());
            if (request.getSkillsJson() != null) studentProfile.setSkillsJson(request.getSkillsJson());
            if (request.getInterestsJson() != null) studentProfile.setInterestsJson(request.getInterestsJson());
            if (request.getPreferredRolesJson() != null) studentProfile.setPreferredRolesJson(request.getPreferredRolesJson());
            if (request.getPreferredLocationsJson() != null) studentProfile.setPreferredLocationsJson(request.getPreferredLocationsJson());
            if (request.getPortfolioUrl() != null) studentProfile.setPortfolioUrl(request.getPortfolioUrl());
            if (request.getGithubUrl() != null) studentProfile.setGithubUrl(request.getGithubUrl());
            if (request.getLinkedinUrl() != null) studentProfile.setLinkedinUrl(request.getLinkedinUrl());

            profileDao.upsertStudentProfile(studentProfile);
        } else if ("MENTOR".equals(user.getRole())) {
            mentorProfile = profileDao.findMentorProfileByUserId(userId).orElse(new MentorProfile());
            mentorProfile.setUserId(userId);
            if (request.getCollegeName() != null) mentorProfile.setCollegeName(request.getCollegeName());
            if (request.getGraduatingBatch() != null) mentorProfile.setGraduatingBatch(request.getGraduatingBatch());
            if (request.getFieldOfWork() != null) mentorProfile.setFieldOfWork(request.getFieldOfWork());
            if (request.getCurrentCompany() != null) mentorProfile.setCurrentCompany(request.getCurrentCompany());
            if (request.getCurrentRole() != null) mentorProfile.setCurrentRole(request.getCurrentRole());
            if (request.getYearsOfExperience() != null) mentorProfile.setYearsOfExperience(request.getYearsOfExperience());
            if (request.getSkillsJson() != null) mentorProfile.setSkillsJson(request.getSkillsJson());
            if (request.getBio() != null) mentorProfile.setBio(request.getBio());
            if (request.getLinkedinUrl() != null) mentorProfile.setLinkedinUrl(request.getLinkedinUrl());
            if (request.getOpenForReferrals() != null) mentorProfile.setOpenForReferrals(request.getOpenForReferrals());

            profileDao.upsertMentorProfile(mentorProfile);
        }

        return new ProfileResponse(user, studentProfile, mentorProfile);
    }

    public List<MentorProfile> listMentors(int limit, int offset, String search) {
        return profileDao.listMentors(limit, offset, search);
    }

    public MentorProfile getMentorById(Long mentorId) {
        return profileDao.findMentorProfileByUserId(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor profile not found with ID: " + mentorId));
    }
}
