package com.thatninjaguyspeaks.hazelcast.service.impl;

import com.hazelcast.map.IMap;
import com.thatninjaguyspeaks.hazelcast.config.ConfigurationLoader;
import com.thatninjaguyspeaks.hazelcast.config.HazelcastClientInitializer;
import com.thatninjaguyspeaks.hazelcast.dto.MapPutRequestDTO;
import com.thatninjaguyspeaks.hazelcast.models.MapConfiguration;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastMapService;
import com.thatninjaguyspeaks.hazelcast.utils.ConfigUtils;
import com.thatninjaguyspeaks.hazelcast.utils.DataConvertorUtils;
import com.thatninjaguyspeaks.hazelcast.utils.HazelcastUtils;
import com.thatninjaguyspeaks.hazelcast.utils.KeyGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class HazelcastMapServiceImpl implements HazelcastMapService {

    private static final String MAP_NAME = "test_map";
    private static final Logger logger = LogManager.getLogger(HazelcastMapServiceImpl.class);

    @Autowired
    HazelcastClientInitializer hazelcastClientInitializer;
    @Autowired
    ConfigurationLoader configurationLoader;

    @Override
    public Object getMapData(Object key) {
        var hazelcastInstance = hazelcastClientInitializer.getHazelcastInstance();
        IMap<Object, Object> map = hazelcastInstance.getMap(MAP_NAME);
        Object value = map.get(key);
        logger.info("Returning value {}", value);
        return value;
    }

    @Override
    public void putMapData(MapPutRequestDTO mapPutRequestDTO) {
        logger.info("Starting map insertion");
        final var hz = hazelcastClientInitializer.getHazelcastInstance();
        final var mapName = mapPutRequestDTO.getMapName();
        final var map = hz.getMap(mapName);
        MapConfiguration mapConfig;
        List<Object> mapData=null;
        final KeyGenerator keyGenerator = new KeyGenerator(mapName, hz);

        try{
            mapConfig = ConfigUtils.extractMapConfig(mapName,
                    configurationLoader.getMapConfigurations());
            mapData = DataConvertorUtils.convertGenericToMapData(mapConfig, mapPutRequestDTO.getData());
            if(mapData!=null && !mapData.isEmpty())
                mapData.parallelStream().forEach((datum) -> {
                    logger.info("Datum: {}", datum);
                    map.put(HazelcastUtils.getKey(mapConfig.getKeyType(), keyGenerator), datum);
                });
            else
                logger.warn("No data was inserted as the data to be inserted is empty after processing");
        }catch (IOException e){
            logger.error("Configurations couldn't be loaded with error: {}", e.getMessage());
            e.printStackTrace();
        } catch (Exception e){
            logger.error("Error in processing data with error: {}", e.getMessage());
            e.printStackTrace();
        }
        logger.info("Map insertion completed");
    }

    @Override
    public void putMapData() {
        logger.info("Starting map default insertion");
//        var hz = hazelcastClientInitializer.getHazelcastInstance();
//        var map = hz.getMap(MAP_NAME);
//        for(var i=0; i<100000; i++){
//            Person person = Person.builder().age(25).name("Alice").build();
//            map.put(getKey(), person);
//        }
        logger.info("Obtained map and inserted value");
    }

    @Override
    public void deleteMap() {
        logger.info("Starting map destruction");
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        var map = hz.getMap(MAP_NAME);
        map.destroy();
        logger.info("Obtained map and deleted map");
    }

}
