package com.thatninjaguyspeaks.hazelcast.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thatninjaguyspeaks.hazelcast.models.MapConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigUtils {

    public static MapConfiguration extractMapConfig(String mapName, List<MapConfiguration> configurations) {
        return configurations.stream()
                .filter(config -> config.getMapName().equals(mapName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No configuration found for map: " + mapName));
    }
}