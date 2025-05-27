package propensi.tens.bms.features.account_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndUserResponseDTO {
    private String fullName;
    private String username;
    private Boolean gender;
    private String role;
    private String phoneNumber;
    private String address;
    private Date dateOfBirth;
    private String status;
    private String outlet;
    private Date createdAt;
    private Date updatedAt;
}
