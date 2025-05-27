package propensi.tens.bms.features.trainee_management.dto.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeerReviewAssignmentResponseDTO {
    private Integer peerReviewAssignmentId;
    private String reviewerUsername;
    private String revieweeUsername;
    private Date endDateFill;
}
