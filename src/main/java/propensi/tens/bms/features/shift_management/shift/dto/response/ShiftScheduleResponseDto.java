package propensi.tens.bms.features.shift_management.shift.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import propensi.tens.bms.features.account_management.dto.response.BaristaDropdownResponseDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ShiftScheduleResponseDto {

    private Long shiftScheduleId;
    private Integer shiftType;
    private LocalDate dateShift;
    private Long outletId;
    private String outletName;
    private UUID headBarId;
    private String headBarName;
    private List<BaristaDropdownResponseDTO> baristas; // List of baristas associated with the shift

}
