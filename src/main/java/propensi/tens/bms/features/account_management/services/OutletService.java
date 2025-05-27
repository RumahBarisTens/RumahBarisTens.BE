package propensi.tens.bms.features.account_management.services;

import propensi.tens.bms.features.account_management.dto.response.OutletResponseDTO;
import propensi.tens.bms.features.account_management.models.Outlet;

import java.util.List;
import java.util.Optional;

public interface OutletService {
    List<OutletResponseDTO> getAllOutlets();
    Optional<Outlet> findById(Long id);

}
