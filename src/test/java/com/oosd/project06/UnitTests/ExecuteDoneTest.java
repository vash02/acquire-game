package com.oosd.project06.UnitTests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oosd.project06.CommandProcessorServiceImpl;
import com.oosd.project06.utils.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ExecuteDoneTest {

    @Mock
    private Gson gson;

    @Mock
    private RequestValidator requestValidator;

    @InjectMocks
    private CommandProcessorServiceImpl commandProcessorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteDone() {
        // Define the input JSON command
        String jsonCommand = "{\"request\":\"done\",\"state\":{\"board\":{\"tiles\":[{\"row\":\"B\",\"column\":\"3\"}],\"hotels\":[{\"hotel\":\"American\",\"tiles\":[{\"row\":\"B\",\"column\":\"3\"}]}]},\"players\":[{\"player\":\"Player2\",\"cash\":6000.0,\"shares\":[{\"share\":\"Imperial\",\"count\":1}],\"tiles\":[{\"row\":\"C\",\"column\":\"4\"}]}]},\"players\":[{\"player\":\"Player2\",\"cash\":6000,\"shares\":[{\"share\":\"Imperial\",\"count\":1}],\"tiles\":[{\"row\":\"F\",\"column\":\"10\"}]}]}";

        // Call the method under test and capture the result
        String result = commandProcessorService.executeDone(jsonCommand);

        // Assert that the result is not null
        assertNotNull(result);
    }

}
