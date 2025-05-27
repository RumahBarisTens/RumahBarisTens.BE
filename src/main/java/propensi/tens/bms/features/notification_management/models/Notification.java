package propensi.tens.bms.features.notification_management.models;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.notification_management.enums.NotificationType;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private EndUser recipient;
    
    @Column(nullable = false)
    private boolean isRead;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column
    private String resourceType;
    
    @Column
    private Long resourceId;
    
    @Column
    private String actionUrl;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
        if (isRead == false) {
            isRead = false;
        }
    }
}