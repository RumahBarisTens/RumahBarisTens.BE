package propensi.tens.bms.features.account_management.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.tens.bms.features.account_management.models.Admin;

import java.util.UUID;

@Repository
public interface AdminDb extends JpaRepository<Admin, UUID>{
    
}
