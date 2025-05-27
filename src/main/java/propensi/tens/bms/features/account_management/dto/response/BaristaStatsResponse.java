package propensi.tens.bms.features.account_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaristaStatsResponse {
    private int total;
    private int active;
    private int inactive;
    private int outlets;
    private List<SimpleStat> outletStats;
    private List<SimpleStat> roleStats;
    private List<SimpleStat> statusStats;
}
