package propensi.tens.bms.features.account_management.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePersonalDataDTO {
    private String username;
    private String fullName;
    private String password;
    private Boolean gender;
    private String phoneNumber;
    private String address;
    private Date dateOfBirth;
}
