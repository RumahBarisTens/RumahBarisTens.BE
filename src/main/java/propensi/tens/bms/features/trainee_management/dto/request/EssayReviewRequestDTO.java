package propensi.tens.bms.features.trainee_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EssayReviewRequestDTO {
    private Long submissionId;
    private double essayScore;
}