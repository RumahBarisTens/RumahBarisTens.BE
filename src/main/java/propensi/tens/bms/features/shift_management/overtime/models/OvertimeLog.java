package propensi.tens.bms.features.shift_management.overtime.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "overtime_log")
public class OvertimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "overtime_log_id")
    private Integer overtimeLogId;

    @Column(name = "barista_id", nullable = false)
    private Integer baristaId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "outlet_id", nullable = false)
    private Integer outletId;

    @Column(name = "date_overtime", nullable = false)
    private LocalDate dateOvertime;

    @Column(name = "start_hour", nullable = false)
    private LocalTime startHour;

    @Column(name = "duration", nullable = false)
    private LocalTime duration;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OvertimeStatus status;

    @Column(name = "verifier")
    private String verifier;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    public enum OvertimeStatus {
        PENDING("Menunggu Konfirmasi"),
        APPROVED("Diterima"),
        REJECTED("Ditolak"),
        ONGOING("Sedang Berlangsung"),
        CANCELLED("Dibatalkan");
        
        private final String displayValue;
        
        OvertimeStatus(String displayValue) {
            this.displayValue = displayValue;
        }
        
        public String getDisplayValue() {
            return displayValue;
        }
    }
    
}