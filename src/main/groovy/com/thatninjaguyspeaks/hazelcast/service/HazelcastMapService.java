package com.thatninjaguyspeaks.hazelcast.service;

import com.thatninjaguyspeaks.hazelcast.dto.MapGetRequestDTO;
import com.thatninjaguyspeaks.hazelcast.dto.MapPutRequestDTO;
import org.springframework.stereotype.Service;

@Service
public interface HazelcastMapService {
    Object getMapData(MapGetRequestDTO requestDTO);

    void putMapData(MapPutRequestDTO data);

    void putMapData();

    void deleteMap();
}
