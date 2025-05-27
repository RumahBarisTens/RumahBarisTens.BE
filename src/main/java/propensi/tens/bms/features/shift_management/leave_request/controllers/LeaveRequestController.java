package propensi.tens.bms.features.shift_management.leave_request.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import propensi.tens.bms.features.account_management.dto.response.BaseResponseDTO;
import propensi.tens.bms.features.shift_management.leave_request.dto.request.CreateLeaveRequestDTO;
import propensi.tens.bms.features.shift_management.leave_request.dto.request.UpdateLeaveRequestDTO;
import propensi.tens.bms.features.shift_management.leave_request.dto.response.LeaveRequestResponseDTO;
import propensi.tens.bms.features.shift_management.leave_request.dto.request.ApproveRejectLeaveRequestDTO;
import propensi.tens.bms.features.shift_management.leave_request.services.LeaveRequestService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shift-management/leave-request")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping("/create")
    public ResponseEntity<?> createLeaveRequest(@RequestBody CreateLeaveRequestDTO createLeaveRequestDTO) {
        BaseResponseDTO<LeaveRequestResponseDTO> response = new BaseResponseDTO<>();
        try {
            LeaveRequestResponseDTO leaveRequest = leaveRequestService.createLeaveRequest(createLeaveRequestDTO);
            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Permohonan berhasil diajukan");
            response.setTimestamp(new Date());
            response.setData(leaveRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/username/{username}")
    public ResponseEntity<?> getLeaveRequestsByUsername(@PathVariable String username) {
        BaseResponseDTO<List<LeaveRequestResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<LeaveRequestResponseDTO> leaveRequests = leaveRequestService.getLeaveRequestsByUsername(username);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar permohonan berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(leaveRequests);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/detail/{username}/{date}")
    public ResponseEntity<?> getLeaveRequestByUsernameAndDate(
            @PathVariable String username,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        BaseResponseDTO<LeaveRequestResponseDTO> response = new BaseResponseDTO<>();
        try {
            LeaveRequestResponseDTO leaveRequest = leaveRequestService.getLeaveRequestByUsernameAndDate(username, date);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Detail permohonan berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(leaveRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLeaveRequestById(@PathVariable UUID id) {
        BaseResponseDTO<LeaveRequestResponseDTO> response = new BaseResponseDTO<>();
        try {
            LeaveRequestResponseDTO leaveRequest = leaveRequestService.getLeaveRequestById(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Detail permohonan berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(leaveRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLeaveRequestsByUser(@PathVariable UUID userId) {
        BaseResponseDTO<List<LeaveRequestResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<LeaveRequestResponseDTO> leaveRequests = leaveRequestService.getLeaveRequestsByUser(userId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar permohonan berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(leaveRequests);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllLeaveRequests() {
        BaseResponseDTO<List<LeaveRequestResponseDTO>> response = new BaseResponseDTO<>();
        List<LeaveRequestResponseDTO> leaveRequests = leaveRequestService.getAllLeaveRequests();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Daftar permohonan berhasil diambil");
        response.setTimestamp(new Date());
        response.setData(leaveRequests);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLeaveRequest(@PathVariable UUID id, @RequestBody UpdateLeaveRequestDTO updateLeaveRequestDTO) {
        BaseResponseDTO<LeaveRequestResponseDTO> response = new BaseResponseDTO<>();
        try {
            LeaveRequestResponseDTO leaveRequest = leaveRequestService.updateLeaveRequest(id, updateLeaveRequestDTO);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(updateLeaveRequestDTO.isCanceled() 
                ? "Permohonan berhasil dibatalkan" 
                : "Permohonan berhasil diperbarui");
            response.setTimestamp(new Date());
            response.setData(leaveRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{username}/{date}")
    public ResponseEntity<?> updateLeaveRequestByUsernameAndDate(
            @PathVariable String username,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestBody UpdateLeaveRequestDTO updateLeaveRequestDTO) {
        BaseResponseDTO<LeaveRequestResponseDTO> response = new BaseResponseDTO<>();
        try {
            LeaveRequestResponseDTO leaveRequest = leaveRequestService.updateLeaveRequestByUsernameAndDate(username, date, updateLeaveRequestDTO);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(updateLeaveRequestDTO.isCanceled() 
                ? "Permohonan berhasil dibatalkan" 
                : "Permohonan berhasil diperbarui");
            response.setTimestamp(new Date());
            response.setData(leaveRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> approveRejectLeaveRequest(@PathVariable UUID id, @RequestBody ApproveRejectLeaveRequestDTO approveRejectLeaveRequestDTO) {
        BaseResponseDTO<LeaveRequestResponseDTO> response = new BaseResponseDTO<>();
        try {
            LeaveRequestResponseDTO leaveRequest = leaveRequestService.approveRejectLeaveRequest(id, approveRejectLeaveRequestDTO);
            String message = approveRejectLeaveRequestDTO.getStatus().name().equals("APPROVED") 
                ? "Permohonan berhasil disetujui" 
                : "Permohonan berhasil ditolak";
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(message);
            response.setTimestamp(new Date());
            response.setData(leaveRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/status/{username}/{date}")
    public ResponseEntity<?> approveRejectLeaveRequestByUsernameAndDate(
            @PathVariable String username,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestBody ApproveRejectLeaveRequestDTO approveRejectLeaveRequestDTO) {
        BaseResponseDTO<LeaveRequestResponseDTO> response = new BaseResponseDTO<>();
        try {
            LeaveRequestResponseDTO leaveRequest = leaveRequestService.approveRejectLeaveRequestByUsernameAndDate(username, date, approveRejectLeaveRequestDTO);
            String message = approveRejectLeaveRequestDTO.getStatus().name().equals("APPROVED") 
                ? "Permohonan berhasil disetujui" 
                : "Permohonan berhasil ditolak";
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(message);
            response.setTimestamp(new Date());
            response.setData(leaveRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}