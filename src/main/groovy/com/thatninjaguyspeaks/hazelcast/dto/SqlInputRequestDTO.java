package com.thatninjaguyspeaks.hazelcast.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlInputRequestDTO {

    @Schema(description = "Name of the Hazelcast map to operate on", required = true)
    private String mapName;

    @Schema(description = "Java class Type of key", required = true)
    private String keyType;

    @Schema(description = "Java class Type of value", required = true)
    private String valueType;
}
