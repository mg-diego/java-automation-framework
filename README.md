# Java Automation Framework

Example of a JAVA Automation Framework that includes UI testing with Selenium WebDriver, API testing, and database interactions with MongoDB and PostgreSQL.

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)

## Getting Started

These instructions will help you set up and run the project on your local machine for development and testing purposes.

## Prerequisites

- Java 17 or higher
- Maven
- Docker (for Selenoid)
- MongoDB instance
- PostgreSQL instance

## Installation

1. **Clone the repository:**

    ```sh
    git clone https://github.com/mg-diego/java-automation-framework.git
    cd java-automation-project
    ```

2. **Install dependencies:**

    ```sh
    mvn clean install
    ```

3. **Set up Selenoid (Optional):**

    ```sh
    cd  java-automation-project\src\external-resources\selenoid
    .\start-selenoid-docker.ps1
    ```

## Running Tests

### Running UI Tests

To run the Selenium WebDriver tests:

```sh
mvn test -DsuiteXmlFile=TestNG.xml -Dstyle.color=always -Dcucumber.filter.tags="@WEB"
```

By the default the tests are executed using local webdrivers, in order to use Selenoid you need to change the webDriverType at ```src\configurations\Configuration.json```

```json
{
    ...
    "webDriverType": "selenoid",
    ...
}
```

### Running API Tests

To run the API tests:

```sh
mvn test -DsuiteXmlFile=TestNG.xml -Dstyle.color=always -Dcucumber.filter.tags="@API"
```

### Running Database Tests

To run the Database tests:

```sh
mvn test -DsuiteXmlFile=TestNG.xml -Dstyle.color=always -Dcucumber.filter.tags="@DB"
```

## Project Structure
```log
java-automation-project
│
├── src
│   ├── configurations
│   ├── external-resources
│   │   └── selenoid
│   ├── main
│   │   ├── java
│   │   │   ├── ApiResources
│   │   │   ├── Database
│   │   │   ├── DriverManager
│   │   │   ├── Enums
│   │   │   ├── Helpers
│   │   │   ├── Models
│   │   │   ├── PageObjectModel
│   │       └── TestContext
│   └── test
│       ├── java
│       │   └── StepDefinitions
│       │       ├── API
│       │       ├── Web
│       │       ├── Hooks
│       │       └── RunCucumberTest
│       └── resources
│           └── features
│               ├── API
│               ├── DB
│               └── Web
├── pom.xml
└── README.md
└── TestNG.xml
```


## Configuration
The ```src\configurations\Configuration.json``` file contains various settings required to run the tests, including database connections, web driver configurations, and API endpoints.

Example:
```json
{
  "postgresqlConnectionString": "jdbc:postgresql://localhost:5432/database",
  "postgresqlUser": "guest",
  "postgresqlPassword": "guest",
  "mongoDbConnectionString": "mongodb://guest:guest@localhost:27017/",
  "configurations" : [
    {
      "tag": "Web",
      "type": "Chrome",
      "webDriverType": "local",
      "baseUrl": "https://www.saucedemo.com/v1/",
      "selenoidUri": "http://localhost:4444/wd/hub",
      "deleteEvidencesForPassedTests": false,
      "capabilities" : []
    },
    {
      "tag": "API",
      "capabilities": [
        {
          "apiType": "Genderize",
          "baseUrl": "https://api.genderize.io/"
        }
      ]
    }
  ],
  "evidencesFolder": "C:\\temp\\evidences",
  "downloadDataPath": "C:\\temp\\downloads"
}
```


- **postgresqlConnectionString:** The connection string for PostgreSQL.
- **postgresqlUser:** The username for PostgreSQL.
- **postgresqlPassword:** The password for PostgreSQL.
- **mongoDbConnectionString:** The connection string for MongoDB.
- **configurations:** An array of configuration objects for different test types (Web, API).
  - **tag:** A tag to identify the configuration.
  - **type:** The type of browser (e.g., Chrome, Firefox).
  - **webDriverType:** The type of WebDriver (e.g., local, selenoid).
  - **baseUrl:** The base URL for the web application.
  - **selenoidUri:** The URI for Selenoid.
  - **deleteEvidencesForPassedTests:** A flag to delete evidences for passed tests.
  - **capabilities:** An array of capabilities for the configuration.
    - **apiType:** The type of API (e.g., Genderize).
    - **baseUrl:** The base URL for the API.
- **evidencesFolder:** The folder path to store test evidences.
- **downloadDataPath:** The folder path to store downloaded data.


## Contributing
1. Fork the repository.
2. Create your feature branch (```git checkout -b feature/your-feature```).
3. Commit your changes (```git commit -m 'Add some feature'```).
4. Push to the branch (```git push origin feature/your-feature```).
5. Open a pull request.

## License
### MIT License

Copyright (c) 2024 Diego Martínez Gilabert

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.