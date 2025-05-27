package propensi.tens.bms.features.shift_management.overtime.services;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import propensi.tens.bms.features.account_management.models.Barista;
import propensi.tens.bms.features.account_management.models.Outlet;
import propensi.tens.bms.features.account_management.repositories.BaristaDb;
import propensi.tens.bms.features.account_management.repositories.OutletDb;
import propensi.tens.bms.features.notification_management.services.NotificationService;
import propensi.tens.bms.features.shift_management.overtime.dto.request.OvertimeLogApprovalRequest;
import propensi.tens.bms.features.shift_management.overtime.dto.request.OvertimeLogRequest;
import propensi.tens.bms.features.shift_management.overtime.dto.request.OvertimeLogStatusRequest;
import propensi.tens.bms.features.shift_management.overtime.dto.response.OvertimeLogResponse;
import propensi.tens.bms.features.shift_management.overtime.models.OvertimeLog;
import propensi.tens.bms.features.shift_management.overtime.models.OvertimeLog.OvertimeStatus;
import propensi.tens.bms.features.shift_management.overtime.repositories.OvertimeLogDb;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
@Service
public class OvertimeLogServiceImpl implements OvertimeLogService {

    @Autowired
    private OvertimeLogDb overtimeLogDb;
    
    @Autowired
    private OutletDb outletDb;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BaristaDb baristaDb;

    // Get All Overtime Logs with date range filtering
    @Override
    public List<OvertimeLogResponse> getAllOvertimeLogs(String status, String sort, LocalDate startDate, LocalDate endDate) {
        List<OvertimeLog> logs;

        // Determine status filter
        OvertimeLog.OvertimeStatus statusEnum = null;
        if (status != null) {
            try {
                statusEnum = OvertimeLog.OvertimeStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter: {}", status);
                throw new IllegalArgumentException("Status tidak valid!");
            }
        }

        // Fetch logs based on status and date range
        if (statusEnum != null) {
            if (startDate != null && endDate != null) {
                logs = overtimeLogDb.findByStatusAndDateOvertimeBetween(statusEnum, startDate, endDate);
            } else {
                logs = "desc".equalsIgnoreCase(sort)
                        ? overtimeLogDb.findByStatusOrderByDateOvertimeDesc(statusEnum)
                        : overtimeLogDb.findByStatusOrderByDateOvertimeAsc(statusEnum);
            }
        } else {
            if (startDate != null && endDate != null) {
                logs = overtimeLogDb.findByDateOvertimeBetween(startDate, endDate);
            } else {
                logs = overtimeLogDb.findAll();
            }
        }

        // Apply sorting
        logs.sort("asc".equalsIgnoreCase(sort)
                ? Comparator.comparing(OvertimeLog::getDateOvertime)
                : Comparator.comparing(OvertimeLog::getDateOvertime).reversed());

        return logs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get Overtime Logs by Barista ID
    @Override
    public List<OvertimeLogResponse> getOvertimeLogsByBarista(Integer baristaId) {
        return overtimeLogDb.findByBaristaIdOrderByDateOvertimeDesc(baristaId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get Overtime Logs by User ID
    @Override
    public List<OvertimeLogResponse> getOvertimeLogsByUser(UUID userId) {
        return overtimeLogDb.findByUserIdOrderByDateOvertimeDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get Overtime Log by ID
    @Override
    public OvertimeLogResponse getOvertimeLogById(Integer id) {
        OvertimeLog overtimeLog = overtimeLogDb.findById(id)
                .orElseThrow(() -> {
                    log.warn("Overtime log not found with id: {}", id);
                    return new RuntimeException("Overtime log tidak ditemukan dengan id: " + id);
                });

        return mapToResponse(overtimeLog);
    }

    // Create Overtime Log
    @Override
    public OvertimeLogResponse createOvertimeLog(OvertimeLogRequest request) {
        log.info("Membuat overtime log untuk baristaId: {}, userId: {}", request.getBaristaId(), request.getUserId());

        OvertimeLog overtimeLog = new OvertimeLog();
        overtimeLog.setBaristaId(request.getBaristaId());
        overtimeLog.setUserId(request.getUserId());
        overtimeLog.setOutletId(request.getOutletId());
        overtimeLog.setDateOvertime(request.getDateOvertime());
        overtimeLog.setStartHour(request.getStartHour());
        overtimeLog.setDuration(request.getDuration());
        overtimeLog.setReason(request.getReason());
        overtimeLog.setStatus(OvertimeLog.OvertimeStatus.PENDING);

        OvertimeLog savedLog = overtimeLogDb.save(overtimeLog);

        log.info("Overtime log berhasil disimpan dengan ID: {}", savedLog.getOvertimeLogId());

        return mapToResponse(savedLog);
    }

    // Get Detail Overtime Log
    @Override
    public OvertimeLogResponse getOvertimeLogDetail(Integer id) {
        OvertimeLog overtimeLog = overtimeLogDb.findById(id)
                .orElseThrow(() -> {
                    log.warn("Detail overtime log tidak ditemukan dengan id: {}", id);
                    return new RuntimeException("Detail overtime log tidak ditemukan dengan id: " + id);
                });

        return mapToResponse(overtimeLog);
    }
    
    // Update Overtime Log Status
    @Override
    public OvertimeLogResponse updateOvertimeLogStatus(Integer id, OvertimeLogStatusRequest request) {
        log.info("Memperbarui status lembur dengan ID: {}", id);
        
        OvertimeLog overtimeLog = overtimeLogDb.findById(id)
                .orElseThrow(() -> {
                    log.warn("Overtime log tidak ditemukan dengan id: {}", id);
                    return new RuntimeException("Overtime log tidak ditemukan dengan id: " + id);
                });
        
        try {
            OvertimeLog.OvertimeStatus newStatus;
            try {
                newStatus = OvertimeLog.OvertimeStatus.valueOf(request.getStatus());
                if (newStatus == OvertimeStatus.CANCELLED) {
                    Barista barista = baristaDb.findById(overtimeLog.getUserId())
                            .orElseThrow(() -> new RuntimeException("Barista tidak ditemukan dengan userId: " + overtimeLog.getUserId()));
                    final Long outletId = barista.getOutlet().getOutletId();
                    Outlet outlet = outletDb.findById(outletId)
                            .orElseThrow(() -> new RuntimeException("Outlet tidak ditemukan dengan outletId: " + outletId));

                    notificationService.notifyOvertimeLogCanceled(id, outlet.getHeadbar().getUsername());
                    }
            } catch (IllegalArgumentException e) {
                log.warn("Status tidak valid: {}", request.getStatus());
                throw new IllegalArgumentException("Status tidak valid: " + request.getStatus());
            }
            
            if (newStatus != OvertimeLog.OvertimeStatus.ONGOING && newStatus != OvertimeLog.OvertimeStatus.CANCELLED) {
                log.warn("Status hanya bisa diubah menjadi ONGOING atau CANCELLED");
                throw new IllegalArgumentException("Status hanya bisa diubah menjadi ONGOING atau CANCELLED");
            }
            
            if (overtimeLog.getStatus() != OvertimeLog.OvertimeStatus.PENDING) {
                log.warn("Hanya log lembur dengan status PENDING yang dapat diubah");
                throw new IllegalArgumentException("Hanya log lembur dengan status PENDING yang dapat diubah");
            }
            
            overtimeLog.setStatus(newStatus);
            
            OvertimeLog updatedLog = overtimeLogDb.save(overtimeLog);
            log.info("Status lembur berhasil diperbarui ke: {}", newStatus);
            
            return mapToResponse(updatedLog);
        } catch (RuntimeException e) {
            log.error("Gagal memperbarui status lembur: {}", e.getMessage());
            throw e;
        }
    }

    private OvertimeLogResponse mapToResponse(OvertimeLog overtimeLog) {
        return OvertimeLogResponse.builder()
                .id(overtimeLog.getOvertimeLogId())
                .baristaId(overtimeLog.getBaristaId())
                .userId(overtimeLog.getUserId())
                .outletId(overtimeLog.getOutletId())
                .dateOvertime(overtimeLog.getDateOvertime())
                .startHour(overtimeLog.getStartHour())
                .duration(overtimeLog.getDuration())
                .reason(overtimeLog.getReason())
                .status(overtimeLog.getStatus())
                .statusDisplay(overtimeLog.getStatus().getDisplayValue())
                .verifier(overtimeLog.getVerifier())
                .outletName(getOutletName(overtimeLog.getOutletId()))
                .baristaName(getBaristaName(overtimeLog.getUserId())) // Add this line
                .createdAt(overtimeLog.getCreatedAt())
                .updatedAt(overtimeLog.getUpdatedAt())
                .build();
    }
    
    @Override
    public String getOutletName(Integer outletId) {
        log.info("Mengambil nama outlet untuk outletId: {}", outletId);

        try {
            return outletDb.findById(Long.valueOf(outletId))
                    .map(Outlet::getName)
                    .orElse("Outlet Tidak Ditemukan");
        } catch (Exception e) {
            log.error("Error mengambil outletName untuk outletId {}: {}", outletId, e.getMessage());
            return "Outlet Error";
        }
    }

    // Add this method to your existing OvertimeLogService class
    @Override
    public OvertimeLogResponse approveOvertimeLog(Integer id, OvertimeLogApprovalRequest request) {
        log.info("Processing approval for overtime log with ID: {}", id);
        
        OvertimeLog overtimeLog = overtimeLogDb.findById(id)
                .orElseThrow(() -> {
                    log.warn("Overtime log tidak ditemukan dengan id: {}", id);
                    return new RuntimeException("Overtime log tidak ditemukan dengan id: " + id);
                });
        
        try {
            OvertimeLog.OvertimeStatus newStatus;
            try {
                newStatus = OvertimeLog.OvertimeStatus.valueOf(request.getStatus());
            } catch (IllegalArgumentException e) {
                log.warn("Status tidak valid: {}", request.getStatus());
                throw new IllegalArgumentException("Status tidak valid: " + request.getStatus());
            }
            
            if (newStatus != OvertimeLog.OvertimeStatus.APPROVED && newStatus != OvertimeLog.OvertimeStatus.REJECTED) {
                log.warn("Status hanya bisa diubah menjadi APPROVED atau REJECTED");
                throw new IllegalArgumentException("Status hanya bisa diubah menjadi APPROVED atau REJECTED");
            }
            
            if (overtimeLog.getStatus() != OvertimeLog.OvertimeStatus.PENDING) {
                log.warn("Hanya log lembur dengan status PENDING yang dapat disetujui atau ditolak");
                throw new IllegalArgumentException("Hanya log lembur dengan status PENDING yang dapat disetujui atau ditolak");
            }
            
            overtimeLog.setStatus(newStatus);
            overtimeLog.setVerifier(request.getVerifier());
            
            OvertimeLog updatedLog = overtimeLogDb.save(overtimeLog);
            log.info("Status lembur berhasil diperbarui ke: {}", newStatus);

            notificationService.notifyOvertimeLogApproved(id, getBaristaName(overtimeLog.getUserId()));
            
            return mapToResponse(updatedLog);
        } catch (Exception e) {
            log.error("Gagal memproses persetujuan lembur: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String getBaristaName(UUID baristaId) {
        log.info("Mengambil nama barista untuk baristaId: {}", baristaId);

        try {
            return baristaDb.findById(baristaId).get().getUsername();
        } catch (Exception e) {
            log.error("Error mengambil baristaName untuk baristaId {}: {}", baristaId, e.getMessage());
            return "Barista Error";
        }
    }
}
