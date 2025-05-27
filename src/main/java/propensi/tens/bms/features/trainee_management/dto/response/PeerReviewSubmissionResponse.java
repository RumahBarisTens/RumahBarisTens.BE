package propensi.tens.bms.features.trainee_management.dto.response;

import java.util.Date;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PeerReviewSubmissionResponse {
    private Integer id;
    private Integer assignmentId;
    private String reviewerUsername;
    private String revieweeUsername;
    private Date reviewedAt;
    private Double q1,q2,q3,q4,q5,q6,q7,q8,q9,q10;
}
