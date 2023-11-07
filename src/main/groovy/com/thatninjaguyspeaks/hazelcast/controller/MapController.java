package com.thatninjaguyspeaks.hazelcast.controller;

import com.thatninjaguyspeaks.hazelcast.dto.MapGetRequestDTO;
import com.thatninjaguyspeaks.hazelcast.dto.MapPutRequestDTO;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/com/thatninjaguyspeaks/hazelcast/map")
@Tag(name = "Map Controller", description = "Controller for Hazelcast map operations")
public class MapController {

    @Autowired
    private HazelcastMapService hazelcastMapService;

    @GetMapping("/{key}")
    @Operation(summary = "Get Map Data", description = "Retrieves data from the Hazelcast map for the given key")
    @ApiResponse(responseCode = "200", description = "Data retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Object.class)))
    public ResponseEntity<Object> getMapData(@PathVariable MapGetRequestDTO data){
        return ResponseEntity.ok(hazelcastMapService.getMapData(data));
    }

    @PutMapping("/put")
    @Operation(summary = "Put Map Data", description = "Puts data into the Hazelcast map")
    @ApiResponse(responseCode = "200", description = "Data put successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    public ResponseEntity<Boolean> putMapData(@Valid @RequestBody MapPutRequestDTO data){
        hazelcastMapService.putMapData(data);
        return null;
    }

    @Deprecated
    @PutMapping("/put-default")
    @Operation(summary = "Put Default Map Data", description = "Puts default data into the Hazelcast map")
    @ApiResponse(responseCode = "200", description = "Default data put successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    public ResponseEntity<Boolean> putMapData(){
        hazelcastMapService.putMapData();
        return null;
    }

    @DeleteMapping("/destoy-map")
    @Operation(summary = "Delete Map", description = "Destroys the Hazelcast map")
    @ApiResponse(responseCode = "200", description = "Map destroyed successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    public ResponseEntity<Boolean> deleteMap() {
        hazelcastMapService.deleteMap();
        return null;
    }

/*
    @GetMapping("/client-reload")
    @Operation(summary = "Reload Client Instance", description = "Reloads the Hazelcast client instance")
    @ApiResponse(responseCode = "200", description = "Client instance reloaded successfully",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = Boolean.class)))
    public ResponseEntity<Boolean> reloadClientInstance(){
        // hazelcastClientInitializer.forceHazelcastClientCreation();
        return ResponseEntity.ok(true);
    }
    */

}
