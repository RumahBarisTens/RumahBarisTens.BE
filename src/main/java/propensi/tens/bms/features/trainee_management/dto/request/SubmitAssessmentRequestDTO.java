package propensi.tens.bms.features.trainee_management.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class SubmitAssessmentRequestDTO {
    private String username;
    private Long assessmentId;
    private List<QuestionAnswerDTO> answers;

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class QuestionAnswerDTO {
        private Long questionId;
        // untuk MC:
        private Long selectedOptionId;
        // untuk Essay:
        private String essayAnswer;
    }
}
