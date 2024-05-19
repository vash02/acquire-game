package com.oosd.project09.IntegrationTests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oosd.project09.CommandProcessorServiceImpl;
import com.oosd.project09.GameFunctions.GameOperations;
import com.sun.jdi.request.InvalidRequestStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PlayerTester {

    @Mock
    Gson gson;
    @Mock
    private Random randomGenerator;

    @Mock
    private GameOperations gameOperations;

    @InjectMocks
    private CommandProcessorServiceImpl commandProcessorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExecuteTurn1() throws IOException, InvalidRequestStateException {

        // Arrange
        JsonObject inputJson = readJsonFile("player-tests/in0.json");
        JsonObject expectedOutputJson = readJsonFile("player-tests/out0.json");

//        when(gson.toJson(any())).thenReturn("{\"response\": \"success\"}");
        // Act
        String result = commandProcessorService.executeTurn(inputJson.toString());
        System.out.println(result);

        // Assert
        assertEquals(expectedOutputJson, JsonParser.parseString(result).getAsJsonObject());
    }

    @Test
    public void testExecuteTurn2() throws IOException, InvalidRequestStateException {
        // Arrange
        JsonObject inputJson = readJsonFile("player-tests/in1.json");
        JsonObject expectedOutputJson = readJsonFile("player-tests/out1.json");

        // Act
        String result = commandProcessorService.executeTurn(inputJson.toString());

        // Assert
        assertEquals(expectedOutputJson, JsonParser.parseString(result).getAsJsonObject());
    }

    @Test
    public void testExecuteTurn3() throws IOException, InvalidRequestStateException {
        // Arrange
        JsonObject inputJson = readJsonFile("player-tests/in2.json");
        JsonObject expectedOutputJson = readJsonFile("player-tests/out2.json");

        // Act
        String result = commandProcessorService.executeTurn(inputJson.toString());

        // Assert
        assertEquals(expectedOutputJson, JsonParser.parseString(result).getAsJsonObject());
    }




    // Helper method to read JSON files
    private JsonObject readJsonFile(String filename) throws IOException {
        String path = Paths.get("src", "test", "resources", filename).toString();
        try (FileReader reader = new FileReader(path)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

}
