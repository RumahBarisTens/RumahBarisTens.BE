package propensi.tens.bms.features.account_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutletResponseDTO {
    private Long outletId;
    private String name;
    private String location;
    private String headBarName;
    private UUID headBarId; 
}

