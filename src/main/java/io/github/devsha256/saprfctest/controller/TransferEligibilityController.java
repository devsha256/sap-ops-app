package io.github.devsha256.saprfctest.controller;

import io.github.devsha256.saprfctest.exception.SapRfcException;
import io.github.devsha256.saprfctest.model.TransferRequest;
import io.github.devsha256.saprfctest.model.TransferResponse;
import io.github.devsha256.saprfctest.service.TransferEligibilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for SAP Transfer Eligibility operations
 */
@RestController
@RequestMapping("/api/sap")
public class TransferEligibilityController {

    private static final Logger logger = LoggerFactory.getLogger(TransferEligibilityController.class);

    @Autowired
    private TransferEligibilityService service;

    @PostMapping("/transfer-eligibility")
    public ResponseEntity<TransferResponse> checkTransferEligibility(
            @RequestBody TransferRequest request) {

        logger.info("Received transfer eligibility request from API");

        try {
            TransferResponse response = service.checkTransferEligibility(request);
            return ResponseEntity.ok(response);

        } catch (SapRfcException e) {
            logger.error("SAP RFC Error", e);

            TransferResponse errorResponse = new TransferResponse();
            errorResponse.setSuccess(false);
            errorResponse.setRetCode("ERROR");
            errorResponse.setRetText(e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/metadata")
    public ResponseEntity<Map<String, String>> getFunctionMetadata() {
        try {
            service.logFunctionMetadata();

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Function metadata logged. Check application logs.");

            return ResponseEntity.ok(response);

        } catch (SapRfcException e) {
            logger.error("Error retrieving metadata", e);

            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "SAP RFC Test Application");
        response.put("version", "1.0.0");
        response.put("groupId", "io.github.devsha256");

        return ResponseEntity.ok(response);
    }
}
