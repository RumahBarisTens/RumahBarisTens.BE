package propensi.tens.bms.features.trainee_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutletSummaryDTO {
    private String name;
    private Double averageScore;
    private Integer baristaCount;
    private Double passRate;
    private Double reviewCompletionRate;
}