package propensi.tens.bms.features.trainee_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EssayReviewRequest {
    private Long submissionId;
    private Double essayScore;
}