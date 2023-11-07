package com.thatninjaguyspeaks.hazelcast.controller;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastMapService;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastPipelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/com/thatninjaguyspeaks/hazelcast/pipeline")
@Tag(name = "Pipeline Controller", description = "Controller for Hazelcast pipeline operations")
public class PipelineController {

    @Autowired
    private HazelcastPipelineService hazelcastPipelineService;

    @GetMapping("/trigger")
    @Operation(summary = "Trigger pipeline", description = "Runs pipeline and inserts data to the Hazelcast map")
    @ApiResponse(responseCode = "200", description = "Data inserted successfully",
            content = @Content(mediaType = "text/plain"))
    public ResponseEntity<Object> triggerPipeline(){
        hazelcastPipelineService.triggerPipeline();
        return ResponseEntity.ok("SUCCESS");
    }

}
