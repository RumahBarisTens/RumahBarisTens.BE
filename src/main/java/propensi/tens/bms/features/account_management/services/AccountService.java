package propensi.tens.bms.features.account_management.services;

import java.util.List;

import propensi.tens.bms.features.account_management.dto.request.ChangePasswordDTO;
import propensi.tens.bms.features.account_management.dto.request.CreateAccountRequestDTO;
import propensi.tens.bms.features.account_management.dto.request.UpdateAccountRoleStatusDTO;
import propensi.tens.bms.features.account_management.dto.request.UpdatePersonalDataDTO;
import propensi.tens.bms.features.account_management.dto.response.EndUserResponseDTO;
import propensi.tens.bms.features.account_management.models.EndUser;


public interface AccountService {
    String hashPassword(String password);
    void createAccount(CreateAccountRequestDTO createAccountRequestDTO) throws Exception;
    
    List<EndUserResponseDTO> getAllEndUsers(String role, String search);

    EndUserResponseDTO getAccountDetail(String username);

    EndUser updateAccountRoleAndStatus(String username, UpdateAccountRoleStatusDTO dto) throws Exception;

    EndUser updateUserPersonalData(String username, UpdatePersonalDataDTO dto) throws Exception;

    EndUser changePassword(String username, ChangePasswordDTO dto) throws Exception;

}
