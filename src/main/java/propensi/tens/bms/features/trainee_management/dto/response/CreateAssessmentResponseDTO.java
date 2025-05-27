package propensi.tens.bms.features.trainee_management.dto.response;

import java.time.LocalDate;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import propensi.tens.bms.features.trainee_management.enums.AssessmentTemplate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAssessmentResponseDTO {
    private AssessmentTemplate template;
    private LocalDate deadline;
    private List<String> assignedUsername;
}
