package propensi.tens.bms.features.account_management.models;

import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "outlet")
public class Outlet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outletId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "headbar_id", referencedColumnName = "id")
    private HeadBar headbar;

    @OneToMany(mappedBy = "outlet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Barista> listBarista;

}
