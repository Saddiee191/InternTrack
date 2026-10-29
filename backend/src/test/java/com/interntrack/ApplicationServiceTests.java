package com.interntrack;

import com.interntrack.applications.*;
import com.interntrack.common.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTests {

    @Mock
    private ApplicationDao applicationDao;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    @DisplayName("Prevent horizontal privilege escalation: Student cannot access another student's application")
    void testPreventHorizontalPrivilegeEscalation() {
        Long ownerUserId = 4L;
        Long attackerUserId = 5L;
        Long applicationId = 100L;

        // When attacker tries to access, the DAO returns empty because userId does not match
        when(applicationDao.findByIdAndUserId(applicationId, attackerUserId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> applicationService.getApplication(applicationId, attackerUserId));
    }

    @Test
    @DisplayName("Student can delete their own application")
    void testDeleteOwnApplicationSuccess() {
        Long ownerUserId = 4L;
        Long applicationId = 100L;

        when(applicationDao.delete(applicationId, ownerUserId)).thenReturn(1);

        assertDoesNotThrow(() -> applicationService.deleteApplication(applicationId, ownerUserId));
        verify(applicationDao, times(1)).delete(applicationId, ownerUserId);
    }
}
