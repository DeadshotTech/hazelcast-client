package com.thatninjaguyspeaks.hazelcast.controller;

import com.thatninjaguyspeaks.hazelcast.service.HazelcastPipelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/com/thatninjaguyspeaks/hazelcast/pipeline")
@Tag(name = "Pipeline Controller", description = "Controller for Hazelcast pipeline operations")
public class PipelineController {

    @Autowired
    private HazelcastPipelineService hazelcastPipelineService;

    @GetMapping("/trigger")
    @Operation(summary = "Trigger pipeline", description = "Runs pipeline and inserts data to the Hazelcast map")
    @ApiResponse(responseCode = "200", description = "Data inserted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Object.class)))
    public ResponseEntity<Object> triggerPipeline(){
        hazelcastPipelineService.triggerPipeline();
        return ResponseEntity.ok("SUCCESS");
    }

    @PostMapping(path = "/upload", consumes = "multipart/form-data", produces = "text/plain")
    @Operation(summary = "Trigger pipeline", description = "Runs pipeline and inserts data to the Hazelcast map")
    public ResponseEntity<Object> uploadCsvData(@RequestParam("file") MultipartFile file){
        try {
            hazelcastPipelineService.uploadCsvData(file.getInputStream());
        } catch (IOException e) {
            return ResponseEntity.ok("Failed to upload");
        }
        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping("/search/{key}")
    @Operation(summary = "Get Map Data", description = "Retrieves data from the Hazelcast map for the given key")
    @ApiResponse(responseCode = "200", description = "Data retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Object.class)))
    public Flux<String> search(@PathVariable String key){
        return hazelcastPipelineService.search(key);
    }

    @GetMapping("/api/load")
    @Operation(summary = "Get API Data", description = "Retrieves data from the API and returns")
    @ApiResponse(responseCode = "200", description = "Data retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Object.class)))
    public Flux<String> loadApiData(){
        return hazelcastPipelineService.loadApiData();
    }
}
