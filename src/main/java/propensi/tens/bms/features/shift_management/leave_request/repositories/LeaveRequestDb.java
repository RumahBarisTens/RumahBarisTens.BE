package propensi.tens.bms.features.shift_management.leave_request.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.shift_management.leave_request.models.LeaveRequest;
import propensi.tens.bms.features.shift_management.leave_request.models.LeaveStatus;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveRequestDb extends JpaRepository<LeaveRequest, UUID> {
    List<LeaveRequest> findByUser(EndUser user);
    List<LeaveRequest> findByUserUsername(String username);
    Optional<LeaveRequest> findByUserUsernameAndRequestDate(String username, Date requestDate);
    boolean existsByUserUsernameAndRequestDateAndStatusNot(String username, Date requestDate, LeaveStatus status);
}