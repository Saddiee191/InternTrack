package com.interntrack.applications;

import com.interntrack.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationDao applicationDao;

    public ApplicationService(ApplicationDao applicationDao) {
        this.applicationDao = applicationDao;
    }

    public List<Application> listApplications(Long userId, String status, String search, String sortBy) {
        return applicationDao.findByUserId(userId, status, search, sortBy);
    }

    public Application getApplication(Long id, Long userId) {
        return applicationDao.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found or you do not have permission to view it"));
    }

    @Transactional
    public Application createApplication(Long userId, ApplicationRequest req) {
        Long id = applicationDao.create(userId, req);
        return getApplication(id, userId);
    }

    @Transactional
    public Application updateApplication(Long id, Long userId, ApplicationRequest req) {
        int rows = applicationDao.update(id, userId, req);
        if (rows == 0) {
            throw new ResourceNotFoundException("Application not found or you do not have permission to edit it");
        }
        return getApplication(id, userId);
    }

    @Transactional
    public Application updateStatus(Long id, Long userId, String newStatus, String notes) {
        int rows = applicationDao.updateStatus(id, userId, newStatus, notes);
        if (rows == 0) {
            throw new ResourceNotFoundException("Application not found or you do not have permission to modify its status");
        }
        return getApplication(id, userId);
    }

    @Transactional
    public void deleteApplication(Long id, Long userId) {
        int rows = applicationDao.delete(id, userId);
        if (rows == 0) {
            throw new ResourceNotFoundException("Application not found or you do not have permission to delete it");
        }
    }

    public List<ApplicationStatusHistory> getStatusHistory(Long id, Long userId) {
        // Enforce ownership
        getApplication(id, userId);
        return applicationDao.getStatusHistory(id);
    }

    public List<ApplicationReminder> getReminders(Long userId) {
        return applicationDao.getReminders(userId);
    }

    @Transactional
    public Long createReminder(Long userId, ReminderRequest req) {
        // Enforce ownership of the application
        getApplication(req.getApplicationId(), userId);
        return applicationDao.createReminder(userId, req);
    }

    public void dismissReminder(Long reminderId, Long userId) {
        int rows = applicationDao.dismissReminder(reminderId, userId);
        if (rows == 0) {
            throw new ResourceNotFoundException("Reminder not found or already dismissed");
        }
    }

    public ApplicationStatsDto getStats(Long userId) {
        return applicationDao.getStats(userId);
    }
}
