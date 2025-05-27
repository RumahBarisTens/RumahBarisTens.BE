package propensi.tens.bms.features.trainee_management.repositories;

import java.util.List;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.trainee_management.models.PeerReviewAssignment;


@Repository
public interface PeerReviewAssignmentRepository extends JpaRepository<PeerReviewAssignment, Integer> {

    @Query("SELECT e FROM EndUser e WHERE TYPE(e) <> Admin")
    List<EndUser> findAllExceptAdmin();

    @Query("SELECT e FROM EndUser e WHERE TYPE(e) = ProbationBarista")
    List<EndUser> findAllProbationBarista();
    
    List<PeerReviewAssignment> findByRevieweeAndReviewedAtIsNull(EndUser reviewee);
    List<PeerReviewAssignment> findByReviewerAndReviewedAtIsNull(EndUser reviewer);

    List<PeerReviewAssignment> findByReviewer(EndUser reviewer);
    List<PeerReviewAssignment> findByReviewee(EndUser reviewee);

    List<PeerReviewAssignment> findByEndDateFillBetween(Date startDate, Date endDate);

}