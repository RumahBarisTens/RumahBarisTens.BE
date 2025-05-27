package propensi.tens.bms.features.trainee_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SubmissionSummaryDTO {
    private Long submissionId;
    private Date submittedAt;
    private Double mcScore;
    private Double essayScore;
    private Double totalScore;
    private Boolean essayReviewed;
    private String username;
}