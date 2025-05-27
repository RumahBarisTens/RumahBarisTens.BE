package propensi.tens.bms.features.account_management.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import propensi.tens.bms.features.account_management.dto.response.OutletResponseDTO;
import propensi.tens.bms.features.account_management.services.OutletService;

import java.util.List;

@RestController
@RequestMapping("/api/outlets")
public class OutletController {

    @Autowired
    private OutletService outletService;

    @GetMapping
    public ResponseEntity<List<OutletResponseDTO>> getAllOutlets() {
        List<OutletResponseDTO> outlets = outletService.getAllOutlets();
        return ResponseEntity.ok(outlets);
    }
}
