package propensi.tens.bms.features.account_management.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.tens.bms.features.account_management.models.EndUser;

@Repository
public interface EndUserDb extends JpaRepository<EndUser, UUID>{
    EndUser findByUsername(String username);
}
