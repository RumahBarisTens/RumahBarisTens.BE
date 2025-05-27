package propensi.tens.bms.features.account_management.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clevel")
public class CLevel extends EndUser {

    @Column(name = "clevel_type", nullable = false)
    private String cLevelType;
}
