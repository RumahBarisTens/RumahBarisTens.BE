package propensi.tens.bms.features.account_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequestDTO {

    @NotBlank
    private String role;
    
    @NotBlank
    private String fullName;

    @NotNull
    private Boolean gender;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String address;

    @NotBlank
    private Date dateOfBirth;

    private String outletName;

    @NotBlank
    private String status;
}
