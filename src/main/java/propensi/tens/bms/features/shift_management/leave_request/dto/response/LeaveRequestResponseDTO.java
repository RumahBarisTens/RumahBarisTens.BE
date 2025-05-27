package propensi.tens.bms.features.shift_management.leave_request.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import propensi.tens.bms.features.shift_management.leave_request.models.LeaveStatus;
import propensi.tens.bms.features.shift_management.leave_request.models.LeaveType;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestResponseDTO {
    private UUID id;
    private String userName;
    private Date requestDate;
    private LeaveType leaveType;
    private String reason;
    private LeaveStatus status;
    private Date createdAt;
    private Date updatedAt;
    private Long idOutlet; 
}