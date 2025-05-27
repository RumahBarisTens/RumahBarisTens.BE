package propensi.tens.bms.features.shift_management.leave_request.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import propensi.tens.bms.features.shift_management.leave_request.models.LeaveStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproveRejectLeaveRequestDTO {
    private LeaveStatus status;
}