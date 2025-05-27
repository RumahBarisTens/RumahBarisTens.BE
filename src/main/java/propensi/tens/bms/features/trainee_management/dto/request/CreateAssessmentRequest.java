package propensi.tens.bms.features.trainee_management.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import propensi.tens.bms.features.trainee_management.enums.AssessmentTemplate;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssessmentRequest {

    @NotNull
    private AssessmentTemplate template;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    @NotNull
    private List<String> assignedUsername;
}
