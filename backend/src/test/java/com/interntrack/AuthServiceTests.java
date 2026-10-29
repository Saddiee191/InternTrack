package com.interntrack;

import com.interntrack.auth.AuthResponse;
import com.interntrack.auth.AuthService;
import com.interntrack.auth.LoginRequest;
import com.interntrack.auth.RegisterRequest;
import com.interntrack.common.BadRequestException;
import com.interntrack.common.UnauthorizedException;
import com.interntrack.security.JwtUtil;
import com.interntrack.users.ProfileDao;
import com.interntrack.users.User;
import com.interntrack.users.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserDao userDao;

    @Mock
    private ProfileDao profileDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validStudentRequest;

    @BeforeEach
    void setUp() {
        validStudentRequest = new RegisterRequest();
        validStudentRequest.setEmail("test.student@college.edu");
        validStudentRequest.setPassword("securePassword123");
        validStudentRequest.setFullName("Test Student");
        validStudentRequest.setRole("STUDENT");
        validStudentRequest.setCollegeName("NIT");
        validStudentRequest.setDegree("B.Tech");
        validStudentRequest.setBranch("CSE");
        validStudentRequest.setGraduationYear(2026);
    }

    @Test
    @DisplayName("Public registration rejects attempts to self-assign ADMIN role")
    void testRegisterAdminRejected() {
        RegisterRequest adminReq = new RegisterRequest();
        adminReq.setEmail("hacker@interntrack.edu");
        adminReq.setPassword("password123");
        adminReq.setFullName("Unauthorized Admin");
        adminReq.setRole("ADMIN");

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(adminReq));
        assertTrue(ex.getMessage().contains("Administrator accounts cannot be registered via public registration"));
        verify(userDao, never()).createUser(any());
    }

    @Test
    @DisplayName("Student registration successfully hashes password and returns AuthResponse")
    void testRegisterStudentSuccess() {
        when(userDao.existsByEmail("test.student@college.edu")).thenReturn(false);
        when(passwordEncoder.encode("securePassword123")).thenReturn("$2a$10$hashedPasswordSample");
        when(userDao.createUser(any(User.class))).thenReturn(100L);
        when(jwtUtil.generateToken(eq(100L), eq("test.student@college.edu"), eq("STUDENT"), eq("Test Student")))
                .thenReturn("mocked.jwt.token");

        AuthResponse response = authService.register(validStudentRequest);

        assertNotNull(response);
        assertEquals("test.student@college.edu", response.getEmail());
        assertEquals("STUDENT", response.getRole());
        assertEquals("mocked.jwt.token", response.getToken());
        verify(userDao, times(1)).createUser(any(User.class));
        verify(profileDao, times(1)).upsertStudentProfile(any());
    }

    @Test
    @DisplayName("Login with invalid password throws UnauthorizedException")
    void testLoginInvalidPassword() {
        User user = new User();
        user.setId(10L);
        user.setEmail("test.student@college.edu");
        user.setPasswordHash("$2a$10$hashedPasswordSample");
        user.setStatus("ACTIVE");

        when(userDao.findByEmail("test.student@college.edu")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "$2a$10$hashedPasswordSample")).thenReturn(false);

        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("test.student@college.edu");
        loginReq.setPassword("wrongPassword");

        assertThrows(UnauthorizedException.class, () -> authService.login(loginReq));
    }
}
