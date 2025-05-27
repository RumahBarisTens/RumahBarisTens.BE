// UserRestController.java
package propensi.tens.bms.features.account_management.controllers;


import propensi.tens.bms.features.account_management.dto.request.ChangePasswordDTO;
import propensi.tens.bms.features.account_management.dto.request.CreateAccountRequestDTO;
import propensi.tens.bms.features.account_management.dto.request.UpdateAccountRoleStatusDTO;
import propensi.tens.bms.features.account_management.dto.request.UpdatePersonalDataDTO;
import propensi.tens.bms.features.account_management.dto.response.BaseResponseDTO;
import propensi.tens.bms.features.account_management.dto.response.EndUserResponseDTO;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.services.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequestDTO createAccountRequest) {
        BaseResponseDTO<String> response = new BaseResponseDTO<>();
        try {
            accountService.createAccount(createAccountRequest);
            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("User berhasil didaftarkan!");
            response.setTimestamp(new Date());
            response.setData("Registration successful");

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/viewall")
    public ResponseEntity<?> getAllEndUsers(@RequestParam(value = "role", required = false) String role,
                                            @RequestParam(value = "search", required = false) String search) {
        BaseResponseDTO<List<EndUserResponseDTO>> baseResponseDTO = new BaseResponseDTO<>();

        try {
            List<EndUserResponseDTO> users = accountService.getAllEndUsers(role, search);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("List of end users retrieved successfully");
            baseResponseDTO.setData(users);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Error retrieving end users: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getAccountDetail(@PathVariable("username") String username) {
        BaseResponseDTO<EndUserResponseDTO> baseResponseDTO = new BaseResponseDTO<>();
        try {
            EndUserResponseDTO policy = accountService.getAccountDetail(username);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(policy);
            baseResponseDTO.setMessage("Account with username " + username + " fetched");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("An error raised");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-role-status")
    public ResponseEntity<?> updateRoleAndStatus(
            @RequestParam("username") String username,
            @RequestBody UpdateAccountRoleStatusDTO dto) {
        BaseResponseDTO<EndUser> response = new BaseResponseDTO<>();
        try {
            EndUser updatedUser = accountService.updateAccountRoleAndStatus(username, dto);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Role dan Status user berhasil diperbarui.");
            response.setTimestamp(new Date());
            response.setData(updatedUser);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Gagal mengupdate role/status: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/update-personal")
    public ResponseEntity<?> updateUserPersonalData(
            @RequestParam("username") String username,
            @RequestBody UpdatePersonalDataDTO dto) {
        BaseResponseDTO<EndUser> response = new BaseResponseDTO<>();
        try {
            accountService.updateUserPersonalData(username, dto);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Data personal user berhasil diperbarui.");
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Gagal mengupdate data personal: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestParam("username") String username,
            @RequestBody ChangePasswordDTO dto) {
        
        BaseResponseDTO<EndUser> response = new BaseResponseDTO<>();
        try {
            accountService.changePassword(username, dto);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Password berhasil diubah.");
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Gagal mengubah password: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        }
    }
}

