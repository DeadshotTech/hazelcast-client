package com.thatninjaguyspeaks.hazelcast.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapConfiguration {
    private String mapName;
    private String keyType;
    private String valueType;
}
