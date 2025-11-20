package io.github.devsha256.saprfctest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Generic RFC Request model - supports any SAP function
 */
public class RfcRequest {
    
    @JsonProperty("functionName")
    private String functionName;
    
    @JsonProperty("importParameters")
    private Map<String, Object> importParameters;
    
    @JsonProperty("tables")
    private Map<String, List<Map<String, Object>>> tables;
    
    @JsonProperty("changingParameters")
    private Map<String, Object> changingParameters;
    
    @JsonProperty("commit")
    private boolean commit = false;
    
    @JsonProperty("stateful")
    private boolean stateful = false;
    
    // Getters and Setters
    public String getFunctionName() {
        return functionName;
    }
    
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    
    public Map<String, Object> getImportParameters() {
        return importParameters;
    }
    
    public void setImportParameters(Map<String, Object> importParameters) {
        this.importParameters = importParameters;
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
    
    public boolean isCommit() {
        return commit;
    }
    
    public void setCommit(boolean commit) {
        this.commit = commit;
    }
    
    public boolean isStateful() {
        return stateful;
    }
    
    public void setStateful(boolean stateful) {
        this.stateful = stateful;
    }
}
