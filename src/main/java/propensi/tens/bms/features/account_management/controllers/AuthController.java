package propensi.tens.bms.features.account_management.controllers;

import jakarta.persistence.EntityNotFoundException;
import propensi.tens.bms.core.security.jwt.JwtUtils;
import propensi.tens.bms.features.account_management.dto.request.LoginJwtRequestDTO;
import propensi.tens.bms.features.account_management.dto.response.BaseResponseDTO;
import propensi.tens.bms.features.account_management.dto.response.LoginJwtResponseDTO;
import propensi.tens.bms.features.account_management.dto.response.UserCheckResponseDTO;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.repositories.EndUserDb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EndUserDb endUserDb;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginJwtRequestDTO loginRequest) {
        BaseResponseDTO<LoginJwtResponseDTO> response = new BaseResponseDTO<>();
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);

            String username = loginRequest.getUsername();
            EndUser user = endUserDb.findByUsername(username);

            java.util.UUID userId = user.getId();

            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String jwt = jwtUtils.generateJwtToken(userId.toString(), username, roles);
            LoginJwtResponseDTO data = new LoginJwtResponseDTO(jwt);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Login berhasil!");
            response.setTimestamp(new Date());
            response.setData(data);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (AuthenticationException | EntityNotFoundException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Username atau Password salah!");
            response.setTimestamp(new Date());
            response.setData(null);

            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestBody Map<String, String> payload) {
        BaseResponseDTO<UserCheckResponseDTO> response = new BaseResponseDTO<>();
        String username = payload.get("username");
        EndUser user = endUserDb.findByUsername(username);

        if (user == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("Username tidak ditemukan");
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        UserCheckResponseDTO data = new UserCheckResponseDTO();
        data.setUsername(user.getUsername());
        data.setIsVerified(user.getIsVerified());
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("User ditemukan");
        response.setTimestamp(new Date());
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verify-default-password")
    public ResponseEntity<?> verifyDefaultPassword(@RequestBody Map<String, String> payload) {
        BaseResponseDTO<String> response = new BaseResponseDTO<>();
        String username = payload.get("username");
        String password = payload.get("password");
        EndUser user = endUserDb.findByUsername(username);

        if (user == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("Username tidak ditemukan");
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (user.getIsVerified()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("User sudah terverifikasi");
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(password, user.getPassword())) {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Default password cocok. Silahkan ganti password Anda.");
            response.setTimestamp(new Date());
            response.setData("CHANGE_PASSWORD");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Default password tidak valid");
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        BaseResponseDTO<String> response = new BaseResponseDTO<>();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Logout berhasil");
        response.setTimestamp(new Date());
        response.setData(null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


