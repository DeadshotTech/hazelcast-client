package com.thatninjaguyspeaks.hazelcast.controller;

import com.thatninjaguyspeaks.hazelcast.dto.SqlRequest;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastSqlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/com/thatninjaguyspeaks/hazelcast/map")
@Tag(name = "SQL Controller", description = "Controller for executing SQL operations on Hazelcast maps")
public class SqlController {

    private static final Logger logger = LogManager.getLogger(SqlController.class);

    @Autowired
    private HazelcastSqlService hazelcastSqlService;

    @PostMapping("/sql")
    @Operation(summary = "Execute SQL Query", description = "Executes a SQL query on a Hazelcast map")
    @ApiResponse(responseCode = "200", description = "Query executed successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Object.class)))
    public ResponseEntity<Object> executeSql(@RequestBody SqlRequest sqlRequest) {
        return ResponseEntity.ok(hazelcastSqlService.executeSqlQuery(sqlRequest));
    }

    @PostMapping("/sql-map")
    @Operation(summary = "Create SQL Mapping", description = "Creates a SQL mapping for a Hazelcast map")
    @ApiResponse(responseCode = "200", description = "Mapping created successfully",
            content = @Content(mediaType = "text/plain"))
    public ResponseEntity<String> createSqlMapping(@RequestBody SqlRequest sqlRequest) {
        hazelcastSqlService.createSqlMapping(sqlRequest);
        return ResponseEntity.ok("SUCCESS");
    }
}
