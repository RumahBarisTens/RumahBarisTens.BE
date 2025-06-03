package propensi.tens.bms.features.shift_management.shift.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Optional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import propensi.tens.bms.features.account_management.dto.response.BaristaDropdownResponseDTO;
import propensi.tens.bms.features.account_management.models.Barista;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.models.HeadBar;
import propensi.tens.bms.features.account_management.models.ProbationBarista;
import propensi.tens.bms.features.account_management.repositories.EndUserDb;
import propensi.tens.bms.features.account_management.services.EndUserService;
import propensi.tens.bms.features.account_management.services.OutletService;
import propensi.tens.bms.features.notification_management.services.NotificationService;
import propensi.tens.bms.features.shift_management.overtime.dto.response.OvertimeLogResponse;
import propensi.tens.bms.features.shift_management.overtime.services.OvertimeLogService;
import propensi.tens.bms.features.shift_management.shift.controllers.ShiftController;
import propensi.tens.bms.features.shift_management.shift.dto.request.ShiftScheduleRequestDto;
import propensi.tens.bms.features.shift_management.shift.dto.response.ShiftScheduleResponseDto;
import propensi.tens.bms.features.shift_management.shift.models.ShiftDetail;
import propensi.tens.bms.features.shift_management.shift.models.ShiftSchedule;
import propensi.tens.bms.features.shift_management.shift.models.ShiftSummary;
import propensi.tens.bms.features.shift_management.shift.repositories.ShiftScheduleRepository;

@Service
public class ShiftServiceImpl implements ShiftService {

    @Autowired
    private ShiftScheduleRepository shiftScheduleRepository;

    @Autowired
    private OvertimeLogService overtimeLogService;

    private final Logger logger = LoggerFactory.getLogger(ShiftController.class);

    @Autowired
    private EndUserDb endUserRepository;

    @Autowired
    private OutletService outletService;

    @Autowired
    private EndUserService endUserService;

    @Autowired private NotificationService notificationService;

    @Override
    public List<ShiftScheduleResponseDto> getShiftsByUserRole(String role, Long outletId) {
        List<ShiftSchedule> shiftSchedules;

        if (role.equalsIgnoreCase("HeadBar") || role.equalsIgnoreCase("HeadBarista")) {
            shiftSchedules = shiftScheduleRepository.findByOutletId(outletId);
        } else if (role.equalsIgnoreCase("Barista") || role.equalsIgnoreCase("ProbationBarista")) {
            shiftSchedules = shiftScheduleRepository.findByOutletId(outletId);
        } else {
            // Admin / C-Level biasanya fetch pakai range, jadi kosongin di sini
            shiftSchedules = new ArrayList<>();
        }

        return shiftSchedules.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShiftScheduleResponseDto createShift(ShiftScheduleRequestDto dto) {
        // Membuat objek ShiftSchedule baru
        ShiftSchedule shift = new ShiftSchedule();
        System.out.println("ShiftServiceImpl.createShift: dto = " + dto);
        // Set shiftType
        shift.setShiftType(dto.getShiftType());

        // Pastikan dateShift sudah berupa LocalDate
        shift.setDateShift(dto.getDateShift());  // dateShift di DTO sudah LocalDate, jadi langsung set ke LocalDate

        // Mendapatkan headBarId dari DTO
        UUID headBarId = dto.getHeadBarId();
        if (headBarId == null) {
            throw new IllegalArgumentException("HeadBarId is required"); // HeadBarId wajib ada
        }
        shift.setHeadBarId(headBarId);  // Set headBarId ke ShiftSchedule

        // Set outlet ID
        shift.setOutletId(dto.getOutletId());

        // Logika untuk assign barista jika ada listBaristaIds di request DTO
        if (dto.getBaristaIds() != null && !dto.getBaristaIds().isEmpty()) {
            // Mendapatkan barista berdasarkan ID yang dikirim dari frontend
            List<EndUser> baristas = endUserRepository.findAllById(dto.getBaristaIds());
            shift.setListBarista(baristas);  // Set barista ke shift
        }

        // Simpan shift ke database
        ShiftSchedule savedShift = shiftScheduleRepository.save(shift);

        notificationService.notifyShiftAssigned(savedShift.getShiftScheduleId(), savedShift.getListBarista());

        // Kembalikan response DTO yang sudah diubah
        return convertToResponseDto(savedShift);
    }

    @Override
    public List<ShiftScheduleResponseDto> getShiftsByOutletAndDateRange(Long outletId, LocalDate startDate, LocalDate endDate) {
        try {
            logger.info("Fetching shifts for outlet " + outletId + " from " + startDate + " to " + endDate);

            List<ShiftSchedule> shiftEntities = shiftScheduleRepository.findByOutletIdAndDateShiftBetween(outletId, startDate, endDate);

            if (shiftEntities.isEmpty()) {
                logger.warn("No shifts found for outlet " + outletId + " in the given date range!");
            }

            return shiftEntities.stream().map(this::convertToResponseDto).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching shifts - " + e.getMessage());
            e.printStackTrace(); // Menampilkan stack trace error untuk debugging
            throw new RuntimeException("Failed to fetch shifts. Check logs for details.");
        }
    }

    @Override
    public ShiftScheduleResponseDto convertToResponseDto(ShiftSchedule shift) {
        ShiftScheduleResponseDto dto = new ShiftScheduleResponseDto();

        dto.setShiftScheduleId(shift.getShiftScheduleId());
        dto.setShiftType(shift.getShiftType());
        dto.setDateShift(shift.getDateShift());

        dto.setOutletId(shift.getOutletId());
        dto.setOutletName(getOutletName(shift.getOutletId()));

        dto.setHeadBarId(shift.getHeadBarId());
        dto.setHeadBarName(getHeadBarName(shift.getHeadBarId()));

        List<BaristaDropdownResponseDTO> baristasDto = shift.getListBarista().stream().map(barista -> {
            BaristaDropdownResponseDTO bDto = new BaristaDropdownResponseDTO();
            bDto.setId(barista.getId().toString());
            bDto.setFullName(barista.getFullName());
            bDto.setRole(getRoleFromUser(barista));
            return bDto;
        }).collect(Collectors.toList());

        dto.setBaristas(baristasDto);

        return dto;
    }

    private String getOutletName(Long outletId) {
        return outletService.findById(outletId)
                .map(outlet -> outlet.getName()) // Make sure field is 'name'
                .orElse("Outlet Tidak Ditemukan");
    }

    private String getHeadBarName(UUID headBarId) {
        return endUserService.findById(headBarId)
                .map(user -> user.getFullName())
                .orElse("HeadBar Tidak Ditemukan");
    }

    private String getRoleFromUser(EndUser user) {
        if (user instanceof HeadBar) return "HeadBar";
        if (user instanceof Barista) return "Barista";
        if (user instanceof ProbationBarista) return "ProbationBarista";
        return "Unknown";
    }

    
    public boolean softDeleteShift(Long shiftId) {
        Optional<ShiftSchedule> optionalShift = shiftScheduleRepository.findById(shiftId);
        if (optionalShift.isEmpty()) return false;

        ShiftSchedule shift = optionalShift.get();
        shift.setDeletedAt(LocalDateTime.now());
        shiftScheduleRepository.save(shift);

        return true;
    }
    
    @Override
    public ShiftSummary getShiftSummary(UUID userId, String monthYear) {
        try {
            // Parse month and year from input (format: "MM-YYYY")
            String[] parts = monthYear.split("-");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            
            // Get all shifts for this user in the specified month
            List<ShiftSchedule> userShifts = shiftScheduleRepository.findShiftsByUserIdAndMonthYear(userId, month, year);
            
            // Count completed days (distinct dates)
            long completedDays = userShifts.stream()
                    .map(ShiftSchedule::getDateShift)
                    .distinct()
                    .count();
            
            // Get overtime logs for this user
            List<OvertimeLogResponse> overtimeLogs = overtimeLogService.getOvertimeLogsByUser(userId);
            
            // Filter overtime logs for the specified month and year
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();
            
            List<OvertimeLogResponse> filteredOvertimeLogs = overtimeLogs.stream()
                    .filter(log -> {
                        LocalDate logDate = log.getDateOvertime();
                        return !logDate.isBefore(startOfMonth) && !logDate.isAfter(endOfMonth);
                    })
                    .collect(Collectors.toList());
            
            // Create calendar data with shift details
            Map<LocalDate, ShiftDetail> calendarData = new HashMap<>();
            
            // Group shifts by date
            Map<LocalDate, List<ShiftSchedule>> shiftsByDate = userShifts.stream()
                    .collect(Collectors.groupingBy(ShiftSchedule::getDateShift));
            
            // For each date, create a ShiftDetail
            shiftsByDate.forEach((date, shifts) -> {
                ShiftSchedule shift = shifts.get(0); // Get first shift if multiple on same day
                
                // Check if there's overtime for this date
                boolean hasOvertime = filteredOvertimeLogs.stream()
                        .anyMatch(log -> log.getDateOvertime().equals(date));
                
                ShiftDetail detail = new ShiftDetail(
                    shift.getShiftType(),
                    getOutletName(shift.getOutletId()),
                    ShiftDetail.getShiftTimeByType(shift.getShiftType()),
                    hasOvertime
                );
                
                calendarData.put(date, detail);
            });
            
            // Create ShiftSummary with overtime logs
            return new ShiftSummary(completedDays, filteredOvertimeLogs, calendarData);
        } catch (Exception e) {
            logger.error("Error getting shift summary: " + e.getMessage(), e);
            return new ShiftSummary(); // Return empty summary on error
        }
    }
}