# FinQA Automation Framework

A comprehensive test automation framework for banking applications with web UI and API testing capabilities.

## Architecture Overview

This framework implements a hybrid architecture combining the best practices from:
- **Page Object Model (POM)**: For clear separation of test logic from UI elements 
- **Behavior-Driven Development (BDD)**: For business-readable specifications
- **Data-Driven Testing**: For extensive test coverage with minimal code duplication

## Key Features

- **Cross-Browser Testing**: Chrome, Firefox, Edge, and Safari support
- **REST API Testing**: Complete API test coverage with RestAssured
- **Visual Validation**: Pixel-by-pixel comparison of UI elements
- **Video Recording**: Automatic test execution recording for debugging
- **Parallel Execution**: Optimized test suite runtime with parallel execution
- **Comprehensive Reporting**: Allure and ExtentReports integration
- **Continuous Integration**: Jenkins pipeline integration

## Technology Stack

- Java 11
- Selenium WebDriver 4
- Cucumber 7
- TestNG
- RestAssured
- Allure Reporting
- ExtentReports
- WebDriverManager
- AShot & Image Comparison
- Monte Screen Recorder
- Log4j2

## Project Structure

```
src
├── main/java/com/finqa
│   ├── api
│   │   └── endpoints        # API endpoints and requests
│   ├── bdd
│   │   └── context         # Test context and dependency injection
│   ├── config              # Configuration management
│   ├── reporting           # Reporting utilities
│   │   └── visual          # Visual validation tools
│   └── ui
│       └── pages           # Page objects
│
├── test/java/com/finqa
│   ├── api
│   │   └── tests           # API tests
│   └── bdd
│       ├── runners         # Cucumber runners
│       └── steps           # Step definitions
│
└── test/resources
    ├── config              # Configuration files
    ├── features            # Cucumber feature files
    ├── test-data           # Test data
    └── visual-baseline     # Visual testing baseline images
```

## Getting Started

### Prerequisites

- Java JDK 11 or higher
- Maven 3.8.x or higher
- Chrome, Firefox, or Edge browser

### Installation

1. Clone the repository
```bash
git clone https://github.com/yourusername/finqa-automation-framework.git
```

2. Install dependencies
```bash
mvn clean install
```

### Running Tests

Run all tests:
```bash
mvn clean test
```

Run specific feature:
```bash
mvn clean test -Dcucumber.filter.tags="@login"
```

Run API tests only:
```bash
mvn clean test -Dtest=AccountAPITest
```

### Generating Reports

Generate Allure report:
```bash
mvn allure:serve
```

## CI/CD Integration

This framework includes a Jenkins pipeline configuration (`Jenkinsfile`) that:
1. Builds the project
2. Runs tests in parallel
3. Generates reports
4. Sends email notifications

## Best Practices

- **Test Independence**: All tests are designed to run independently
- **Fail-Fast Approach**: Tests fail immediately when preconditions aren't met
- **Explicit Waits**: Smart waiting strategies to avoid flaky tests
- **Screenshot Capture**: Automatic screenshot capture on test failure
- **Logging**: Comprehensive logging with Log4j2
- **Configuration Management**: Externalized configuration for different environments

## Future Enhancements

- [ ] AWS Device Farm integration for mobile testing
- [ ] Performance testing integration with JMeter
- [ ] Security testing integration with OWASP ZAP
- [ ] AI-powered test analytics dashboard
