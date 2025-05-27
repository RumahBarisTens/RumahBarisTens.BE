package propensi.tens.bms.features.shift_management.admin_dashboard.services;

import propensi.tens.bms.features.account_management.dto.response.OutletResponseDTO;
import propensi.tens.bms.features.shift_management.admin_dashboard.dto.request.AdminDashboardFilterRequestDTO;
import propensi.tens.bms.features.shift_management.admin_dashboard.dto.response.AdminBaristaShiftDetailDTO;
import propensi.tens.bms.features.shift_management.admin_dashboard.dto.response.BaristaShiftSummaryDTO;

import java.util.List;
import java.util.UUID;

public interface AdminDashboardService {
    List<BaristaShiftSummaryDTO> getShiftSummary(AdminDashboardFilterRequestDTO request);
    List<AdminBaristaShiftDetailDTO> getBaristaShiftDetails(UUID baristaId, AdminDashboardFilterRequestDTO request);
    List<OutletResponseDTO> getAllOutlets();
}