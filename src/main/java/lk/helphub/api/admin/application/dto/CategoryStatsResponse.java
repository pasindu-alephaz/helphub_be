package lk.helphub.api.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsResponse {
    private UUID subcategoryId;
    private Map<String, String> name;
    private long jobCount;
}
