package propensi.tens.bms.features.trainee_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private Integer totalBaristas;
    private Double averageScore;
    private Double scoreTrend;
    private Double passRate;
    private Integer completedReviews;
    private Integer totalReviews;
}