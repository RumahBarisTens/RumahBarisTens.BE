package propensi.tens.bms.features.account_management.services;

import propensi.tens.bms.features.account_management.models.EndUser;

import java.util.Optional;
import java.util.UUID;

public interface EndUserService {
    Optional<EndUser> findById(UUID id); // Atau UUID kalau di entity lo
}
