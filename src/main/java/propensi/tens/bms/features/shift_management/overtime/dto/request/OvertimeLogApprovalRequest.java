package propensi.tens.bms.features.shift_management.overtime.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeLogApprovalRequest {
    
    @NotNull(message = "Status is required")
    private String status; // APPROVED or REJECTED
    
    @NotNull(message = "Verifier is required")
    private String verifier; // The HeadBar who approves/rejects
}