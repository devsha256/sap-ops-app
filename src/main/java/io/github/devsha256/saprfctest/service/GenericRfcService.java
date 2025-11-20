package io.github.devsha256.saprfctest.service;

import com.sap.conn.jco.*;
import io.github.devsha256.saprfctest.exception.SapRfcException;
import io.github.devsha256.saprfctest.model.RfcRequest;
import io.github.devsha256.saprfctest.model.RfcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Generic SAP RFC Service - supports any function dynamically
 */
@Service
public class GenericRfcService {
    
    private static final Logger logger = LoggerFactory.getLogger(GenericRfcService.class);
    
    @Autowired
    private JCoDestination destination;
    
    /**
     * Execute any SAP RFC function dynamically
     */
    public RfcResponse executeFunction(RfcRequest request) {
        
        String functionName = request.getFunctionName();
        logger.info("========================================");
        logger.info("Executing SAP RFC: {}", functionName);
        logger.info("========================================");
        
        RfcResponse response = new RfcResponse();
        response.setFunctionName(functionName);
        
        try {
            long startTime = System.currentTimeMillis();
            
            // Begin stateful context if requested
            if (request.isStateful()) {
                JCoContext.begin(destination);
                logger.debug("Started stateful context");
            }
            
            // Get function from repository
            JCoFunction function = destination.getRepository().getFunction(functionName);
            
            if (function == null) {
                throw new SapRfcException("Function " + functionName + " not found in SAP system");
            }
            
            // Set import parameters
            setImportParameters(function, request.getImportParameters());
            
            // Set table parameters
            setTableParameters(function, request.getTables());
            
            // Set changing parameters
            setChangingParameters(function, request.getChangingParameters());
            
            // Execute function
            logger.debug("Executing RFC function...");
            function.execute(destination);
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Extract export parameters
            Map<String, Object> exportParams = extractExportParameters(function);
            
            // Extract table parameters
            Map<String, List<Map<String, Object>>> tables = extractTableParameters(function);
            
            // Extract changing parameters
            Map<String, Object> changingParams = extractChangingParameters(function);
            
            // Commit if requested
            if (request.isCommit()) {
                commitTransaction();
                logger.debug("Transaction committed");
            }
            
            // End stateful context
            if (request.isStateful()) {
                JCoContext.end(destination);
                logger.debug("Ended stateful context");
            }
            
            // Build response
            response.setSuccess(true);
            response.setExportParameters(exportParams);
            response.setTables(tables);
            response.setChangingParameters(changingParams);
            response.setExecutionTimeMs(duration);
            
            logger.info("RFC execution completed successfully in {} ms", duration);
            logger.info("========================================");
            
        } catch (JCoException e) {
            logger.error("SAP RFC Error: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setError(e.getMessage());
            
            // End context on error
            if (request.isStateful() && JCoContext.isStateful(destination)) {
                JCoContext.end(destination);
            }
            
            throw new SapRfcException("Error executing RFC: " + e.getMessage(), e);
        }
        
        return response;
    }
    
    /**
     * Set import parameters dynamically
     */
    private void setImportParameters(JCoFunction function, Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return;
        }
        
        JCoParameterList importParams = function.getImportParameterList();
        if (importParams == null) {
            return;
        }
        
        logger.debug("Setting import parameters:");
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();
            
            try {
                if (paramValue == null) {
                    importParams.setValue(paramName, "");
                } else {
                    importParams.setValue(paramName, paramValue.toString());
                }
                logger.debug("  {} = '{}'", paramName, paramValue);
            } catch (Exception e) {
                logger.warn("Could not set parameter {}: {}", paramName, e.getMessage());
            }
        }
    }
    
    /**
     * Set table parameters dynamically
     */
    private void setTableParameters(JCoFunction function, Map<String, List<Map<String, Object>>> tables) {
        if (tables == null || tables.isEmpty()) {
            return;
        }
        
        JCoParameterList tableParams = function.getTableParameterList();
        if (tableParams == null) {
            return;
        }
        
        logger.debug("Setting table parameters:");
        for (Map.Entry<String, List<Map<String, Object>>> entry : tables.entrySet()) {
            String tableName = entry.getKey();
            List<Map<String, Object>> rows = entry.getValue();
            
            try {
                JCoTable table = tableParams.getTable(tableName);
                
                for (Map<String, Object> row : rows) {
                    table.appendRow();
                    for (Map.Entry<String, Object> field : row.entrySet()) {
                        table.setValue(field.getKey(), field.getValue());
                    }
                }
                
                logger.debug("  {} - {} rows", tableName, rows.size());
            } catch (Exception e) {
                logger.warn("Could not set table {}: {}", tableName, e.getMessage());
            }
        }
    }
    
    /**
     * Set changing parameters dynamically
     */
    private void setChangingParameters(JCoFunction function, Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return;
        }
        
        JCoParameterList changingParams = function.getChangingParameterList();
        if (changingParams == null) {
            return;
        }
        
        logger.debug("Setting changing parameters:");
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();
            
            try {
                changingParams.setValue(paramName, paramValue != null ? paramValue.toString() : "");
                logger.debug("  {} = '{}'", paramName, paramValue);
            } catch (Exception e) {
                logger.warn("Could not set changing parameter {}: {}", paramName, e.getMessage());
            }
        }
    }
    
    /**
     * Extract export parameters
     */
    private Map<String, Object> extractExportParameters(JCoFunction function) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        JCoParameterList exportParams = function.getExportParameterList();
        if (exportParams == null) {
            return result;
        }
        
        JCoParameterFieldIterator it = exportParams.getParameterFieldIterator();
        while (it.hasNextField()) {
            JCoParameterField field = it.nextParameterField();
            result.put(field.getName(), field.getValue());
        }
        
        return result;
    }
    
    /**
     * Extract table parameters
     */
    private Map<String, List<Map<String, Object>>> extractTableParameters(JCoFunction function) {
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        
        JCoParameterList tableParams = function.getTableParameterList();
        if (tableParams == null) {
            return result;
        }
        
        JCoParameterFieldIterator it = tableParams.getParameterFieldIterator();
        while (it.hasNextField()) {
            JCoParameterField field = it.nextParameterField();
            
            if (field.isTable()) {
                JCoTable table = field.getTable();
                List<Map<String, Object>> rows = new ArrayList<>();
                
                for (int i = 0; i < table.getNumRows(); i++) {
                    table.setRow(i);
                    Map<String, Object> row = new LinkedHashMap<>();
                    
                    for (JCoFieldIterator fieldIt = table.getFieldIterator(); fieldIt.hasNextField(); ) {
                        JCoField rowField = fieldIt.nextField();
                        row.put(rowField.getName(), rowField.getValue());
                    }
                    
                    rows.add(row);
                }
                
                result.put(field.getName(), rows);
            }
        }
        
        return result;
    }
    
    /**
     * Extract changing parameters
     */
    private Map<String, Object> extractChangingParameters(JCoFunction function) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        JCoParameterList changingParams = function.getChangingParameterList();
        if (changingParams == null) {
            return result;
        }
        
        JCoParameterFieldIterator it = changingParams.getParameterFieldIterator();
        while (it.hasNextField()) {
            JCoParameterField field = it.nextParameterField();
            result.put(field.getName(), field.getValue());
        }
        
        return result;
    }
    
    /**
     * Commit transaction
     */
    private void commitTransaction() throws JCoException {
        JCoFunction commitFunction = destination.getRepository().getFunction("BAPI_TRANSACTION_COMMIT");
        
        if (commitFunction != null) {
            commitFunction.getImportParameterList().setValue("WAIT", "X");
            commitFunction.execute(destination);
        }
    }
}
