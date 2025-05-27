package propensi.tens.bms.features.trainee_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import propensi.tens.bms.features.trainee_management.models.QuestionOption;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findByQuestionId(Long questionId);

    List<QuestionOption> findByQuestionIdAndIsCorrectTrue(Long questionId);
}
