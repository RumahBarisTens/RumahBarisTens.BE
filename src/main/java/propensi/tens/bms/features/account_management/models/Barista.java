package propensi.tens.bms.features.account_management.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "barista")
public class Barista extends EndUser {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "outlet_id", referencedColumnName = "outletId")
    private Outlet outlet;

    @Column(name = "is_intern", nullable = false)
    private Boolean isIntern;

}
