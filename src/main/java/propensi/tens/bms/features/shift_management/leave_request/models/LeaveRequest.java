package propensi.tens.bms.features.shift_management.leave_request.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import propensi.tens.bms.features.account_management.models.EndUser;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "leave_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private EndUser user;
    
    @Column(name = "request_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date requestDate;
    
    @Column(name = "leave_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;
    
    @Column(name = "reason")
    private String reason;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;
    
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        if (status == null) {
            status = LeaveStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
}