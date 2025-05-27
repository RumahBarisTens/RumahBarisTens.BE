package propensi.tens.bms.features.account_management.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import propensi.tens.bms.features.account_management.models.Barista;

import java.util.UUID;
import java.util.List;

@Repository
public interface BaristaDb extends JpaRepository<Barista, UUID>{
    List<Barista> findByOutlet_OutletId(Long outletId);
    @Query("SELECT COUNT(b) FROM Barista b WHERE b.outlet.outletId = :outletId")
    long countByOutletId(Long outletId);

    List<Barista> findByOutlet_OutletIdAndStatusIgnoreCase(Long outletId, String status);

    @Query("SELECT b FROM Barista b WHERE b.isIntern = :isIntern")
    List<Barista> findAllTraineeBarista(Boolean isIntern);
}
