package propensi.tens.bms.features.notification_management.services;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import propensi.tens.bms.features.account_management.models.CLevel;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.repositories.BaristaDb;
import propensi.tens.bms.features.account_management.repositories.CLevelDb;
import propensi.tens.bms.features.account_management.repositories.EndUserDb;
import propensi.tens.bms.features.account_management.repositories.HeadBarDb;
import propensi.tens.bms.features.account_management.repositories.ProbationBaristaDb;
import propensi.tens.bms.features.notification_management.dto.response.NotificationResponse;
import propensi.tens.bms.features.notification_management.enums.NotificationType;
import propensi.tens.bms.features.notification_management.models.Notification;
import propensi.tens.bms.features.notification_management.repositories.NotificationRepository;
import propensi.tens.bms.features.shift_management.leave_request.models.LeaveRequest;
import propensi.tens.bms.features.shift_management.leave_request.repositories.LeaveRequestDb;
import propensi.tens.bms.features.shift_management.overtime.models.OvertimeLog;
import propensi.tens.bms.features.shift_management.overtime.repositories.OvertimeLogDb;
import propensi.tens.bms.features.shift_management.shift.models.ShiftSchedule;
import propensi.tens.bms.features.shift_management.shift.repositories.ShiftScheduleRepository;
import propensi.tens.bms.features.trainee_management.enums.AssignedRole;
import propensi.tens.bms.features.trainee_management.models.Assessment;
import propensi.tens.bms.features.trainee_management.models.AssessmentSubmission;
import propensi.tens.bms.features.trainee_management.models.PeerReviewAssignment;
import propensi.tens.bms.features.trainee_management.models.TrainingMaterial;
import propensi.tens.bms.features.trainee_management.repositories.AssessmentRepository;
import propensi.tens.bms.features.trainee_management.repositories.AssessmentSubmissionRepository;
import propensi.tens.bms.features.trainee_management.repositories.PeerReviewAssignmentRepository;
import propensi.tens.bms.features.trainee_management.repositories.TrainingMaterialDB;

@Service
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private EndUserDb endUserDb;

    @Autowired
    private CLevelDb CLevelDb;

    @Autowired
    private HeadBarDb headBarDb;

    @Autowired
    private BaristaDb baristaDb;

    @Autowired
    private ProbationBaristaDb probationBaristaDb;
    
    @Autowired
    private AssessmentRepository assessmentRepository;
    
    @Autowired
    private AssessmentSubmissionRepository submissionRepository;
    
    @Autowired
    private PeerReviewAssignmentRepository peerReviewRepository;

    @Autowired
    private TrainingMaterialDB trainingMaterialDb;

    @Autowired
    private LeaveRequestDb leaveRequestRepository;

    @Autowired
    private OvertimeLogDb overtimeLogDb;
    
    @Autowired
    private ShiftScheduleRepository shiftRepository;

    @Override
    public NotificationResponse createNotification(String username, String title, String message,
                                                  NotificationType type, String resourceType, Long resourceId, String actionUrl) {
        EndUser user = endUserDb.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .recipient(user)
                .isRead(false)
                .createdAt(new Date())
                .resourceType(resourceType)
                .resourceId(resourceId)
                .actionUrl(actionUrl)
                .build();
        
        notification = notificationRepository.save(notification);
        return mapToResponse(notification);
    }

    @Override
    public List<NotificationResponse> getUserNotifications(String username) {
        EndUser user = endUserDb.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NotificationResponse> getUserNotificationsPaged(String username, Pageable pageable) {
        EndUser user = endUserDb.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));
        
        notification.setRead(true);
        notification = notificationRepository.save(notification);
        return mapToResponse(notification);
    }

    @Override
    public List<NotificationResponse> markAllAsRead(String username) {
        EndUser user = endUserDb.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        
        List<Notification> unreadNotifications = notificationRepository.findByRecipientAndIsReadOrderByCreatedAtDesc(user, false);
        List<NotificationResponse> responses = new ArrayList<>();
        
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            responses.add(mapToResponse(notificationRepository.save(notification)));
        }
        
        return responses;
    }

    @Override
    public long getUnreadCount(String username) {
        EndUser user = endUserDb.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        
        return notificationRepository.countByRecipientAndIsRead(user, false);
    }

    // Assessment notifications
    @Override
    public void notifyAssessmentAssigned(Long assessmentId, Set<EndUser> user) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assessment not found with id: " + assessmentId));
        
        String title = "New Assessment Assigned";
        String message = "Kamu ditugaskan untuk mengerjakan ujian " + assessment.getTemplate().name();
        String resourceType = "ASSESSMENT";
        String actionUrl = "/assessment";
        
        for (EndUser u : user) {
            try {
                createNotification(
                    u.getUsername(), 
                    title, 
                    message, 
                    NotificationType.ASSESSMENT_ASSIGNED, 
                    resourceType, 
                    assessmentId,
                    actionUrl
                );
                log.info("Notification sent to user: {} for assessment assignment", u.getUsername());
            } catch (Exception e) {
                log.error("Failed to send notification to user: {} - {}", u.getUsername(), e.getMessage());
            }
        }
    }

    // @Override
    // public void notifyAssessmentSubmitted(Long assessmentId, String submitterUsername) {
    //     Assessment assessment = assessmentRepository.findById(assessmentId)
    //             .orElseThrow(() -> new EntityNotFoundException("Assessment not found with id: " + assessmentId));
        
    //     List<CLevel> clevels = CLevelDb.findAll();
        
    //     String title = "Assessment Submitted";
    //     String message = "User " + submitterUsername + " has submitted the assessment: " + assessment.getTemplate().name();
    //     String resourceType = "ASSESSMENT_SUBMISSION";
    //     String actionUrl = "/assessment/" + assessmentId + "/submissions";
        
    //     for (EndUser clevel : clevels) {
    //         try {
    //             createNotification(
    //                 clevel.getUsername(), 
    //                 title, 
    //                 message, 
    //                 NotificationType.ASSESSMENT_SUBMITTED, 
    //                 resourceType, 
    //                 assessmentId,
    //                 actionUrl
    //             );
    //             log.info("Notification sent to admin: {} for assessment submission", clevel.getUsername());
    //         } catch (Exception e) {
    //             log.error("Failed to send notification to admin: {} - {}", clevel.getUsername(), e.getMessage());
    //         }
    //     }
    // }

    @Override
    public void notifyEssayReviewRequired(Long submissionId, String submitterUsername) {
        AssessmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found with id: " + submissionId));
        
        List<CLevel> clevels = CLevelDb.findAll();
        
        String title = "Essay Review Required";
        String message = "Ujian milik " + submitterUsername + " perlu dinilai";
        String resourceType = "ESSAY_SUBMISSION";
        String actionUrl = "/assessment/dashboard-clevel/" + submission.getAssessment().getId();
        
        for (EndUser clevel : clevels) {
            try {
                createNotification(
                    clevel.getUsername(), 
                    title, 
                    message, 
                    NotificationType.ESSAY_REVIEW_REQUIRED, 
                    resourceType, 
                    submissionId,
                    actionUrl
                );
                log.info("Notification sent to reviewer: {} for essay review", clevel.getUsername());
            } catch (Exception e) {
                log.error("Failed to send notification to reviewer: {} - {}", clevel.getUsername(), e.getMessage());
            }
        }
    }

    // @Override
    // public void notifyEssayReviewCompleted(Long submissionId, String studentUsername) {
    //     AssessmentSubmission submission = submissionRepository.findById(submissionId)
    //             .orElseThrow(() -> new EntityNotFoundException("Submission not found with id: " + submissionId));
        
    //     String title = "Essay Review Completed";
    //     String message = "Your essay has been reviewed. Check your results.";
    //     String resourceType = "ESSAY_SUBMISSION";
    //     String actionUrl = "/assessment/" + submission.getAssessment().getId() + "/submission/" + submissionId + "/result";
        
    //     try {
    //         createNotification(
    //             studentUsername, 
    //             title, 
    //             message, 
    //             NotificationType.ESSAY_REVIEW_COMPLETED, 
    //             resourceType, 
    //             submissionId,
    //             actionUrl
    //         );
    //         log.info("Notification sent to student: {} for essay review completion", studentUsername);
    //     } catch (Exception e) {
    //         log.error("Failed to send notification to student: {} - {}", studentUsername, e.getMessage());
    //     }
    // }

    // @Override
    // public void notifyAssessmentDeadlineApproaching(Long assessmentId, List<String> usernames) {
    //     Assessment assessment = assessmentRepository.findById(assessmentId)
    //             .orElseThrow(() -> new EntityNotFoundException("Assessment not found with id: " + assessmentId));
        
    //     String title = "Assessment Deadline Approaching";
    //     String message = "Your assessment deadline is approaching for: " + assessment.getTemplate().name();
    //     String resourceType = "ASSESSMENT";
    //     String actionUrl = "/assessment/" + assessmentId;
        
    //     for (String username : usernames) {
    //         try {
    //             createNotification(
    //                 username, 
    //                 title, 
    //                 message, 
    //                 NotificationType.ASSESSMENT_DEADLINE, 
    //                 resourceType, 
    //                 assessmentId,
    //                 actionUrl
    //             );
    //             log.info("Deadline notification sent to user: {} for assessment", username);
    //         } catch (Exception e) {
    //             log.error("Failed to send deadline notification to user: {} - {}", username, e.getMessage());
    //         }
    //     }
    // }

    // Peer review notifications
    @Override
    public void notifyPeerReviewAssigned(Integer assignmentId, String reviewerUsername, String revieweeUsername) {
        PeerReviewAssignment assignment = peerReviewRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Peer review assignment not found with id: " + assignmentId));
        
        // Notify the reviewer
        String title = "New Peer Review Assignment";
        String message = "You have been assigned to review " + revieweeUsername;
        String resourceType = "PEER_REVIEW";
        String actionUrl = "/peer-review/";
        
        try {
            createNotification(
                reviewerUsername, 
                title, 
                message, 
                NotificationType.PEER_REVIEW_ASSIGNED, 
                resourceType, 
                assignmentId.longValue(),
                actionUrl
            );
            log.info("Notification sent to reviewer: {} for peer review assignment", reviewerUsername);
        } catch (Exception e) {
            log.error("Failed to send notification to reviewer: {} - {}", reviewerUsername, e.getMessage());
        }
        
        // Notify the reviewee
        title = "Peer Review Scheduled";
        message = reviewerUsername + " has been assigned to review your performance";
        
        try {
            createNotification(
                revieweeUsername, 
                title, 
                message, 
                NotificationType.PEER_REVIEW_ASSIGNED, 
                resourceType, 
                assignmentId.longValue(),
                actionUrl
            );
            log.info("Notification sent to reviewee: {} about peer review", revieweeUsername);
        } catch (Exception e) {
            log.error("Failed to send notification to reviewee: {} - {}", revieweeUsername, e.getMessage());
        }
    }

    // @Override
    // public void notifyPeerReviewSubmitted(Integer submissionId, String reviewerUsername, String revieweeUsername) {
    //     // Notify the reviewee
    //     String title = "Peer Review Submitted";
    //     String message = reviewerUsername + " has submitted your peer review";
    //     String resourceType = "PEER_REVIEW_SUBMISSION";
    //     String actionUrl = "/peer-review/result/" + submissionId;
        
    //     try {
    //         createNotification(
    //             revieweeUsername, 
    //             title, 
    //             message, 
    //             NotificationType.PEER_REVIEW_SUBMITTED, 
    //             resourceType, 
    //             submissionId.longValue(),
    //             actionUrl
    //         );
    //         log.info("Notification sent to reviewee: {} about peer review submission", revieweeUsername);
    //     } catch (Exception e) {
    //         log.error("Failed to send notification to reviewee: {} - {}", revieweeUsername, e.getMessage());
    //     }
        
    //     // Notify admins
    //     List<EndUser> admins = endUserDb.findAllAdmin();
    //     title = "Peer Review Completed";
    //     message = reviewerUsername + " has submitted a peer review for " + revieweeUsername;
        
    //     for (EndUser admin : admins) {
    //         try {
    //             createNotification(
    //                 admin.getUsername(), 
    //                 title, 
    //                 message, 
    //                 NotificationType.PEER_REVIEW_SUBMITTED, 
    //                 resourceType, 
    //                 submissionId.longValue(),
    //                 actionUrl
    //             );
    //             log.info("Notification sent to admin: {} about peer review submission", admin.getUsername());
    //         } catch (Exception e) {
    //             log.error("Failed to send notification to admin: {} - {}", admin.getUsername(), e.getMessage());
    //         }
    //     }
    // }

    // @Override
    // public void notifyPeerReviewDeadlineApproaching(Integer assignmentId, String reviewerUsername) {
    //     PeerReviewAssignment assignment = peerReviewRepository.findById(assignmentId)
    //             .orElseThrow(() -> new EntityNotFoundException("Peer review assignment not found with id: " + assignmentId));
        
    //     String title = "Peer Review Deadline Approaching";
    //     String message = "Your peer review deadline is approaching for reviewing " + assignment.getReviewee().getUsername();
    //     String resourceType = "PEER_REVIEW";
    //     String actionUrl = "/peer-review/assignment/" + assignmentId;
        
    //     try {
    //         createNotification(
    //             reviewerUsername, 
    //             title, 
    //             message, 
    //             NotificationType.PEER_REVIEW_DEADLINE, 
    //             resourceType, 
    //             assignmentId.longValue(),
    //             actionUrl
    //         );
    //         log.info("Deadline notification sent to reviewer: {} for peer review", reviewerUsername);
    //     } catch (Exception e) {
    //         log.error("Failed to send deadline notification to reviewer: {} - {}", reviewerUsername, e.getMessage());
    //     }
    // }

    @Override
    public void notifyTrainingMaterialAssigned(Long trainingMaterialId, Set<AssignedRole> assignedRoles) {
        TrainingMaterial training = trainingMaterialDb.findById(trainingMaterialId)
                .orElseThrow(() -> new EntityNotFoundException("Training Material assignment not found with id: " + trainingMaterialId));

        String title = "New Training Material Assigned";
        String message = "Kamu ditugaskan untuk membaca materi: " + training.getTitle();
        String resourceType = "TRAINING_MATERIAL";
        String actionUrl = "/training-materials";
        
        for (AssignedRole assignedRole : assignedRoles) {
            List<?> users = switch (assignedRole) {
                case PROBATION_BARISTA -> probationBaristaDb.findAll();
                case HEAD_BAR -> headBarDb.findAll();
                case BARISTA -> baristaDb.findAllTraineeBarista(false);
                case INTERN_BARISTA -> baristaDb.findAllTraineeBarista(true);
            };
            
            for (Object userObj : users) {
                String username = ((EndUser) userObj).getUsername();
                
                try {
                    createNotification(
                        username, 
                        title, 
                        message, 
                        NotificationType.TRAINING_MATERIAL_ASSIGNED, 
                        resourceType, 
                        trainingMaterialId,
                        actionUrl
                    );
                    log.info("Notification sent to user: {} for training material: {}", username, trainingMaterialId);
                } catch (Exception e) {
                    log.error("Failed to send notification to user: {} - {}", username, e.getMessage());
                }
            }
        }
    }
    
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .resourceType(notification.getResourceType())
                .resourceId(notification.getResourceId())
                .actionUrl(notification.getActionUrl())
                .build();
    }

    @Override
    public void notifyLeaveRequestCanceled(UUID requestId, String headbarUsername) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found with id: " + requestId));
        
        String title = "Leave Request Canceled";
        String message = "Barista " + request.getUser().getFullName() + 
                " has canceled their " + request.getLeaveType().toString() + " request";
        String resourceType = "LEAVE_REQUEST";
        String actionUrl = "jadwal/izin-cuti/headbar/reports";
        
        try {
            createNotification(
                headbarUsername, 
                title, 
                message, 
                NotificationType.LEAVE_REQUEST, 
                resourceType, 
                null,
                actionUrl
            );
            log.info("Notification sent to headbar: {} for leave request cancellation", headbarUsername);
        } catch (Exception e) {
            log.error("Failed to send notification to headbar: {} - {}", headbarUsername, e.getMessage());
        }
    }

    @Override
    public void notifyOvertimeLogCanceled(Integer requestId, String headbarUsername) {
        OvertimeLog request = overtimeLogDb.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found with id: " + requestId));

        EndUser user = endUserDb.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));
        
        String title = "Overtime Log Canceled";
        String message = "Barista " + user.getUsername() + 
                " has canceled their " + request.getDateOvertime() + " log";
        String resourceType = "OVERTIME_LOG";
        String actionUrl = "jadwal/lembur/headbar/reports";
        
        try {
            createNotification(
                headbarUsername, 
                title, 
                message, 
                NotificationType.OVERTIME, 
                resourceType, 
                null,
                actionUrl
            );
            log.info("Notification sent to headbar: {} for leave request cancellation", headbarUsername);
        } catch (Exception e) {
            log.error("Failed to send notification to headbar: {} - {}", headbarUsername, e.getMessage());
        }
    }
    
    @Override
    public void notifyShiftAssigned(Long shiftId, List<EndUser> assignedUsers) {
        ShiftSchedule shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new EntityNotFoundException("Shift not found with id: " + shiftId));

        String type = shift.getShiftType() == 1 ? "pagi" : "sore";
        
        String title = "Penugasan Shift";
        String message = "Kamu ditugaskan shift " + type + " tanggal " +
                DateTimeFormatter.ofPattern("dd MMM yyyy").format(shift.getDateShift()) 
                ;
        String resourceType = "SHIFT";
        String actionUrl = "/jadwal/shift";
        
        for (EndUser user : assignedUsers) {
            try {
                createNotification(
                    user.getUsername(), 
                    title, 
                    message, 
                    NotificationType.SHIFT, 
                    resourceType, 
                    shiftId,
                    actionUrl
                );
                log.info("Notification sent to user: {} for shift assignment", user.getUsername());
            } catch (Exception e) {
                log.error("Failed to send notification to user: {} - {}", user.getUsername(), e.getMessage());
            }
        }
    }
    
    @Override
    public void notifyLeaveRequestApproved(UUID requestId, String requestingUsername) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found with id: " + requestId));
        
        String typeString = request.getLeaveType().toString().toLowerCase();
        String title = typeString.substring(0, 1).toUpperCase() + typeString.substring(1) + " Request Approved";
        
       String message = "Your " + typeString + " request for " +
    DateTimeFormatter.ofPattern("dd MMM yyyy").format(
        ((java.sql.Date) request.getRequestDate()).toLocalDate()
    ) +
    " has been approved";

        String resourceType = "LEAVE_REQUEST";
        String actionUrl = "/jadwal/izin-cuti/barista/list";
        
        try {
            createNotification(
                requestingUsername, 
                title, 
                message, 
                NotificationType.LEAVE_REQUEST, 
                resourceType, 
                null,
                actionUrl
            );
            log.info("Notification sent to user: {} for leave request approval", requestingUsername);
        } catch (Exception e) {
            log.error("Failed to send notification to user: {} - {}", requestingUsername, e.getMessage());
        }
    }

    public void notifyOvertimeLogApproved(Integer requestId, String requestingUsername) {
        OvertimeLog request = overtimeLogDb.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found with id: " + requestId));
        
        String title = "Overtime Log Approved";
        
        String message = "Pengajuan lembur kamu pada " + 
                DateTimeFormatter.ofPattern("dd MMM yyyy").format((TemporalAccessor) request.getDateOvertime()) + 
                " telah disetujui";
        String resourceType = "OVERTIME_LOG";
        String actionUrl = "/jadwal/lembur/" + requestId;
        
        try {
            createNotification(
                requestingUsername, 
                title, 
                message, 
                NotificationType.OVERTIME, 
                resourceType,
                null,
                actionUrl
            );
            log.info("Notification sent to user: {} for leave request approval", requestingUsername);
        } catch (Exception e) {
            log.error("Failed to send notification to user: {} - {}", requestingUsername, e.getMessage());
        }
    }
}