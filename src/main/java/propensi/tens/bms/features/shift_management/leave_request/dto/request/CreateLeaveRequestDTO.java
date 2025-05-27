package propensi.tens.bms.features.shift_management.leave_request.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import propensi.tens.bms.features.shift_management.leave_request.models.LeaveType;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLeaveRequestDTO {
    private String username;
    private Date requestDate;
    private LeaveType leaveType;
    private String reason;
}