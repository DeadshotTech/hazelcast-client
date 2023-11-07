package com.thatninjaguyspeaks.hazelcast.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for getting data from a Hazelcast map")
public class MapGetRequestDTO {

    @NotEmpty(message = "mapName cannot be null or empty")
    @Schema(description = "Name of the Hazelcast map", required = true, example = "myMap")
    private String mapName;

    @NotEmpty(message = "key cannot be null or empty")
    @Schema(description = "key to get data from the Hazelcast map", required = true,
            example = "34 or \"34\"")
    private Object key;

}
