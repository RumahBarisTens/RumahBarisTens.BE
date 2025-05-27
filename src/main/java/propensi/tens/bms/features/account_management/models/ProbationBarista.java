package propensi.tens.bms.features.account_management.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "probation_barista")
public class ProbationBarista extends EndUser {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "outlet_id", referencedColumnName = "outletId")
    private Outlet outlet;
    
    @Temporal(TemporalType.DATE)
    private Date probationEndDate;
}
