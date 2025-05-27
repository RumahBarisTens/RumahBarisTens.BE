package propensi.tens.bms.features.shift_management.overtime.services;

import propensi.tens.bms.features.shift_management.overtime.dto.request.OvertimeLogApprovalRequest;
import propensi.tens.bms.features.shift_management.overtime.dto.request.OvertimeLogRequest;
import propensi.tens.bms.features.shift_management.overtime.dto.request.OvertimeLogStatusRequest;
import propensi.tens.bms.features.shift_management.overtime.dto.response.OvertimeLogResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface OvertimeLogService {
    public List<OvertimeLogResponse> getAllOvertimeLogs(String status, String sort, LocalDate startDate, LocalDate endDate);
    public List<OvertimeLogResponse> getOvertimeLogsByBarista(Integer baristaId);
    public List<OvertimeLogResponse> getOvertimeLogsByUser(UUID userId);
    public OvertimeLogResponse getOvertimeLogById(Integer id);
    public OvertimeLogResponse createOvertimeLog(OvertimeLogRequest request);
    public OvertimeLogResponse getOvertimeLogDetail(Integer id);
    public OvertimeLogResponse updateOvertimeLogStatus(Integer id, OvertimeLogStatusRequest request);
    public String getOutletName(Integer outletId);
    public OvertimeLogResponse approveOvertimeLog(Integer id, OvertimeLogApprovalRequest request);
    public String getBaristaName(UUID baristaId);
}
