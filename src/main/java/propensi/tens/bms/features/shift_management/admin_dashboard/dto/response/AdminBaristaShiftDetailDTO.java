package propensi.tens.bms.features.shift_management.admin_dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminBaristaShiftDetailDTO {
    private LocalDate shiftDate;
    private Integer shiftType; // Bisa null jika hanya overtime tanpa shift
    private Long outletId;
    private String outletName;
    private Boolean hasOvertime = false;  // Default false
    private String overtimeStatus;  // Bisa null
    private Integer overtimeDuration;  // Dalam jam, bisa null
}
