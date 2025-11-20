package io.github.devsha256.saprfctest.controller;

import io.github.devsha256.saprfctest.exception.SapRfcException;
import io.github.devsha256.saprfctest.model.RfcRequest;
import io.github.devsha256.saprfctest.model.RfcResponse;
import io.github.devsha256.saprfctest.service.GenericRfcService;
import io.github.devsha256.saprfctest.service.RfcMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic RFC Controller - handles any SAP function
 */
@RestController
@RequestMapping("/api/sap/rfc")
public class GenericRfcController {
    
    private static final Logger logger = LoggerFactory.getLogger(GenericRfcController.class);
    
    @Autowired
    private GenericRfcService rfcService;
    
    @Autowired
    private RfcMetadataService metadataService;
    
    /**
     * Execute any SAP RFC function
     */
    @PostMapping("/execute")
    public ResponseEntity<RfcResponse> executeFunction(@RequestBody RfcRequest request) {
        
        logger.info("Received RFC request for function: {}", request.getFunctionName());
        
        try {
            RfcResponse response = rfcService.executeFunction(request);
            return ResponseEntity.ok(response);
            
        } catch (SapRfcException e) {
            logger.error("RFC execution error", e);
            
            RfcResponse errorResponse = new RfcResponse();
            errorResponse.setSuccess(false);
            errorResponse.setFunctionName(request.getFunctionName());
            errorResponse.setError(e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
        }
    }
    
    /**
     * Get function metadata
     */
    @GetMapping("/metadata/{functionName}")
    public ResponseEntity<Map<String, Object>> getFunctionMetadata(
            @PathVariable String functionName) {
        
        try {
            Map<String, Object> metadata = metadataService.getFunctionMetadata(functionName);
            return ResponseEntity.ok(metadata);
            
        } catch (SapRfcException e) {
            logger.error("Error retrieving metadata", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Generic SAP RFC Executor");
        response.put("version", "2.0.0");
        
        return ResponseEntity.ok(response);
    }
}
