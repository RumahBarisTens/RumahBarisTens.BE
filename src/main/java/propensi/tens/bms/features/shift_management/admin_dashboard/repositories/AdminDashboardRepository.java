package propensi.tens.bms.features.shift_management.admin_dashboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import propensi.tens.bms.features.account_management.models.EndUser;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AdminDashboardRepository extends JpaRepository<EndUser, UUID> {

    // Query 1: Ambil data shift schedule dasar
    @Query("SELECT ss.shiftScheduleId, eu.id as baristaId, eu.fullName as baristaName, " +
           "ss.dateShift, ss.shiftType, o.outletId, o.name as outletName " +
           "FROM ShiftSchedule ss " +
           "JOIN ss.listBarista eu " +
           "LEFT JOIN Barista b ON eu.id = b.id " +
           "LEFT JOIN ProbationBarista pb ON eu.id = pb.id " +
           "LEFT JOIN Outlet o ON (b.outlet.outletId = o.outletId OR pb.outlet.outletId = o.outletId) " +
           "WHERE ss.dateShift BETWEEN :startDate AND :endDate " +
           "AND (:outletId IS NULL OR o.outletId = :outletId) " +
           "AND (:baristaName IS NULL OR eu.fullName LIKE %:baristaName%)")
    List<Object[]> findBasicShiftData(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("outletId") Long outletId,
            @Param("baristaName") String baristaName);

    // Query 2: Ambil data overtime terpisah
    @Query("SELECT ol.userId, ol.dateOvertime, ol.duration, ol.status, ol.outletId, o.name as outletName " +
           "FROM OvertimeLog ol " +
           "LEFT JOIN Outlet o ON ol.outletId = o.outletId " +
           "WHERE ol.dateOvertime BETWEEN :startDate AND :endDate " +
           "AND ol.status = 'APPROVED'")
    List<Object[]> findApprovedOvertimes(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Query 3: Ambil data overtime untuk barista tertentu
    @Query("SELECT ol.userId, ol.dateOvertime, ol.duration, ol.status, ol.outletId, o.name as outletName " +
           "FROM OvertimeLog ol " +
           "LEFT JOIN Outlet o ON ol.outletId = o.outletId " +
           "WHERE ol.userId = :baristaId " +
           "AND ol.dateOvertime BETWEEN :startDate AND :endDate " +
           "AND ol.status = 'APPROVED'")
    List<Object[]> findApprovedOvertimesByBaristaId(
            @Param("baristaId") UUID baristaId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}