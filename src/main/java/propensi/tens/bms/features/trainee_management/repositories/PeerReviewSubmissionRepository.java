package propensi.tens.bms.features.trainee_management.repositories;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import propensi.tens.bms.features.trainee_management.models.PeerReviewAssignment;
import propensi.tens.bms.features.trainee_management.models.PeerReviewSubmission;

public interface PeerReviewSubmissionRepository extends JpaRepository<PeerReviewSubmission, Integer> {
    List<PeerReviewSubmission> findByAssignmentReviewerUsername(String reviewerUsername);
    List<PeerReviewSubmission> findByAssignmentRevieweeUsername(String revieweeUsername);

    List<PeerReviewSubmission> findByReviewedAtBetween(Date startDate, Date endDate);
    PeerReviewSubmission findByAssignment(PeerReviewAssignment assignment);
    // @Query("SELECT prs FROM PeerReviewSubmission prs JOIN prs.assignment pra JOIN pra.user u WHERE u.username = :username")
    // List<PeerReviewSubmission> findSubmissionsByUsername(@Param("username") String username);
}
