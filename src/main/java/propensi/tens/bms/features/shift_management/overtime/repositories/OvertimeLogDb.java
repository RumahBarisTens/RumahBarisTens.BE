package propensi.tens.bms.features.shift_management.overtime.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.tens.bms.features.shift_management.overtime.models.OvertimeLog;
import propensi.tens.bms.features.shift_management.overtime.models.OvertimeLog.OvertimeStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OvertimeLogDb extends JpaRepository<OvertimeLog, Integer> {

    List<OvertimeLog> findByStatusOrderByDateOvertimeDesc(OvertimeStatus status);

    List<OvertimeLog> findByStatusOrderByDateOvertimeAsc(OvertimeStatus status);

    List<OvertimeLog> findByBaristaIdOrderByDateOvertimeDesc(Integer baristaId);

    List<OvertimeLog> findByUserIdOrderByDateOvertimeDesc(UUID userId);

    // Added methods for date range filtering
    List<OvertimeLog> findByStatusAndDateOvertimeBetween(OvertimeStatus status, LocalDate startDate, LocalDate endDate);

    List<OvertimeLog> findByDateOvertimeBetween(LocalDate startDate, LocalDate endDate);

    
}