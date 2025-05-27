package propensi.tens.bms.features.account_management.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import propensi.tens.bms.features.account_management.models.ProbationBarista;

import java.util.UUID;
import java.util.Date;
import java.util.List;

@Repository
public interface ProbationBaristaDb extends JpaRepository<ProbationBarista, UUID>{
    List<ProbationBarista> findByOutlet_OutletId(Long outletId);
    List<ProbationBarista> findByOutlet_OutletIdAndStatusIgnoreCase(Long outletId, String status);
    @Query("SELECT pb FROM ProbationBarista pb WHERE pb.outlet.name = :outletName")
    List<ProbationBarista> findByOutletName(String outletName);
    
    // Mencari ProbationBarista berdasarkan status
    List<ProbationBarista> findByStatusIgnoreCase(String status);
    
    // Mencari ProbationBarista berdasarkan username (untuk fitur search)
    @Query("SELECT pb FROM ProbationBarista pb WHERE LOWER(pb.username) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<ProbationBarista> findByUsernameContainingIgnoreCase(String search);
    
    // Mencari ProbationBarista berdasarkan tanggal akhir probation
    List<ProbationBarista> findByProbationEndDateAfter(Date date);
    
    // Mencari ProbationBarista berdasarkan nama outlet dan status
    @Query("SELECT pb FROM ProbationBarista pb WHERE pb.outlet.name = :outletName AND LOWER(pb.status) = LOWER(:status)")
    List<ProbationBarista> findByOutletNameAndStatusIgnoreCase(String outletName, String status);
}
