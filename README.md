sap-rfc-test/
├── pom.xml
├── README.md
├── .gitignore
├── lib/
│   ├── sapjco3.jar
│   └── sapjco3.dll (or .so for Linux)
└── src/
    └── main/
        ├── java/
        │   └── io/
        │       └── github/
        │           └── devsha256/
        │               └── saprfctest/
        │                   ├── SapRfcTestApplication.java
        │                   ├── config/
        │                   │   └── SapJCoConfig.java
        │                   ├── service/
        │                   │   └── TransferEligibilityService.java
        │                   ├── controller/
        │                   │   └── TransferEligibilityController.java
        │                   ├── model/
        │                   │   ├── TransferRequest.java
        │                   │   └── TransferResponse.java
        │                   └── exception/
        │                       └── SapRfcException.java
        └── resources/
            ├── application.properties
            └── application-prod.properties
