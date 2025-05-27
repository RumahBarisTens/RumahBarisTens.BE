package propensi.tens.bms.features.notification_management.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.notification_management.models.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(EndUser recipient);
    Page<Notification> findByRecipientOrderByCreatedAtDesc(EndUser recipient, Pageable pageable);
    List<Notification> findByRecipientAndIsReadOrderByCreatedAtDesc(EndUser recipient, boolean isRead);
    long countByRecipientAndIsRead(EndUser recipient, boolean isRead);
    List<Notification> findByResourceTypeAndResourceId(String resourceType, Long resourceId);
}