package propensi.tens.bms.features.trainee_management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssessmentRequest {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    private List<String> assignedUsername;
}
