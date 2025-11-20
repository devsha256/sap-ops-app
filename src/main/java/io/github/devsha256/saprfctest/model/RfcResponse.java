package io.github.devsha256.saprfctest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Generic RFC Response model
 */
public class RfcResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("functionName")
    private String functionName;
    
    @JsonProperty("exportParameters")
    private Map<String, Object> exportParameters;
    
    @JsonProperty("tables")
    private Map<String, List<Map<String, Object>>> tables;
    
    @JsonProperty("changingParameters")
    private Map<String, Object> changingParameters;
    
    @JsonProperty("executionTimeMs")
    private long executionTimeMs;
    
    @JsonProperty("error")
    private String error;
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    
    public Map<String, Object> getExportParameters() {
        return exportParameters;
    }
    
    public void setExportParameters(Map<String, Object> exportParameters) {
        this.exportParameters = exportParameters;
    }
    
    public Map<String, List<Map<String, Object>>> getTables() {
        return tables;
    }
    
    public void setTables(Map<String, List<Map<String, Object>>> tables) {
        this.tables = tables;
    }
    
    public Map<String, Object> getChangingParameters() {
        return changingParameters;
    }
    
    public void setChangingParameters(Map<String, Object> changingParameters) {
        this.changingParameters = changingParameters;
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
