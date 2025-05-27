package propensi.tens.bms.features.account_management.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.repositories.EndUserDb;

import java.util.Optional;
import java.util.UUID;

@Service
public class EndUserServiceImpl implements EndUserService {

    @Autowired
    private EndUserDb endUserRepository;

    @Override
    public Optional<EndUser> findById(UUID id) {
        return endUserRepository.findById(id);
    }
}
