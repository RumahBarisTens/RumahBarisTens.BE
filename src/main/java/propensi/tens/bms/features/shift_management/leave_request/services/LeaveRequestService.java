package propensi.tens.bms.features.shift_management.leave_request.services;

import propensi.tens.bms.features.shift_management.leave_request.dto.request.ApproveRejectLeaveRequestDTO;
import propensi.tens.bms.features.shift_management.leave_request.dto.request.CreateLeaveRequestDTO;
import propensi.tens.bms.features.shift_management.leave_request.dto.request.UpdateLeaveRequestDTO;
import propensi.tens.bms.features.shift_management.leave_request.dto.response.LeaveRequestResponseDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface LeaveRequestService {
    LeaveRequestResponseDTO createLeaveRequest(CreateLeaveRequestDTO createLeaveRequestDTO) throws Exception;
    LeaveRequestResponseDTO getLeaveRequestById(UUID id) throws Exception;
    List<LeaveRequestResponseDTO> getLeaveRequestsByUser(UUID userId) throws Exception;
    List<LeaveRequestResponseDTO> getLeaveRequestsByUsername(String username) throws Exception;
    List<LeaveRequestResponseDTO> getAllLeaveRequests();
    LeaveRequestResponseDTO updateLeaveRequest(UUID id, UpdateLeaveRequestDTO updateLeaveRequestDTO) throws Exception;
    LeaveRequestResponseDTO approveRejectLeaveRequest(UUID id, ApproveRejectLeaveRequestDTO approveRejectLeaveRequestDTO) throws Exception;
    
    // Tambahan method baru
    LeaveRequestResponseDTO getLeaveRequestByUsernameAndDate(String username, Date requestDate) throws Exception;
    LeaveRequestResponseDTO updateLeaveRequestByUsernameAndDate(String username, Date requestDate, UpdateLeaveRequestDTO updateLeaveRequestDTO) throws Exception;
    LeaveRequestResponseDTO approveRejectLeaveRequestByUsernameAndDate(String username, Date requestDate, ApproveRejectLeaveRequestDTO approveRejectLeaveRequestDTO) throws Exception;
}