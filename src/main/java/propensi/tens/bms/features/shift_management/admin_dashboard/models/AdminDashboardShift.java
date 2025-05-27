package propensi.tens.bms.features.shift_management.admin_dashboard.models;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AdminDashboardShift {
    private Long shiftScheduleId;
    private UUID baristaId;  
    private String baristaName;
    private String role;
    private Long outletId;
    private String outletName;
    private LocalDate dateShift;
    private Integer shiftType;
    private Integer shiftCount;
    private Long overtimeLogId;
    private LocalDate dateOvertime;
    private Integer duration;
    private String status;
}