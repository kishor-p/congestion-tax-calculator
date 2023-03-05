## Congestion-Tax-Calculator (JAVA, SpringBoot API)

- The purpose of this mocroservice API is to calculate  Congestion-Tax for a Vehicle on given dates.
- It's part of a Coding assignment. Built using `JAVA` `SPRING-BOOT`
- TAX Rules for this application can be configured via an external JSON file and could employ this solution for different cities and years.
- Currently the code is shipped with default configuration for Gothenburg City and Year 2013

### How to Run
This application can be run in 2 ways. 

#### DOCKER way:
1. Make sure you have docker installed in your system.
2. Navigate to the `congestion-tax-calculator/java/congestion-tax-calculator/` via terminal. (Where `Dockerfile` is located)
3. Run `docker build -t congestion-tax-calculator .` This will build the docker image.
4. Run `docker run -p 8080:8080 -t congestion-calculator`
5. At browser navigate to: http://localhost:8080/congestion-tax-calculator/api/
6. This will take you to the SWAGGER page with list of exposed HTTP REST API endpoints

#### Spring Boot Way:
1. Make sure you have Java 11 installed in your system.
2. Navigate to the `congestion-tax-calculator/java/congestion-tax-calculator/` via terminal (where `mvnw` is located)
3. Run `./mvnw spring-boot:run`
4.  At browser navigate to: http://localhost:8080/congestion-tax-calculator/api/
5. This will take you to the SWAGGER page with list of exposed HTTP REST API endpoints

### Code Modules:
Here the code is modularized in to separated packages. Here is the detailed explanation on each module.

##### Models:
- `package com.vcc.congestiontaxcalculator.model.config`
  - The `Tax Rules` can be configured for different cities  and years.
  - This package holds all the POJOs that represent the structure of Configuration
- `package com.vcc.congestiontaxcalculator.model.dto`
  - This package hold the DTO for payload for REST endpoints
- `package com.vcc.congestiontaxcalculator.model.error`
  - This package holds the POJOs related to Custom Exception and pre-defined Error Codes for the application
- `package com.vcc.congestiontaxcalculator.model.vehicles`
  - This package holds the POJOs representing different Vehicle Types.

##### Services:
- `package com.vcc.congestiontaxcalculator.service`
  - This package holds all the services available in the application.
  - `CongestionConfigService` is the SINGLETON service that initiates and holds the Tax Rules and Year configuration for application.
  - `CongestionTaxCalculatorService` the actual service that calculate the tax for each request.

##### HTTP REST API and Config
- `package com.vcc.congestiontaxcalculator.controller`
  - This package holds all the available HTTP REST endpoints for the application.
- `import org.springframework.web.context.request.WebRequest`
  - This package holds the Controller Advise to handle Exceptions thrown by Service Layer and Send user in Problam Details Format

### Unit Tests
- JUnit and Mockito are used to setup Unit Tests for the `CongestionTaxCalculatorService`
- Tests are located at `package com.vcc.congestiontaxcalculator.service`
- Here test-cases/test-scenarios are designed to be loaded from external **FIXTURES** via JSON file.
- Test Case Fixtures are loaded from: `congestion-tax-calculator/java/congestion-tax-calculator/src/test/resources/get-tax-fixture-1.json`
- The Junit Test case runs for each TestCase scenarios listed in: **get-tax-fixture-1.json**


### External Configuration (Bonus Scenario):
- The TAX Rules and RED days that dictates the Congestion Tax calculation are loaded externally in to application.
- These rulea re placed in a JSON file at: `congestion-tax-calculator/java/congestion-tax-calculator/src/main/resources/`
- File name is configured in `application.properties` file
- Current loaded configuration are in file: **congestion-config.json**
- This file supports loading Tax Rules for different **CITIES** and red days from different **YEAR** as well
