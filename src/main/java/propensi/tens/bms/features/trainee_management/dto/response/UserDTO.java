package propensi.tens.bms.features.trainee_management.dto.response;

import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String fullName;

    public UserDTO(UUID id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }
    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
}
