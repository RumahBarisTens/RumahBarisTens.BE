package propensi.tens.bms.features.trainee_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreatePeerReviewSubmissionRequest {
    @NotNull private Integer assignmentId;
    @NotNull private Double q1;
    @NotNull private Double q2;
    @NotNull private Double q3;
    @NotNull private Double q4;
    @NotNull private Double q5;
    @NotNull private Double q6;
    @NotNull private Double q7;
    @NotNull private Double q8;
    @NotNull private Double q9;
    @NotNull private Double q10;
}
