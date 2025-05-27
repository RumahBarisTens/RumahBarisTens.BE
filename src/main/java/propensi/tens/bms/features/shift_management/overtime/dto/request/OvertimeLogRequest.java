package propensi.tens.bms.features.shift_management.overtime.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeLogRequest {
    
    @NotNull(message = "Barista ID is required")
    private Integer baristaId;
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Outlet ID is required")
    private Integer outletId;
    
    @NotNull(message = "Date of overtime is required")
    private LocalDate dateOvertime;
    
    @NotNull(message = "Start hour is required")
    private LocalTime startHour;
    
    @NotNull(message = "Duration is required")
    private LocalTime duration;
    
    @NotNull(message = "Reason is required")
    private String reason;

    
}