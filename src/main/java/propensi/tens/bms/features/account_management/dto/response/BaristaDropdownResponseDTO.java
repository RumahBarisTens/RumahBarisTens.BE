package propensi.tens.bms.features.account_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BaristaDropdownResponseDTO {
    private String id;       
    private String fullName;
    private String role;  
}


