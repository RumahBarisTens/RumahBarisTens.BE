package propensi.tens.bms.features.shift_management.shift.services;

import propensi.tens.bms.features.shift_management.shift.dto.request.ShiftScheduleRequestDto;
import propensi.tens.bms.features.shift_management.shift.dto.response.ShiftScheduleResponseDto;
import propensi.tens.bms.features.shift_management.shift.models.ShiftSchedule;
import propensi.tens.bms.features.shift_management.shift.models.ShiftSummary;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ShiftService {

    /**
     * Get all shifts by user role and outlet ID.
     * @param role the user role (e.g., HeadBarista, Barista, etc.)
     * @param outletId the outlet identifier
     * @return list of shift schedules
     */
    List<ShiftScheduleResponseDto> getShiftsByUserRole(String role, Long outletId);

    /**
     * Create a new shift schedule.
     * @param dto shift schedule request payload
     * @return created shift schedule
     */
    ShiftScheduleResponseDto createShift(ShiftScheduleRequestDto dto);


    List<ShiftScheduleResponseDto> getShiftsByOutletAndDateRange(Long outletId, LocalDate startDate, LocalDate endDate);
    // void deleteShiftById(Long id);

    boolean softDeleteShift(Long shiftId);

    ShiftSummary getShiftSummary(UUID userId, String monthYear);

    /**
     * Mengkonversi ShiftSchedule entity menjadi ShiftScheduleResponseDto.
     * @param shift entity ShiftSchedule
     * @return DTO ShiftScheduleResponseDto
     */
    ShiftScheduleResponseDto convertToResponseDto(ShiftSchedule shift);

}