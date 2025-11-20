package io.github.devsha256.saprfctest.service;

import com.sap.conn.jco.*;
import io.github.devsha256.saprfctest.exception.SapRfcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service to retrieve SAP RFC function metadata
 */
@Service
public class RfcMetadataService {
    
    private static final Logger logger = LoggerFactory.getLogger(RfcMetadataService.class);
    
    @Autowired
    private JCoDestination destination;
    
    /**
     * Get complete function metadata
     */
    public Map<String, Object> getFunctionMetadata(String functionName) {
        logger.info("Retrieving metadata for function: {}", functionName);
        
        try {
            JCoFunction function = destination.getRepository().getFunction(functionName);
            
            if (function == null) {
                throw new SapRfcException("Function " + functionName + " not found");
            }
            
            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("functionName", functionName);
            metadata.put("importParameters", getParameterMetadata(function.getImportParameterList()));
            metadata.put("exportParameters", getParameterMetadata(function.getExportParameterList()));
            metadata.put("tableParameters", getTableMetadata(function.getTableParameterList()));
            metadata.put("changingParameters", getParameterMetadata(function.getChangingParameterList()));
            metadata.put("xmlTemplate", function.toXML());
            
            return metadata;
            
        } catch (JCoException e) {
            logger.error("Error retrieving metadata", e);
            throw new SapRfcException("Error retrieving metadata: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get parameter metadata
     */
    private List<Map<String, Object>> getParameterMetadata(JCoParameterList paramList) {
        List<Map<String, Object>> params = new ArrayList<>();
        
        if (paramList == null) {
            return params;
        }
        
        JCoListMetaData metaData = paramList.getListMetaData();
        
        for (int i = 0; i < metaData.getFieldCount(); i++) {
            Map<String, Object> param = new LinkedHashMap<>();
            param.put("name", metaData.getName(i));
            param.put("type", metaData.getTypeAsString(i));
            param.put("length", metaData.getLength(i));
            param.put("decimals", metaData.getDecimals(i));
            param.put("description", metaData.getDescription(i));
            param.put("optional", metaData.isOptional(i));
            
            params.add(param);
        }
        
        return params;
    }
    
    /**
     * Get table metadata
     */
    private List<Map<String, Object>> getTableMetadata(JCoParameterList tableList) {
        List<Map<String, Object>> tables = new ArrayList<>();
        
        if (tableList == null) {
            return tables;
        }
        
        JCoListMetaData metaData = tableList.getListMetaData();
        
        for (int i = 0; i < metaData.getFieldCount(); i++) {
            Map<String, Object> table = new LinkedHashMap<>();
            table.put("name", metaData.getName(i));
            table.put("type", metaData.getTypeAsString(i));
            table.put("description", metaData.getDescription(i));
            
            tables.add(table);
        }
        
        return tables;
    }
}
