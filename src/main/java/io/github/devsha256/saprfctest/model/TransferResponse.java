package io.github.devsha256.saprfctest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response model for transfer eligibility check
 */
public class TransferResponse {

    @JsonProperty("retCode")
    private String retCode;

    @JsonProperty("retText")
    private String retText;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("executionTimeMs")
    private long executionTimeMs;

    // Getters and Setters
    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetText() {
        return retText;
    }

    public void setRetText(String retText) {
        this.retText = retText;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
}
