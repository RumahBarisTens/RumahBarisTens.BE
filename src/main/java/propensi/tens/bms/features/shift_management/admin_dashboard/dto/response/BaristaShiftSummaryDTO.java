package propensi.tens.bms.features.shift_management.admin_dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import propensi.tens.bms.features.shift_management.overtime.models.OvertimeLog.OvertimeStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaristaShiftSummaryDTO {
    private UUID baristaId;
    private String baristaName;
    private String role = "BARISTA";  // Default value
    private Long outletId;
    private String outletName;
    private Integer totalShifts = 0;
    private List<ShiftDetail> shifts = new ArrayList<>();
    private Integer overtimeDays = 0;
    private Integer totalOvertimeMinutes = 0;
    private List<OvertimeDetail> overtimes = new ArrayList<>();
    private String confirmationMessage;  // Bisa null

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShiftDetail {
        private LocalDate shiftDate;
        private Integer shiftType;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OvertimeDetail {
        private Integer overtimeLogId;
        private LocalDate overtimeDate;
        private LocalTime startHour;
        private LocalTime duration;
        private Integer durationMinutes;
        private OvertimeStatus status;
        private String statusDisplay;
        private String reason;
    }
}
