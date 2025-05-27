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
     * Mendapatkan semua shift berdasarkan peran pengguna dan outlet ID.
     * @param role peran pengguna (misalnya, HeadBarista, Barista, dll.)
     * @param outletId ID outlet
     * @return daftar shift schedule
     */
    List<ShiftScheduleResponseDto> getShiftsByUserRole(String role, Long outletId);

    /**
     * Membuat jadwal shift baru.
     * @param dto payload request jadwal shift
     * @return jadwal shift yang telah dibuat
     */
    ShiftScheduleResponseDto createShift(ShiftScheduleRequestDto dto);

    /**
     * Mendapatkan shift berdasarkan outlet dan rentang tanggal.
     * @param outletId ID outlet
     * @param startDate tanggal mulai
     * @param endDate tanggal akhir
     * @return daftar shift schedule
     */
    List<ShiftScheduleResponseDto> getShiftsByOutletAndDateRange(Long outletId, LocalDate startDate, LocalDate endDate);

    /**
     * Mendapatkan ringkasan shift berdasarkan userId dan bulan.
     * @param userId ID pengguna
     * @param month bulan yang ingin diambil
     * @return Ringkasan shift
     */
    ShiftSummary getShiftSummary(UUID userId, String month);
    
    /**
     * Mengkonversi ShiftSchedule entity menjadi ShiftScheduleResponseDto.
     * @param shift entity ShiftSchedule
     * @return DTO ShiftScheduleResponseDto
     */
    ShiftScheduleResponseDto convertToResponseDto(ShiftSchedule shift);
}
