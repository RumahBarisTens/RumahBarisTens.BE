package propensi.tens.bms.features.trainee_management.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import propensi.tens.bms.features.trainee_management.enums.QuestionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDetailResponse {
    private Long answerId;
    private Long questionId;
    private String questionCode;
    private String questionText;
    private QuestionType questionType;
    private List<OptionDetailResponse> options;
    private Long selectedOptionId;
    private String essayAnswer;
    private Boolean isCorrect;
}