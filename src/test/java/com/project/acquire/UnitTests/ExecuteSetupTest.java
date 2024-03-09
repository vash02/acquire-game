package com.project.acquire.UnitTests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.project.acquire.CommandProcessorServiceImpl;
import com.project.acquire.utils.RequestValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

public class ExecuteSetupTest {

    @Mock
    private Gson gson;

    @InjectMocks
    private RequestValidator requestValidator;

    @InjectMocks
    private CommandProcessorServiceImpl commandProcessorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteSetup() {
        // Define the input JSON command
        String jsonCommand = "{\"request\":\"setup\",\"players\":[\"Player1\",\"Player2\",\"Player3\"]}";

        // Mock the response from Gson.toJson method
        when(gson.toJson(any())).thenReturn("{\"response\": \"success\"}");

        // Call the method under test and capture the result
        String result = commandProcessorService.executeSetup(jsonCommand);

        // Assert that the result is not null
        Assertions.assertNotNull(result);
    }

    @Test
    public void testExecuteSetup_Invalid_Request() {
        // Define the input JSON command
        String jsonCommand1 = "{\"request\": \"setup\", \"players\": [\"Player2\", \"Player2\", \"Player3\"]}";
        String jsonCommand2 = "{\"request\": \"setup\", \"players\": [\"abcdefghijklmnopqrstuvwxyz\", \"Player2\", \"Player3\"]}";

        // Mock the response from requestValidator.isValidSetup
        JsonObject command1 = JsonParser.parseString(jsonCommand1).getAsJsonObject();
        Boolean res1 = requestValidator.isValidSetup(command1);
        assertEquals(false, res1);

        //Mock response for invalid player name character length
        JsonObject command2 = JsonParser.parseString(jsonCommand2).getAsJsonObject();
        Boolean res2 = requestValidator.isValidSetup(command2);
        assertEquals(false, res2);

        // Mock the response from Gson.toJson method
        when(gson.toJson(any())).thenReturn("{\"response\": \"success\"}");

        // Call the method under test and capture the result
        String result = commandProcessorService.executeSetup(jsonCommand1);

        // Assert that the result is not null
        Assertions.assertNotNull(result);
    }

}
