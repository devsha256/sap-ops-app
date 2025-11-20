package io.github.devsha256.saprfctest.config;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * SAP JCo Configuration
 * Configures the connection to SAP system using JCo library
 */
@Configuration
public class SapJCoConfig {

    private static final Logger logger = LoggerFactory.getLogger(SapJCoConfig.class);

    @Value("${sap.client}")
    private String client;

    @Value("${sap.user}")
    private String user;

    @Value("${sap.password}")
    private String password;

    @Value("${sap.language}")
    private String language;

    @Value("${sap.host}")
    private String host;

    @Value("${sap.sysnr}")
    private String sysnr;

    @Value("${sap.destination:SAP_SYSTEM}")
    private String destinationName;

    @Bean
    public JCoDestination jCoDestination() throws JCoException {
        logger.info("=================================================");
        logger.info("Initializing SAP JCo Destination: {}", destinationName);
        logger.info("=================================================");

        // Create destination configuration
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, host);
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, sysnr);
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, client);
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, user);
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, password);
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, language);
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "10");

        // Create destination file
        createDestinationDataFile(destinationName, connectProperties);

        // Get destination
        JCoDestination destination = JCoDestinationManager.getDestination(destinationName);

        logger.info("SAP JCo Destination initialized successfully");
        logger.info("  Host: {}", host);
        logger.info("  System Number: {}", sysnr);
        logger.info("  Client: {}", client);
        logger.info("  User: {}", user);
        logger.info("  Language: {}", language);

        // Test connection
        destination.ping();
        logger.info("SAP connection test: SUCCESS ✓");
        logger.info("=================================================");

        return destination;
    }

    private void createDestinationDataFile(String destinationName, Properties properties) {
        try {
            File destCfg = new File(destinationName + ".jcoDestination");
            try (FileOutputStream fos = new FileOutputStream(destCfg, false)) {
                properties.store(fos, "SAP JCo Destination Configuration");
            }
            logger.debug("Destination file created: {}", destCfg.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Error creating destination file", e);
            throw new RuntimeException("Unable to create SAP destination file", e);
        }
    }
}