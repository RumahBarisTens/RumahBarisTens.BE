package propensi.tens.bms.features.trainee_management.dto.request;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeerReviewAssignmentRequestDTO {
    private String reviewerUsername;
    private String revieweeUsername;
    private Date endDateFill;
}
