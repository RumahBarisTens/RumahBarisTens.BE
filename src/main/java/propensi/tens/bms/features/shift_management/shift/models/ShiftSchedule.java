package propensi.tens.bms.features.shift_management.shift.models;

import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.models.HeadBar;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shift_schedule",
       uniqueConstraints = @UniqueConstraint(columnNames = {"createdAt", "date_shift", "shift_type", "outlet_id", "head_bar_id"}))
@Getter
@Setter
public class ShiftSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shiftScheduleId;

    private LocalDateTime createdAt;

    private Integer shiftType;

    @Column(nullable = false)
    private LocalDate dateShift;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "shift_schedule_barista",
        joinColumns = @JoinColumn(name = "shift_schedule_id"),
        inverseJoinColumns = @JoinColumn(name = "barista_id")
    )
    private List<EndUser> listBarista;

    @Column(nullable = false)
    private Long outletId;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(nullable = false)
    private UUID headBarId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "headBarId", referencedColumnName = "id", insertable = false, updatable = false)
    private HeadBar headBar;

    private int overtimeHours;
}
