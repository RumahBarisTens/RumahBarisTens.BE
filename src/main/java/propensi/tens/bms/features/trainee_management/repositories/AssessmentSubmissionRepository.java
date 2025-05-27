package propensi.tens.bms.features.trainee_management.repositories;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import propensi.tens.bms.features.trainee_management.dto.response.SubmissionSummaryDTO;
import propensi.tens.bms.features.trainee_management.models.AssessmentSubmission;
public interface AssessmentSubmissionRepository extends JpaRepository<AssessmentSubmission, Long> {
    @Query("""
        select new propensi.tens.bms.features.trainee_management.dto.response.SubmissionSummaryDTO(
          s.id,
          s.submittedAt,
          s.mcScore,
          s.essayScore,
          s.totalScore,
          s.essayReviewed,
          u.username
        )
        from AssessmentSubmission s
        join s.user u
       where s.assessment.id = :assessmentId
    """)
    List<SubmissionSummaryDTO> findSummariesByAssessmentId(@Param("assessmentId") Long assessmentId);

    @Query("""
        select new propensi.tens.bms.features.trainee_management.dto.response.SubmissionSummaryDTO(
          s.id,
          s.submittedAt,
          s.mcScore,
          s.essayScore,
          s.totalScore,
          s.essayReviewed,
          u.username
        )
        from AssessmentSubmission s
        join s.user u
       where u.id = :userId
       order by s.submittedAt desc
    """)
    List<SubmissionSummaryDTO> findAllByUserIdOrderBySubmittedAtDesc(@Param("userId") UUID userId);

    @Query("""
        SELECT AVG(s.totalScore)
        FROM AssessmentSubmission s
        WHERE s.assessment.id = :assessmentId
    """)
    Double calculateAverageScoreByAssessmentId(@Param("assessmentId") Long assessmentId);

    @Query("""
        SELECT 
            CASE
                WHEN s.totalScore < 60 THEN '< 60'
                WHEN s.totalScore < 70 THEN '60-69'
                WHEN s.totalScore < 80 THEN '70-79'
                WHEN s.totalScore < 90 THEN '80-89'
                ELSE '90-100'
            END as range,
            COUNT(s) as count
        FROM AssessmentSubmission s
        WHERE s.assessment.id = :assessmentId
        GROUP BY range
        ORDER BY range
    """)
    List<Object[]> getScoreDistributionByAssessmentId(@Param("assessmentId") Long assessmentId);
}
