package propensi.tens.bms.features.account_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaristaAll {
    private String id;
    private String fullName;
    private String role;
    private String outlet;
    private String status;
}
