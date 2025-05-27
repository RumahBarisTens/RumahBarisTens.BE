package propensi.tens.bms.features.trainee_management.dto.request;

import java.util.Date;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePeerReviewAssignmentRequestDTO {
    private List<String> reviewerUsernames;
    
    private Date endDateFill;
}