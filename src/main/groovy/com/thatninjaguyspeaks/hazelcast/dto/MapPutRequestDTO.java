package com.thatninjaguyspeaks.hazelcast.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MapPutRequestDTO {
        String mapName;
        List<Map<String, Object>> data;

}
