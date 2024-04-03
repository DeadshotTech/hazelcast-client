package com.thatninjaguyspeaks.hazelcast.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for putting data into a Hazelcast map")
public class MapPutRequestDTO {

        @NotEmpty(message = "mapName cannot be null or empty")
        @Schema(description = "Name of the Hazelcast map", required = true, example = "myMap")
        private String mapName;

        @NotEmpty(message = "value cannot be null or empty")
        @Schema(description = "List of map entries to put into the Hazelcast map", required = true,
                example = "[{\"key1\":\"value1\"}, {\"key2\":\"value2\"}]")
        private List<Map<String, Object>> value;

}
