package propensi.tens.bms.features.trainee_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSummaryDTO {
    private Integer questionNumber;
    private String text;
    private Double averageScore;
}