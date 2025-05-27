package propensi.tens.bms.features.account_management.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.tens.bms.features.account_management.models.Outlet;


@Repository
public interface OutletDb extends JpaRepository<Outlet, Long>{
    Outlet findByName(String name);
    Outlet findByOutletId(Long outletId);
}
