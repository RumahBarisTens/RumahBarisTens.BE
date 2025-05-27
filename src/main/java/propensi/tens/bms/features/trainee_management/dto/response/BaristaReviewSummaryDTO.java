package propensi.tens.bms.features.trainee_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaristaReviewSummaryDTO {
    private String username;
    private Double averageScore;
    private Integer reviewsCompleted;
    private Integer reviewsTotal;
    private String status;
    private String lastReviewDate;
    private String outlet;
    private String position;
    private String probationEndDate;
    private String trend;
    private Double trendValue;
}