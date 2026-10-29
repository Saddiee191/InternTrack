package com.interntrack.auth;

import com.interntrack.common.BadRequestException;
import com.interntrack.common.ResourceNotFoundException;
import com.interntrack.common.UnauthorizedException;
import com.interntrack.security.JwtUtil;
import com.interntrack.users.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserDao userDao;
    private final ProfileDao profileDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserDao userDao, ProfileDao profileDao, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userDao = userDao;
        this.profileDao = profileDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String role = (request.getRole() != null && !request.getRole().isBlank()) 
                ? request.getRole().trim().toUpperCase() : "STUDENT";

        // Never allow public self-assignment of ADMIN role
        if ("ADMIN".equals(role)) {
            throw new BadRequestException("Administrator accounts cannot be registered via public registration.");
        }
        if (!"STUDENT".equals(role) && !"MENTOR".equals(role)) {
            throw new BadRequestException("Invalid role specified. Role must be STUDENT or MENTOR.");
        }

        String email = request.getEmail().trim().toLowerCase();
        if (userDao.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setRole(role);
        user.setStatus("ACTIVE");

        Long userId = userDao.createUser(user);

        // Initialize role-specific profile
        Integer completionPct = 0;
        if ("STUDENT".equals(role)) {
            StudentProfile sp = new StudentProfile();
            sp.setUserId(userId);
            sp.setCollegeName(request.getCollegeName());
            sp.setDegree(request.getDegree());
            sp.setBranch(request.getBranch());
            sp.setGraduationYear(request.getGraduationYear());
            profileDao.upsertStudentProfile(sp);
            completionPct = sp.getProfileCompletionPct();
        } else if ("MENTOR".equals(role)) {
            MentorProfile mp = new MentorProfile();
            mp.setUserId(userId);
            mp.setCollegeName(request.getCollegeName());
            mp.setCurrentCompany(request.getCurrentCompany());
            mp.setCurrentRole(request.getCurrentRole());
            mp.setOpenForReferrals(true);
            mp.setIsVerifiedMentor(false);
            profileDao.upsertMentorProfile(mp);
        }

        String token = jwtUtil.generateToken(userId, email, role, user.getFullName());
        return new AuthResponse(token, userId, email, user.getFullName(), role, user.getAvatarUrl(), completionPct);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new UnauthorizedException("This account is not active. Please contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        Integer completionPct = null;
        if ("STUDENT".equals(user.getRole())) {
            Optional<StudentProfile> sp = profileDao.findStudentProfileByUserId(user.getId());
            completionPct = sp.map(StudentProfile::getProfileCompletionPct).orElse(0);
        }

        String token = jwtUtil.generateToken(user.getId(), email, user.getRole(), user.getFullName());
        return new AuthResponse(token, user.getId(), email, user.getFullName(), user.getRole(), user.getAvatarUrl(), completionPct);
    }

    public AuthResponse getCurrentUser(Long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Integer completionPct = null;
        if ("STUDENT".equals(user.getRole())) {
            Optional<StudentProfile> sp = profileDao.findStudentProfileByUserId(user.getId());
            completionPct = sp.map(StudentProfile::getProfileCompletionPct).orElse(0);
        }

        return new AuthResponse(null, user.getId(), user.getEmail(), user.getFullName(), user.getRole(), user.getAvatarUrl(), completionPct);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match");
        }

        userDao.updatePassword(userId, passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void deleteAccount(Long userId) {
        userDao.softDeleteUser(userId);
    }
}
