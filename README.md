# Acquire Game

## Description
This project implements test harness, unit tests and Integration tests for executing Acquire game command operations.

## Running Application

### Using command line
1. Open a terminal or command prompt.
2. Navigate to the project directory
3. Run the following command to execute the build:
    ```bash
   ./gradlew build
    ```

### Starting the application

1. The build will generate application jar.
2. Either use IDE to run the Springboot application using plugins
OR
run the command in terminal
```bash
java -jar {jar_name}.jar
```
3. Once the application is up the commands can be tried as requests using curl command in terminal or using postman.
4. Given that you use local host server , the service endpoints will be accessible at `http://localhost:{port}/{}`, where `{}` can be replaced with the desired endpoint (e.g., `setup`, `place`, `buy`, `done`).
5. Example requests can be found under directory `src/test/java/com/project/acquire/admin-tests/`.
