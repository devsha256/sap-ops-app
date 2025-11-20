package io.github.devsha256.saprfctest.service;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import io.github.devsha256.saprfctest.exception.SapRfcException;
import io.github.devsha256.saprfctest.model.TransferRequest;
import io.github.devsha256.saprfctest.model.TransferResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to handle SAP RFC calls for transfer eligibility
 */
@Service
public class TransferEligibilityService {

    private static final Logger logger = LoggerFactory.getLogger(TransferEligibilityService.class);
    private static final String FUNCTION_NAME = "Z_WEB_TRANSFER_ELIGIBILITY";

    @Autowired
    private JCoDestination destination;

    /**
     * Check transfer eligibility by calling SAP RFC function
     */
    public TransferResponse checkTransferEligibility(TransferRequest request) {

        logger.info("========================================");
        logger.info("Calling SAP RFC: {}", FUNCTION_NAME);
        logger.info("========================================");
        logger.info("Input Parameters:");
        logger.info("  I_FROM_ESIID: '{}'", request.getFromEsiid());
        logger.info("  I_KYP: '{}'", request.getKyp());
        logger.info("  I_MVIDATE: '{}'", request.getMviDate());
        logger.info("  I_PARTNER: '{}'", request.getPartner());
        logger.info("  I_TO_ESIID: '{}'", request.getToEsiid());
        logger.info("  OLD_PRDUCT: '{}'", request.getOldProduct());
        logger.info("========================================");

        try {
            // Get function from repository
            JCoFunction function = destination.getRepository().getFunction(FUNCTION_NAME);

            if (function == null) {
                throw new SapRfcException("Function " + FUNCTION_NAME + " not found in SAP system");
            }

            // Set import parameters
            JCoParameterList importParams = function.getImportParameterList();
            importParams.setValue("I_FROM_ESIID", request.getFromEsiid() != null ? request.getFromEsiid() : "");
            importParams.setValue("I_KYP", request.getKyp() != null ? request.getKyp() : "");
            importParams.setValue("I_MVIDATE", request.getMviDate() != null ? request.getMviDate() : "");
            importParams.setValue("I_PARTNER", request.getPartner() != null ? request.getPartner() : "");
            importParams.setValue("I_TO_ESIID", request.getToEsiid() != null ? request.getToEsiid() : "");
            importParams.setValue("OLD_PRDUCT", request.getOldProduct() != null ? request.getOldProduct() : "");

            logger.debug("Executing RFC function...");
            long startTime = System.currentTimeMillis();

            // Execute function
            function.execute(destination);

            long duration = System.currentTimeMillis() - startTime;
            logger.debug("RFC execution completed in {} ms", duration);

            // Get export parameters
            JCoParameterList exportParams = function.getExportParameterList();
            String retCode = exportParams.getString("E_RETCODE");
            String retText = exportParams.getString("E_RETTEXT");

            logger.info("========================================");
            logger.info("SAP Response:");
            logger.info("  E_RETCODE: '{}'", retCode);
            logger.info("  E_RETTEXT: '{}'", retText);
            logger.info("  Execution Time: {} ms", duration);
            logger.info("========================================");

            // Create response
            TransferResponse response = new TransferResponse();
            response.setRetCode(retCode);
            response.setRetText(retText);
            response.setExecutionTimeMs(duration);
            response.setSuccess("00".equals(retCode));

            return response;

        } catch (JCoException e) {
            logger.error("SAP RFC Error: {}", e.getMessage(), e);
            throw new SapRfcException("Error calling SAP RFC: " + e.getMessage(), e);
        }
    }

    /**
     * Log function metadata for debugging
     */
    public void logFunctionMetadata() {
        logger.info("========================================");
        logger.info("Function Metadata: {}", FUNCTION_NAME);
        logger.info("========================================");

        try {
            JCoFunction function = destination.getRepository().getFunction(FUNCTION_NAME);

            if (function == null) {
                logger.error("Function {} not found", FUNCTION_NAME);
                return;
            }

            // Log import parameters
            logger.info("Import Parameters:");
            JCoParameterList importParams = function.getImportParameterList();
            for (int i = 0; i < importParams.getFieldCount(); i++) {
                logger.info("  [{}] {} - Type: {}, Length: {}, Description: {}",
                        i,
                        importParams.getName(i),
                        importParams.getTypeAsString(i),
                        importParams.getLength(i),
                        importParams.getDescription(i));
            }

            // Log export parameters
            logger.info("Export Parameters:");
            JCoParameterList exportParams = function.getExportParameterList();
            for (int i = 0; i < exportParams.getFieldCount(); i++) {
                logger.info("  [{}] {} - Type: {}, Length: {}, Description: {}",
                        i,
                        exportParams.getName(i),
                        exportParams.getTypeAsString(i),
                        exportParams.getLength(i),
                        exportParams.getDescription(i));
            }

            // Log function XML template
            logger.info("========================================");
            logger.info("Function XML Template:");
            logger.info("{}", function.toXML());
            logger.info("========================================");

        } catch (JCoException e) {
            logger.error("Error retrieving function metadata", e);
            throw new SapRfcException("Error retrieving metadata: " + e.getMessage(), e);
        }
    }
}
