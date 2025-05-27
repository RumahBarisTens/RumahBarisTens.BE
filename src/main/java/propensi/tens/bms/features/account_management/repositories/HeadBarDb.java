package propensi.tens.bms.features.account_management.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.tens.bms.features.account_management.models.HeadBar;

import java.util.UUID;
import java.util.List;

@Repository
public interface HeadBarDb extends JpaRepository<HeadBar, UUID>{
    List<HeadBar> findByOutlet_OutletId(Long outletId);
    List<HeadBar> findByOutlet_OutletIdAndStatusIgnoreCase(Long outletId, String status);
}
