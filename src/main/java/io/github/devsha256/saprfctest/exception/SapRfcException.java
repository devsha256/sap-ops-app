package io.github.devsha256.saprfctest.exception;

/**
 * Custom exception for SAP RFC operations
 */
public class SapRfcException extends RuntimeException {

    public SapRfcException(String message) {
        super(message);
    }

    public SapRfcException(String message, Throwable cause) {
        super(message, cause);
    }
}
