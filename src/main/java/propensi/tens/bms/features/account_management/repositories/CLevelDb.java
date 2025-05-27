package propensi.tens.bms.features.account_management.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.tens.bms.features.account_management.models.CLevel;

import java.util.Optional;
import java.util.UUID;
import java.util.List;


@Repository
public interface CLevelDb extends JpaRepository<CLevel, UUID>{
    Optional<CLevel> findById(UUID id);
    List<CLevel> findBycLevelType(String cLevelType);
}
