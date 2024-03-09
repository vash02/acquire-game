package com.project.acquire.IntegrationTests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.project.acquire.CommandProcessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AdminTester {

    @InjectMocks
    private CommandProcessorServiceImpl commandProcessorService;

    @BeforeEach
    public void setUp() {
        // Instantiate the CommandProcessor class
        commandProcessorService = new CommandProcessorServiceImpl(new Gson());
    }

    @Test
    public void runTests() {
        Gson gson = new Gson();
        for (int i = 0; i < 3; i++) {
            String inputFileName = "src/test/java/com/oosd/project06/admin-tests/in" + i + ".json";
            String outputFileName = "src/test/java/com/oosd/project06/admin-tests/out" + i + ".json";
            try (FileReader inputFileReader = new FileReader(inputFileName);
                 FileReader outputFileReader = new FileReader(outputFileName)) {

                JsonObject inputJson = JsonParser.parseReader(inputFileReader).getAsJsonObject();
                JsonObject expectedOutputJson = JsonParser.parseReader(outputFileReader).getAsJsonObject();

                String requestType = inputJson.get("request").getAsString();
                String actualOutput;
                switch (requestType) {
                    case "setup":
                        actualOutput = commandProcessorService.executeSetup(inputJson.toString());
                        break;
                    case "place":
                        actualOutput = commandProcessorService.executePlace(inputJson.toString());
                        break;
                    case "buy":
                        actualOutput = commandProcessorService.executeBuy(inputJson.toString());
                        break;
                    case "done":
                        actualOutput = commandProcessorService.executeDone(inputJson.toString());
                        break;
                    default:
                        actualOutput = "Invalid request type";
                }

                assertEquals(expectedOutputJson, gson.fromJson(actualOutput, JsonObject.class));

            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
