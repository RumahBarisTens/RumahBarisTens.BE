package propensi.tens.bms.features.trainee_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import propensi.tens.bms.features.trainee_management.enums.AssessmentTemplate;
import propensi.tens.bms.features.trainee_management.models.AssessmentQuestion;

public interface AssessmentQuestionRepository extends JpaRepository<AssessmentQuestion, Long> {
    List<AssessmentQuestion> findByAssessmentId(Long assessmentId);

    List<AssessmentQuestion> findByTemplate(AssessmentTemplate template);
}
