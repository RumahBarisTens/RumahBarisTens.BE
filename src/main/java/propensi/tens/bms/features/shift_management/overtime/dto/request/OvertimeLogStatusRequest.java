package propensi.tens.bms.features.shift_management.overtime.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeLogStatusRequest {
    
    @NotNull(message = "Status is required")
    private String status;
}