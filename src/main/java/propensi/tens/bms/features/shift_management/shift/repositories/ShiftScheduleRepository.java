package propensi.tens.bms.features.shift_management.shift.repositories;


import propensi.tens.bms.features.shift_management.shift.models.ShiftSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {

    List<ShiftSchedule> findByOutletId(Long outletId);

    List<ShiftSchedule> findByOutletIdAndDateShiftBetween(Long outletId, LocalDate startDate, LocalDate endDate);

    Optional<ShiftSchedule> findByDateShiftAndShiftTypeAndOutletIdAndHeadBarId(LocalDate dateShift, Integer shiftType, Long outletId, UUID headBarId);

    @Query("SELECT s FROM ShiftSchedule s WHERE s.outletId = :outletId")
    List<ShiftSchedule> getShiftsByUserRole(@Param("role") String role, @Param("outletId") Long outletId);

    @Query("SELECT COUNT(s) FROM ShiftSchedule s WHERE s.deletedAt IS NULL AND s.dateShift >= CURRENT_DATE")
    long countActiveShifts();
    
    @Query("SELECT COUNT(s) FROM ShiftSchedule s WHERE s.deletedAt IS NULL AND s.shiftType = :shiftType")
    long countByShiftType(int shiftType);
    
    @Query("SELECT COUNT(s) FROM ShiftSchedule s WHERE s.deletedAt IS NULL AND s.outletId = :outletId AND s.dateShift >= CURRENT_DATE")
    long countActiveShiftsByOutletId(Long outletId);
    
    @Query("SELECT s FROM ShiftSchedule s WHERE s.deletedAt IS NULL AND s.dateShift = :date")
    List<ShiftSchedule> findByDateShift(LocalDate date);

    @Query("SELECT s FROM ShiftSchedule s JOIN s.listBarista b WHERE b.id = :userId AND EXTRACT(MONTH FROM s.dateShift) = :month AND EXTRACT(YEAR FROM s.dateShift) = :year")
    List<ShiftSchedule> findShiftsByUserIdAndMonthYear(@Param("userId") UUID userId, @Param("month") int month,
            @Param("year") int year);

    @Query("SELECT s FROM ShiftSchedule s JOIN s.listBarista b WHERE b.id = :userId AND s.dateShift = :date")
    List<ShiftSchedule> findShiftsByUserIdAndDate(@Param("userId") UUID userId, @Param("date") LocalDate date);
}