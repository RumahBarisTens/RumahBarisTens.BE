package propensi.tens.bms.features.trainee_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import propensi.tens.bms.features.trainee_management.models.AssessmentAnswer;

import java.util.List;

@Repository
public interface AssessmentAnswerRepository extends JpaRepository<AssessmentAnswer, Long> {

    /**
     * Ambil semua AssessmentAnswer untuk submission tertentu
     */
    @Query("""
        SELECT a
          FROM AssessmentAnswer a
         WHERE a.submission.id = :submissionId
    """)
    List<AssessmentAnswer> findAnswersBySubmissionId(@Param("submissionId") Long submissionId);
}
