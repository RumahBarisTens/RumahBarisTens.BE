package propensi.tens.bms.features.notification_management.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import propensi.tens.bms.features.notification_management.enums.NotificationType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private Date createdAt;
    private String resourceType;
    private Long resourceId;
    private String actionUrl;
}