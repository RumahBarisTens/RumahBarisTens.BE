package propensi.tens.bms.features.account_management.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "head_bar")
public class HeadBar extends EndUser {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "outlet_id", referencedColumnName = "outletId")
    private Outlet outlet;

}
