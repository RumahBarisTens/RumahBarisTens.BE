package propensi.tens.bms.features.shift_management.shift.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ShiftScheduleRequestDto {

    @NotNull
    private Integer shiftType; // 1 = Pagi, 2 = Sore

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateShift; // Format input yyyy-MM-dd

    @NotNull
    private Long outletId; 

    @NotNull
    private UUID headBarId; // UUID Head Bar user yang assign shift

    @NotNull
    private List<UUID> baristaIds; // List UUID baristas yang di assign
}
