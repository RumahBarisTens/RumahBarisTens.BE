package propensi.tens.bms.features.account_management.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.tens.bms.features.account_management.dto.response.OutletResponseDTO;
import propensi.tens.bms.features.account_management.models.Outlet;
import propensi.tens.bms.features.account_management.repositories.OutletDb;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.UUID;

@Service
public class OutletServiceImpl implements OutletService {

    @Autowired
    private OutletDb outletRepository;

    @Override
    public List<OutletResponseDTO> getAllOutlets() {
        List<Outlet> outlets = outletRepository.findAll();

        return outlets.stream().map(outlet -> {
            String headBarName = outlet.getHeadbar() != null
                    ? outlet.getHeadbar().getFullName() // Sesuaikan dengan field HeadBar kamu
                    : null;
            UUID headBarId = outlet.getHeadbar() != null ? outlet.getHeadbar().getId() : null;


            return new OutletResponseDTO(
                    outlet.getOutletId(),
                    outlet.getName(),
                    outlet.getLocation(),
                    headBarName,
                    headBarId
            );
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<Outlet> findById(Long id) {
        return outletRepository.findById(id);
    }
}
