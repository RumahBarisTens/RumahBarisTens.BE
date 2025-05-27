package propensi.tens.bms.features.trainee_management.dto.request;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import propensi.tens.bms.features.trainee_management.enums.AssignedRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingMaterialRequestDTO {
    private String title;
    private String link;
    private String description;
    private List<AssignedRole> assignedRoles;
}
