package propensi.tens.bms.features.shift_management.overtime.dto.response;

import propensi.tens.bms.features.shift_management.overtime.models.OvertimeLog.OvertimeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeLogResponse {
    private Integer id;
    private Integer baristaId;
    private UUID userId;
    private Integer outletId;
    private LocalDate dateOvertime;
    private LocalTime startHour;
    private LocalTime duration;
    private String reason;
    private OvertimeStatus status;
    private String statusDisplay;
    private String verifier;
    private String outletName;
    private String baristaName;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
