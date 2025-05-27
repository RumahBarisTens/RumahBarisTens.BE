package propensi.tens.bms.features.shift_management.admin_dashboard.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardFilterRequestDTO {
    
    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;
    
    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;
    
    private Long outletId;
    
    private String baristaName;

    private String timeRange;
}