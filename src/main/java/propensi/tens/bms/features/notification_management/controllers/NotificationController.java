package propensi.tens.bms.features.notification_management.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import propensi.tens.bms.features.account_management.dto.response.BaseResponseDTO;
import propensi.tens.bms.features.notification_management.dto.response.NotificationResponse;
import propensi.tens.bms.features.notification_management.services.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/{username}")
    public ResponseEntity<BaseResponseDTO<List<NotificationResponse>>> getUserNotifications(@PathVariable("username") String username) {
        BaseResponseDTO<List<NotificationResponse>> response = new BaseResponseDTO<>();
        try {
            
            List<NotificationResponse> notifications = notificationService.getUserNotifications(username);
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Notifications retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(notifications);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/paged")
    public ResponseEntity<BaseResponseDTO<Page<NotificationResponse>>> getPagedNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        BaseResponseDTO<Page<NotificationResponse>> response = new BaseResponseDTO<>();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationResponse> notifications = notificationService.getUserNotificationsPaged(username, pageable);
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Notifications retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(notifications);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<BaseResponseDTO<Map<String, Long>>> getUnreadCount() {
        BaseResponseDTO<Map<String, Long>> response = new BaseResponseDTO<>();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            long count = notificationService.getUnreadCount(username);
            
            Map<String, Long> countMap = new HashMap<>();
            countMap.put("count", count);
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Unread count retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(countMap);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{id}/read")
    public ResponseEntity<BaseResponseDTO<NotificationResponse>> markAsRead(@PathVariable Long id) {
        BaseResponseDTO<NotificationResponse> response = new BaseResponseDTO<>();
        try {
            NotificationResponse notification = notificationService.markAsRead(id);
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Notification marked as read successfully");
            response.setTimestamp(new Date());
            response.setData(notification);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}