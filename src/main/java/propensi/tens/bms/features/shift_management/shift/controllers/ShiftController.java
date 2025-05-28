package propensi.tens.bms.features.shift_management.shift.controllers;

import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.shift_management.shift.dto.request.ShiftScheduleRequestDto;
import propensi.tens.bms.features.shift_management.shift.dto.response.ShiftScheduleResponseDto;
import propensi.tens.bms.features.shift_management.shift.models.ShiftSchedule;
import propensi.tens.bms.features.shift_management.shift.models.ShiftSummary;
import propensi.tens.bms.features.shift_management.shift.services.ShiftService;
import propensi.tens.bms.features.shift_management.shift.repositories.ShiftScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("api/shift")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private ShiftScheduleRepository shiftScheduleRepository;

    private final Logger logger = LoggerFactory.getLogger(ShiftController.class);


    @GetMapping("/{outletId}")
    public ResponseEntity<?> getShiftsByOutletAndDateRange(
            @PathVariable Long outletId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        logger.info("===== START getShiftsByOutletAndDateRange =====");
        logger.info("Received request: outletId=" + outletId + ", startDate=" + startDate + ", endDate=" + endDate);

        try {
            if (startDate == null || endDate == null) {
                logger.error("Missing required query params: startDate or endDate is null");
                return ResponseEntity.badRequest().body("Missing required query parameters: startDate or endDate.");
            }

            LocalDate start;
            LocalDate end;

            try {
                start = LocalDate.parse(startDate);
                logger.info("Parsed startDate: " + start);
            } catch (Exception e) {
                logger.error("Failed to parse startDate: " + startDate, e);
                return ResponseEntity.badRequest().body("Invalid startDate format. Expected format: YYYY-MM-DD");
            }

            try {
                end = LocalDate.parse(endDate);
                logger.info("Parsed endDate: " + end);
            } catch (Exception e) {
                logger.error("Failed to parse endDate: " + endDate, e);
                return ResponseEntity.badRequest().body("Invalid endDate format. Expected format: YYYY-MM-DD");
            }

            // Validasi range date
            if (start.isAfter(end)) {
                logger.error("Start date is after end date! startDate=" + start + ", endDate=" + end);
                return ResponseEntity.badRequest().body("Start date cannot be after end date.");
            }

            // Log sebelum ke service
            logger.info("Calling service getShiftsByOutletAndDateRange with outletId=" + outletId + ", start=" + start + ", end=" + end);

            List<ShiftScheduleResponseDto> shifts = shiftService.getShiftsByOutletAndDateRange(outletId, start, end);

            logger.info("Service returned " + shifts.size() + " shift(s)");

            logger.info("===== END getShiftsByOutletAndDateRange SUCCESS =====");
            return ResponseEntity.ok(shifts);

        } catch (Exception e) {
            logger.error("===== ERROR getShiftsByOutletAndDateRange =====", e);
            e.printStackTrace(); // Tampilkan stack trace lengkap
            return ResponseEntity.internalServerError().body("Internal server error. Check logs for details.");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ShiftScheduleResponseDto> createShift(@RequestBody ShiftScheduleRequestDto requestDto) {
        ShiftScheduleResponseDto responseDto = shiftService.createShift(requestDto);
        return ResponseEntity.ok(responseDto);
    }



  
    @DeleteMapping("/{shiftId}")
    public ResponseEntity<?> deleteShift(@PathVariable Long shiftId) {
        boolean deleted = shiftService.softDeleteShift(shiftId);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Shift not found");
        }

        return ResponseEntity.ok("Shift deleted (soft)");
    }


    @DeleteMapping("/api/shift/{shiftId}/barista/{baristaId}")
    public ResponseEntity<?> removeBaristaFromShift(
        @PathVariable Long shiftId,
        @PathVariable UUID baristaId
    ) {
        Optional<ShiftSchedule> optionalShift = shiftScheduleRepository.findById(shiftId);
        if (optionalShift.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shift not found");
        }

        ShiftSchedule shift = optionalShift.get();

        // Filter barista
        List<EndUser> updatedBaristas = shift.getListBarista().stream()
            .filter(b -> !b.getId().equals(baristaId))
            .collect(Collectors.toList());

        if (updatedBaristas.isEmpty()) {
            // Kalau barista udah habis, hapus shift-nya (soft delete atau hard delete)
            shiftScheduleRepository.delete(shift); // atau soft-delete
            return ResponseEntity.ok("Shift deleted because no baristas left");
        }

        // Masih ada barista, update list dan save
        shift.setListBarista(updatedBaristas);
        shiftScheduleRepository.save(shift);

        return ResponseEntity.ok("Barista removed from shift");
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getShiftDetailByDate(
            @RequestParam UUID userId,
            @RequestParam String date) {

        logger.info("===== START getShiftDetailByDate =====");

        try {
            LocalDate shiftDate = LocalDate.parse(date);
            List<ShiftSchedule> shifts = shiftScheduleRepository.findShiftsByUserIdAndDate(userId, shiftDate);

            List<ShiftScheduleResponseDto> shiftDetails = shifts.stream()
                    .map(shift -> shiftService.convertToResponseDto(shift))
                    .collect(Collectors.toList());

            logger.info("===== END getShiftDetailByDate SUCCESS =====");
            return ResponseEntity.ok(shiftDetails);

        } catch (Exception e) {
            logger.error("===== ERROR getShiftDetailByDate =====", e);
            return ResponseEntity.internalServerError().body("Internal server error. Check logs for details.");
        }
    }

    @GetMapping("/personal-summary")
    public ResponseEntity<?> getPersonalShiftSummary(
            @RequestParam UUID userId,
            @RequestParam(required = false, defaultValue = "current") String period) {

        logger.info("===== START getPersonalShiftSummary =====");
        logger.info("Received request: userId=" + userId + ", period=" + period);

        try {
            // Handle period parameter
            String monthYear;

            // Cek apakah format period adalah format bulan sederhana (seperti di endpoint
            // /summary lama)
            if (period.matches("\\d{1,2}")) {
                // Format bulan sederhana (contoh: "4" untuk April tahun ini)
                int month = Integer.parseInt(period);
                int year = LocalDate.now().getYear();
                monthYear = month + "-" + year;
                logger.info("Simple month format detected: " + period + ", converted to: " + monthYear);
            }
            // Cek apakah format period sudah dalam format "MM-YYYY"
            else if (period.matches("\\d{1,2}-\\d{4}")) {
                // Format sudah benar, gunakan langsung
                monthYear = period;
            }
            // Handle nilai khusus
            else if ("current".equals(period)) {
                // Bulan saat ini
                LocalDate now = LocalDate.now();
                monthYear = now.getMonthValue() + "-" + now.getYear();
            } else if ("previous".equals(period)) {
                // Bulan sebelumnya
                LocalDate previousMonth = LocalDate.now().minusMonths(1);
                monthYear = previousMonth.getMonthValue() + "-" + previousMonth.getYear();
            } else {
                // Format tidak valid
                logger.error("Invalid period format: " + period);
                return ResponseEntity.badRequest().body(
                        "Format period tidak valid. Gunakan 'current', 'previous', nomor bulan (1-12), atau format 'MM-YYYY'.");
            }

            ShiftSummary summary = shiftService.getShiftSummary(userId, monthYear);

            logger.info("===== END getPersonalShiftSummary SUCCESS =====");
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            logger.error("===== ERROR getPersonalShiftSummary =====", e);
            return ResponseEntity.internalServerError().body("Internal server error. Check logs for details.");
        }
    }
}