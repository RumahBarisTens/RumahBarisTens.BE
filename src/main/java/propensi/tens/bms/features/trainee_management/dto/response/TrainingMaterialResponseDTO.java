package propensi.tens.bms.features.trainee_management.dto.response;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import propensi.tens.bms.features.trainee_management.enums.AssignedRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingMaterialResponseDTO {
    private Long id;
    private String title;
    private String type;
    private String link;
    private String description;
    private List<AssignedRole> assignedRoles;
    private Date createdAt;
}
