package propensi.tens.bms.features.shift_management.shift.models;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftDetail {
    private Integer shiftType;     // 1 = Morning, 2 = Afternoon
    private String outletName;     // Outlet name
    private String shiftTime;      // Shift time (e.g., "08:00 - 16:00")
    private boolean isOvertime;    // Whether this shift includes overtime
    
    // Helper method to get shift time based on shift type
    public static String getShiftTimeByType(Integer shiftType) {
        return shiftType == 1 ? "08:00 - 16:00" : "16:00 - 00:00";
    }
}