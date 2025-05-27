package propensi.tens.bms.features.shift_management.admin_dashboard.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import propensi.tens.bms.features.account_management.dto.response.OutletResponseDTO;
import propensi.tens.bms.features.account_management.models.Outlet;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.models.HeadBar;
import propensi.tens.bms.features.account_management.repositories.OutletDb;
import propensi.tens.bms.features.account_management.repositories.HeadBarDb;
import propensi.tens.bms.features.shift_management.admin_dashboard.dto.request.AdminDashboardFilterRequestDTO;
import propensi.tens.bms.features.shift_management.admin_dashboard.dto.response.AdminBaristaShiftDetailDTO;
import propensi.tens.bms.features.shift_management.admin_dashboard.dto.response.BaristaShiftSummaryDTO;
import propensi.tens.bms.features.shift_management.admin_dashboard.repositories.AdminDashboardRepository;
import propensi.tens.bms.features.shift_management.overtime.models.OvertimeLog;
import propensi.tens.bms.features.shift_management.overtime.models.OvertimeLog.OvertimeStatus;
import propensi.tens.bms.features.shift_management.overtime.repositories.OvertimeLogDb;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {
    @Autowired
    private AdminDashboardRepository adminDashboardRepository;
    
    @Autowired
    private OutletDb outletDb;
    
    @Autowired
    private HeadBarDb headBarDb;
    
    @Autowired
    private OvertimeLogDb overtimeLogDb;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // 1. Get Shift Summary
    @Override
    public List<BaristaShiftSummaryDTO> getShiftSummary(AdminDashboardFilterRequestDTO request) {
        validateRequest(request);
        
        // Step 1: Fetch shift data
        List<Object[]> shiftData = adminDashboardRepository.findBasicShiftData(
            request.getStartDate(),
            request.getEndDate(),
            request.getOutletId(),
            request.getBaristaName()
        );

        // Step 2: Fetch overtime data - now fetching all approved overtimes
        List<OvertimeLog> overtimeLogs = overtimeLogDb.findByDateOvertimeBetween(
            request.getStartDate(),
            request.getEndDate()
        ).stream()
        .filter(log -> log.getStatus() == OvertimeStatus.APPROVED)
        .collect(Collectors.toList());
        
        logger.info("Fetched {} approved overtime logs for period {} to {}", 
            overtimeLogs.size(), request.getStartDate(), request.getEndDate());

        // Step 3: Map to DTO
        Map<UUID, BaristaShiftSummaryDTO> resultMap = new HashMap<>();

        // Process shift data
        for (Object[] row : shiftData) {
            UUID baristaId = (UUID) row[1];
            LocalDate shiftDate = (LocalDate) row[3];
            if (shiftDate == null) {
                logger.warn("Null shift date found for baristaId={}", baristaId);
                continue;
            }
            BaristaShiftSummaryDTO dto = resultMap.computeIfAbsent(baristaId, id -> {
                BaristaShiftSummaryDTO newDto = new BaristaShiftSummaryDTO();
                newDto.setBaristaId(baristaId);
                newDto.setBaristaName((String) row[2]);
                newDto.setOutletId((Long) row[5]);
                newDto.setOutletName((String) row[6]);
                newDto.setShifts(new ArrayList<>());
                newDto.setOvertimes(new ArrayList<>());
                return newDto;
            });
            
            dto.getShifts().add(new BaristaShiftSummaryDTO.ShiftDetail(
                shiftDate,
                (Integer) row[4]
            ));
            dto.setTotalShifts(dto.getShifts().size());
        }

        // Process overtime data with improved handling
        for (OvertimeLog log : overtimeLogs) {
            UUID baristaId = log.getUserId();
            
            // Skip if barista is not in our result map (not in the shift data)
            if (!resultMap.containsKey(baristaId) && request.getBaristaName() != null) {
                continue;
            }
            
            BaristaShiftSummaryDTO dto = resultMap.computeIfAbsent(baristaId, id -> {
                BaristaShiftSummaryDTO newDto = new BaristaShiftSummaryDTO();
                newDto.setBaristaId(baristaId);
                newDto.setBaristaName(getBaristaName(baristaId));
                newDto.setOutletId(log.getOutletId().longValue());
                newDto.setOutletName(getOutletName(log.getOutletId()));
                newDto.setShifts(new ArrayList<>());
                newDto.setOvertimes(new ArrayList<>());
                return newDto;
            });
            
            // Calculate duration in minutes
            int durationMinutes = convertToMinutes(log.getDuration());
            
            // Add overtime detail
            dto.getOvertimes().add(new BaristaShiftSummaryDTO.OvertimeDetail(
                log.getOvertimeLogId(),
                log.getDateOvertime(),
                log.getStartHour(),
                log.getDuration(),
                durationMinutes,
                log.getStatus(),
                log.getStatus().getDisplayValue(),
                log.getReason()
            ));
            
            // Update total overtime minutes
            dto.setTotalOvertimeMinutes(dto.getTotalOvertimeMinutes() + durationMinutes);
        }
        
        // Calculate unique overtime days for each barista
        for (BaristaShiftSummaryDTO dto : resultMap.values()) {
            Set<LocalDate> uniqueOvertimeDays = new HashSet<>();
            for (BaristaShiftSummaryDTO.OvertimeDetail overtime : dto.getOvertimes()) {
                uniqueOvertimeDays.add(overtime.getOvertimeDate());
            }
            dto.setOvertimeDays(uniqueOvertimeDays.size());
        }

        return new ArrayList<>(resultMap.values());
    }

    // 2. Get Detail Shift per Barista
    @Override
    public List<AdminBaristaShiftDetailDTO> getBaristaShiftDetails(UUID baristaId, AdminDashboardFilterRequestDTO request) {
        validateRequest(request);
        
        List<Object[]> shiftData = adminDashboardRepository.findBasicShiftData(
            request.getStartDate(),
            request.getEndDate(),
            request.getOutletId(),
            null
        ).stream()
         .filter(row -> baristaId.equals(row[1]))
         .collect(Collectors.toList());

        // Fetch all overtime logs for this barista in the date range
        List<OvertimeLog> overtimeLogs = overtimeLogDb.findByUserIdOrderByDateOvertimeDesc(baristaId)
            .stream()
            .filter(log -> !log.getDateOvertime().isBefore(request.getStartDate()) && 
                          !log.getDateOvertime().isAfter(request.getEndDate()))
            .collect(Collectors.toList());
        
        logger.info("Fetched {} overtime logs for baristaId={} for period {} to {}", 
            overtimeLogs.size(), baristaId, request.getStartDate(), request.getEndDate());

        List<AdminBaristaShiftDetailDTO> result = new ArrayList<>();
        
        // Create a map of date to overtime log for quick lookup
        Map<LocalDate, OvertimeLog> overtimeByDate = overtimeLogs.stream()
            .collect(Collectors.toMap(
                OvertimeLog::getDateOvertime,
                log -> log,
                (existing, replacement) -> existing // Keep first one in case of duplicates
            ));
        
        for (Object[] row : shiftData) {
            LocalDate shiftDate = (LocalDate) row[3];
            if (shiftDate == null) {
                logger.warn("Null shift date found for baristaId={}", baristaId);
                continue;
            }
            AdminBaristaShiftDetailDTO dto = new AdminBaristaShiftDetailDTO();
            dto.setShiftDate(shiftDate);
            dto.setShiftType((Integer) row[4]);
            dto.setOutletId((Long) row[5]);
            dto.setOutletName((String) row[6]);
            
            // Check if there's an overtime for this date
            OvertimeLog overtimeLog = overtimeByDate.get(shiftDate);
            if (overtimeLog != null && overtimeLog.getStatus() == OvertimeStatus.APPROVED) {
                dto.setHasOvertime(true);
                dto.setOvertimeStatus(overtimeLog.getStatus().getDisplayValue());
                int durationMinutes = convertToMinutes(overtimeLog.getDuration());
                dto.setOvertimeDuration(durationMinutes / 60); // Convert minutes to hours
            }
            
            result.add(dto);
        }
        
        // Add days with overtime but no shift
        for (OvertimeLog log : overtimeLogs) {
            if (log.getStatus() == OvertimeStatus.APPROVED) {
                boolean hasShiftForDate = result.stream()
                    .anyMatch(dto -> dto.getShiftDate().equals(log.getDateOvertime()));
                
                if (!hasShiftForDate) {
                    AdminBaristaShiftDetailDTO dto = new AdminBaristaShiftDetailDTO();
                    dto.setShiftDate(log.getDateOvertime());
                    dto.setShiftType(null); // No shift
                    dto.setOutletId(log.getOutletId().longValue());
                    dto.setOutletName(getOutletName(log.getOutletId()));
                    dto.setHasOvertime(true);
                    dto.setOvertimeStatus(log.getStatus().getDisplayValue());
                    int durationMinutes = convertToMinutes(log.getDuration());
                    dto.setOvertimeDuration(durationMinutes / 60); // Convert minutes to hours
                    
                    result.add(dto);
                }
            }
        }
        
        // Sort by date
        result.sort((a, b) -> a.getShiftDate().compareTo(b.getShiftDate()));

        return result;
    }

    // 3. Get All Outlets
    @Override
    public List<OutletResponseDTO> getAllOutlets() {
        return outletDb.findAll().stream()
            .map(outlet -> {
                HeadBar headBar = headBarDb.findByOutlet_OutletId(outlet.getOutletId())
                    .stream().findFirst().orElse(null);
                return new OutletResponseDTO(
                    outlet.getOutletId(),
                    outlet.getName(),
                    outlet.getLocation(),
                    headBar != null ? headBar.getFullName() : null,
                    headBar != null ? headBar.getId() : null
                );
            })
            .collect(Collectors.toList());
    }

    // Validasi request
    private void validateRequest(AdminDashboardFilterRequestDTO request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Tanggal mulai tidak boleh setelah tanggal selesai");
        }
    }

    // Fetch barista name from EndUser
    private String getBaristaName(UUID baristaId) {
        return adminDashboardRepository.findById(baristaId)
            .map(EndUser::getFullName)
            .orElse("Unknown Barista");
    }
    
    // Get outlet name
    private String getOutletName(Integer outletId) {
        try {
            return outletDb.findById(Long.valueOf(outletId))
                    .map(Outlet::getName)
                    .orElse("Outlet Tidak Ditemukan");
        } catch (Exception e) {
            logger.error("Error mengambil outletName untuk outletId {}: {}", outletId, e.getMessage());
            return "Outlet Error";
        }
    }
    
    // Convert LocalTime to minutes
    private int convertToMinutes(LocalTime time) {
        if (time == null) {
            return 0;
        }
        return time.getHour() * 60 + time.getMinute();
    }
}
