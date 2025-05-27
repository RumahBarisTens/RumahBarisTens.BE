package propensi.tens.bms.features.shift_management.leave_request.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import propensi.tens.bms.features.account_management.models.Barista;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.models.Outlet;
import propensi.tens.bms.features.account_management.repositories.EndUserDb;
import propensi.tens.bms.features.account_management.repositories.OutletDb;
import propensi.tens.bms.features.notification_management.services.NotificationService;
import propensi.tens.bms.features.shift_management.leave_request.dto.request.ApproveRejectLeaveRequestDTO;
import propensi.tens.bms.features.shift_management.leave_request.dto.request.CreateLeaveRequestDTO;
import propensi.tens.bms.features.shift_management.leave_request.dto.request.UpdateLeaveRequestDTO;
import propensi.tens.bms.features.shift_management.leave_request.dto.response.LeaveRequestResponseDTO;
import propensi.tens.bms.features.shift_management.leave_request.models.LeaveRequest;
import propensi.tens.bms.features.shift_management.leave_request.models.LeaveStatus;
import propensi.tens.bms.features.shift_management.leave_request.repositories.LeaveRequestDb;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Autowired
    private LeaveRequestDb leaveRequestDb;

    @Autowired
    private EndUserDb endUserDb;

    @Autowired
    private OutletDb outletDb;;

    @Autowired
    private NotificationService notificationService;

    @Override
    public LeaveRequestResponseDTO createLeaveRequest(CreateLeaveRequestDTO createLeaveRequestDTO) throws Exception {
        // Validate input
        if (createLeaveRequestDTO.getRequestDate() == null) {
            throw new Exception("Tanggal permohonan wajib diisi");
        }

        // Validate that date is not already requested by this user
        boolean dateAlreadyRequested = leaveRequestDb.existsByUserUsernameAndRequestDateAndStatusNot(
            createLeaveRequestDTO.getUsername(), 
            createLeaveRequestDTO.getRequestDate(),
            LeaveStatus.CANCELED
        );

        if (dateAlreadyRequested) {
            throw new Exception("Anda sudah mengajukan cuti/izin pada tanggal tersebut");
        }

        if (createLeaveRequestDTO.getLeaveType() == null) {
            throw new Exception("Jenis permohonan wajib diisi");
        }
    
        // Get user by username
        EndUser user = endUserDb.findByUsername(createLeaveRequestDTO.getUsername());
        if (user == null) {
            throw new Exception("User tidak ditemukan");
        }
    
        // Create leave request
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setUser(user);
        leaveRequest.setRequestDate(createLeaveRequestDTO.getRequestDate());
        leaveRequest.setLeaveType(createLeaveRequestDTO.getLeaveType());
        leaveRequest.setReason(createLeaveRequestDTO.getReason());
        leaveRequest.setStatus(LeaveStatus.PENDING);
    
        // Save to database
        leaveRequest = leaveRequestDb.save(leaveRequest);
    
        // Return response
        return mapToLeaveRequestResponseDTO(leaveRequest);
    }
    
    @Override
    public List<LeaveRequestResponseDTO> getLeaveRequestsByUsername(String username) throws Exception {
        EndUser user = endUserDb.findByUsername(username);
        if (user == null) {
            throw new Exception("User tidak ditemukan");
        }
    
        List<LeaveRequest> leaveRequests = leaveRequestDb.findByUser(user);
        return leaveRequests.stream()
                .map(this::mapToLeaveRequestResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveRequestResponseDTO getLeaveRequestById(UUID id) throws Exception {
        Optional<LeaveRequest> leaveRequestOptional = leaveRequestDb.findById(id);
        if (leaveRequestOptional.isEmpty()) {
            throw new Exception("Permohonan izin/cuti tidak ditemukan");
        }
        return mapToLeaveRequestResponseDTO(leaveRequestOptional.get());
    }

    @Override
    public List<LeaveRequestResponseDTO> getLeaveRequestsByUser(UUID userId) throws Exception {
        Optional<EndUser> userOptional = endUserDb.findById(userId);
        if (userOptional.isEmpty()) {
            throw new Exception("User tidak ditemukan");
        }
        EndUser user = userOptional.get();

        List<LeaveRequest> leaveRequests = leaveRequestDb.findByUser(user);
        return leaveRequests.stream()
                .map(this::mapToLeaveRequestResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestResponseDTO> getAllLeaveRequests() {
        // Get all leave requests - frontend will handle filtering based on user roles
        List<LeaveRequest> leaveRequests = leaveRequestDb.findAll();
        return leaveRequests.stream()
                .map(this::mapToLeaveRequestResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveRequestResponseDTO updateLeaveRequest(UUID id, UpdateLeaveRequestDTO updateLeaveRequestDTO) throws Exception {
        Optional<LeaveRequest> leaveRequestOptional = leaveRequestDb.findById(id);
        if (leaveRequestOptional.isEmpty()) {
            throw new Exception("Permohonan izin/cuti tidak ditemukan");
        }
        
        LeaveRequest leaveRequest = leaveRequestOptional.get();
        
        // Check if request is already approved or rejected
        if (leaveRequest.getStatus() == LeaveStatus.APPROVED || leaveRequest.getStatus() == LeaveStatus.REJECTED) {
            throw new Exception("Permohonan yang sudah disetujui atau ditolak tidak dapat diubah");
        }


        
        
        // If canceled is true, set status to CANCELED
        if (updateLeaveRequestDTO.isCanceled()) {
            leaveRequest.setStatus(LeaveStatus.CANCELED);
            Long outletId = null;
            if (leaveRequest.getUser() instanceof Barista) {
                Barista barista = (Barista) leaveRequest.getUser();
                if (barista.getOutlet() != null) {
                outletId = barista.getOutlet().getOutletId();
                }
            }

            Outlet outlet = outletDb.findById(outletId).orElseThrow(() -> new Exception("Outlet tidak ditemukan"));

            notificationService.notifyLeaveRequestCanceled(id, outlet.getHeadbar().getUsername());
        } else {
            // Update fields
            if (updateLeaveRequestDTO.getRequestDate() != null) {
                leaveRequest.setRequestDate(updateLeaveRequestDTO.getRequestDate());
            }
            if (updateLeaveRequestDTO.getLeaveType() != null) {
                leaveRequest.setLeaveType(updateLeaveRequestDTO.getLeaveType());
            }
            leaveRequest.setReason(updateLeaveRequestDTO.getReason());
        }
        
        // Save to database
        leaveRequest = leaveRequestDb.save(leaveRequest);
        
        // Return response
        return mapToLeaveRequestResponseDTO(leaveRequest);
    }

    @Override
    public LeaveRequestResponseDTO approveRejectLeaveRequest(UUID id, ApproveRejectLeaveRequestDTO approveRejectLeaveRequestDTO) throws Exception {
        Optional<LeaveRequest> leaveRequestOptional = leaveRequestDb.findById(id);
        if (leaveRequestOptional.isEmpty()) {
            throw new Exception("Permohonan izin/cuti tidak ditemukan");
        }
        
        LeaveRequest leaveRequest = leaveRequestOptional.get();
        
        // Check if request is already processed
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new Exception("Permohonan sudah diproses sebelumnya");
        }
        
        // Update status
        leaveRequest.setStatus(approveRejectLeaveRequestDTO.getStatus());
        
        // Save to database
        leaveRequest = leaveRequestDb.save(leaveRequest);
        notificationService.notifyLeaveRequestApproved(leaveRequest.getId(), leaveRequest.getUser().getUsername());
        System.out.println("APU");
        
        // Return response
        return mapToLeaveRequestResponseDTO(leaveRequest);
    }
    
    private LeaveRequestResponseDTO mapToLeaveRequestResponseDTO(LeaveRequest leaveRequest) {
        LeaveRequestResponseDTO dto = new LeaveRequestResponseDTO();
        dto.setId(leaveRequest.getId());
        dto.setUserName(leaveRequest.getUser().getFullName());
        dto.setRequestDate(leaveRequest.getRequestDate());
        dto.setLeaveType(leaveRequest.getLeaveType());
        dto.setReason(leaveRequest.getReason());
        dto.setStatus(leaveRequest.getStatus());
        dto.setCreatedAt(leaveRequest.getCreatedAt());
        dto.setUpdatedAt(leaveRequest.getUpdatedAt());
        // Ambil outlet dan headbar dari user (Barista)
        if (leaveRequest.getUser() instanceof Barista) {
            Barista barista = (Barista) leaveRequest.getUser();
            if (barista.getOutlet() != null) {
                dto.setIdOutlet(barista.getOutlet().getOutletId()); // ID Outlet dari database
            }
        }

        return dto;
    }

    @Override
    public LeaveRequestResponseDTO getLeaveRequestByUsernameAndDate(String username, Date requestDate) throws Exception {
        Optional<LeaveRequest> leaveRequestOptional = leaveRequestDb.findByUserUsernameAndRequestDate(username, requestDate);
        if (leaveRequestOptional.isEmpty()) {
            throw new Exception("Permohonan izin/cuti tidak ditemukan");
        }
        
        return mapToLeaveRequestResponseDTO(leaveRequestOptional.get());
    }

    @Override
    public LeaveRequestResponseDTO updateLeaveRequestByUsernameAndDate(String username, Date requestDate, UpdateLeaveRequestDTO updateLeaveRequestDTO) throws Exception {
        Optional<LeaveRequest> leaveRequestOptional = leaveRequestDb.findByUserUsernameAndRequestDate(username, requestDate);
        if (leaveRequestOptional.isEmpty()) {
            throw new Exception("Permohonan izin/cuti tidak ditemukan");
        }
        
        LeaveRequest leaveRequest = leaveRequestOptional.get();
        
        // Check if request is already approved or rejected
        if (leaveRequest.getStatus() == LeaveStatus.APPROVED || leaveRequest.getStatus() == LeaveStatus.REJECTED) {
            throw new Exception("Permohonan yang sudah disetujui atau ditolak tidak dapat diubah");
        }
        
        // Continue with the existing logic for update
        if (updateLeaveRequestDTO.isCanceled()) {
            leaveRequest.setStatus(LeaveStatus.CANCELED);
        } else {
            if (updateLeaveRequestDTO.getRequestDate() != null) {
                leaveRequest.setRequestDate(updateLeaveRequestDTO.getRequestDate());
            }
            if (updateLeaveRequestDTO.getLeaveType() != null) {
                leaveRequest.setLeaveType(updateLeaveRequestDTO.getLeaveType());
            }
            leaveRequest.setReason(updateLeaveRequestDTO.getReason());
        }
        
        leaveRequest = leaveRequestDb.save(leaveRequest);
        
        return mapToLeaveRequestResponseDTO(leaveRequest);
    }

    @Override
    public LeaveRequestResponseDTO approveRejectLeaveRequestByUsernameAndDate(String username, Date requestDate, ApproveRejectLeaveRequestDTO approveRejectLeaveRequestDTO) throws Exception {
        Optional<LeaveRequest> leaveRequestOptional = leaveRequestDb.findByUserUsernameAndRequestDate(username, requestDate);
        if (leaveRequestOptional.isEmpty()) {
            throw new Exception("Permohonan izin/cuti tidak ditemukan");
        }
        
        LeaveRequest leaveRequest = leaveRequestOptional.get();
        
        // Check if request is already processed
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new Exception("Permohonan sudah diproses sebelumnya");
        }
        
        // Update status
        leaveRequest.setStatus(approveRejectLeaveRequestDTO.getStatus());
        
        // Save to database
        leaveRequest = leaveRequestDb.save(leaveRequest);
        
        return mapToLeaveRequestResponseDTO(leaveRequest);
    }
}