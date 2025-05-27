package propensi.tens.bms.features.trainee_management.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import propensi.tens.bms.features.trainee_management.models.PeerReviewContent;

public interface PeerReviewContentRepository extends JpaRepository<PeerReviewContent, Integer> {
    List<PeerReviewContent> findAllByOrderByQuestionNumberAsc();
}
