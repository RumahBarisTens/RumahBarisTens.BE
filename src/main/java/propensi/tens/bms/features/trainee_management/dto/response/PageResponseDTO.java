package propensi.tens.bms.features.trainee_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
    private List<T> content;
    private Integer totalPages;
    private Long totalElements;
    private Integer currentPage;
    private Integer size;
}