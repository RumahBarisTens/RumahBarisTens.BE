package propensi.tens.bms.features.account_management.dto.response;

import lombok.Data;

@Data
public class UserCheckResponseDTO {
    private String username;
    private Boolean isVerified;
}
