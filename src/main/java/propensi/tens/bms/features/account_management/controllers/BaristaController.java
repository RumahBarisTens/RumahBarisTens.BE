package propensi.tens.bms.features.account_management.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import propensi.tens.bms.features.account_management.services.BaristaService;
import propensi.tens.bms.features.account_management.dto.response.BaristaAll;
import propensi.tens.bms.features.account_management.dto.response.BaristaDropdownResponseDTO;
import propensi.tens.bms.features.account_management.dto.response.BaristaStatsResponse;

import java.util.List;

@RestController
@RequestMapping("/api/baristas")
public class BaristaController {

    @Autowired
    private BaristaService baristaService;

    @GetMapping
    public ResponseEntity<List<BaristaDropdownResponseDTO>> getBaristasByOutlet(
        @RequestParam Long outletId,
        @RequestParam(required = false) String status) {
        List<BaristaDropdownResponseDTO> baristas = baristaService.getBaristasForDropdown(outletId, status);
        return ResponseEntity.ok(baristas);
    }

    @GetMapping("/stats")
    public ResponseEntity<BaristaStatsResponse> getStats() {
        return ResponseEntity.ok(baristaService.getStats());
    }


    @GetMapping("/all")
    public ResponseEntity<List<BaristaAll>> getAllBaristasDetailed() {
        return ResponseEntity.ok(baristaService.getAllBaristasDetailed());
    }

}
