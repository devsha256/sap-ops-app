sap-rfc-test/
├── src/main/java/io/github/devsha256/saprfctest/
│   ├── SapRfcTestApplication.java
│   ├── config/
│   │   └── SapJCoConfig.java
│   ├── model/
│   │   ├── RfcRequest.java              [NEW - Generic]
│   │   ├── RfcResponse.java             [NEW - Generic]
│   │   ├── RfcParameter.java            [NEW]
│   │   ├── RfcTable.java                [NEW]
│   │   └── RfcTableRow.java             [NEW]
│   ├── service/
│   │   ├── GenericRfcService.java       [NEW - Main service]
│   │   └── RfcMetadataService.java      [NEW - Metadata]
│   ├── controller/
│   │   ├── GenericRfcController.java    [NEW]
│   │   └── RfcMetadataController.java   [NEW]
│   └── exception/
│       └── SapRfcException.java
