package propensi.tens.bms.features.notification_management.services;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.notification_management.dto.response.NotificationResponse;
import propensi.tens.bms.features.notification_management.enums.NotificationType;
import propensi.tens.bms.features.trainee_management.enums.AssignedRole;

public interface NotificationService {
    // General notification methods
    NotificationResponse createNotification(String username, String title, String message, 
                                           NotificationType type, String resourceType, Long resourceId, String actionUrl);
    
    List<NotificationResponse> getUserNotifications(String username);
    Page<NotificationResponse> getUserNotificationsPaged(String username, Pageable pageable);
    NotificationResponse markAsRead(Long id);
    List<NotificationResponse> markAllAsRead(String username);
    long getUnreadCount(String username);
    
    // Assessment specific notifications
    void notifyAssessmentAssigned(Long assessmentId,  Set<EndUser> user);
    // void notifyAssessmentSubmitted(Long assessmentId, String submitterUsername);
    void notifyEssayReviewRequired(Long submissionId, String submitterUsername);
    // void notifyEssayReviewCompleted(Long submissionId, String studentUsername);
    // void notifyAssessmentDeadlineApproaching(Long assessmentId, List<String> usernames);
    
    // Peer review specific notifications
    void notifyPeerReviewAssigned(Integer assignmentId, String reviewerUsername, String revieweeUsername);
    // void notifyPeerReviewSubmitted(Integer submissionId, String reviewerUsername, String revieweeUsername);
    // void notifyPeerReviewDeadlineApproaching(Integer assignmentId, String reviewerUsername);

    void notifyTrainingMaterialAssigned(Long trainingMaterialId, Set<AssignedRole> assignedRoles);


    void notifyLeaveRequestCanceled(UUID requestId, String headbarUsername);
    void notifyOvertimeLogCanceled(Integer requestId, String headbarUsername);
    void notifyShiftAssigned(Long shiftId, List<EndUser> assignedUsers);
    void notifyLeaveRequestApproved(UUID requestId, String requestingUsername);
    void notifyOvertimeLogApproved(Integer requestId, String requestingUsername);
}