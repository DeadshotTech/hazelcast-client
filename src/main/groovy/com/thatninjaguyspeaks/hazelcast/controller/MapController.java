package com.thatninjaguyspeaks.hazelcast.controller;


import com.thatninjaguyspeaks.hazelcast.config.HazelcastClientInitializer;
import com.thatninjaguyspeaks.hazelcast.dto.MapPutRequestDTO;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastMapService;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/com/thatninjaguyspeaks/hazelcast/map")
public class MapController<K, V> {

    @Autowired
    private HazelcastMapService hazelcastMapService;
    @Autowired
    private HazelcastSqlService hazelcastSqlService;
    @Autowired
    private HazelcastClientInitializer hazelcastClientInitializer;

    @GetMapping("/{key}")
    public ResponseEntity<Object> getMapData(@PathVariable K key){
        return ResponseEntity.ok(hazelcastMapService.getMapData(key));
    }

    @PutMapping("/put")
    public ResponseEntity<Boolean> putMapData(@RequestBody MapPutRequestDTO data){
        hazelcastMapService.putMapData(data);
        return null;
    }

    @PutMapping("/put-default")
    public ResponseEntity<Boolean> putMapData(){
        hazelcastMapService.putMapData();
        return null;
    }

    @DeleteMapping("/destoy-map")
    public ResponseEntity<Boolean> deleteMap() {
        hazelcastMapService.deleteMap();
        return null;
    }

    @PostMapping("/sql")
    public ResponseEntity<Boolean> executeSql() {
        hazelcastSqlService.executeSqlQuery();
        return null;
    }

//    @GetMapping("/client-reload")
//    public ResponseEntity<Boolean> reloadClientInstance(){
//        hazelcastClientInitializer.forceHazelcastClientCreation();
//        return null;
//    }

}
