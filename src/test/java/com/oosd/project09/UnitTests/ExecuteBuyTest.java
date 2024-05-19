package com.oosd.project09.UnitTests;

import com.google.gson.Gson;
import com.oosd.project09.CommandProcessorServiceImpl;
import com.oosd.project09.utils.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ExecuteBuyTest {

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
    public void testExecuteBuy() {
        // Define the input JSON command
        String jsonCommand = "{\"request\":\"buy\",\"shares\":[\"Festival\",\"Imperial\"],\"state\":{\"board\":{\"tiles\":[{\"row\":\"B\",\"column\":\"3\"}],\"hotels\":[{\"hotel\":\"American\",\"tiles\":[{\"row\":\"B\",\"column\":\"3\"}]}]},\"players\":[{\"player\":\"Player1\",\"cash\":6000,\"shares\":[{\"share\":\"Festival\",\"count\":2}],\"tiles\":[{\"row\":\"H\",\"column\":\"11\"}]}]}}";

        // Mock the response from Gson.toJson method
        when(gson.toJson(any())).thenReturn("{\"response\": \"success\"}");

        // Call the method under test and capture the result
        String result = commandProcessorService.executeBuy(jsonCommand);

        // Assert that the result is as expected
        assertNotNull(result);
    }
}
