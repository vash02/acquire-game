package com.project.acquire.UnitTests;

import com.google.gson.Gson;
import com.project.acquire.CommandProcessorServiceImpl;
import com.project.acquire.utils.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ExecutePlaceTest {

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
    public void testExecutePlace() {
        String jsonCommand = "{\"request\":\"place\",\"row\":\"B\",\"column\":\"3\",\"state\":{\"board\":{\"tiles\":[{\"row\":\"A\",\"column\":\"1\"},{\"row\":\"B\",\"column\":\"2\"},{\"row\":\"C\",\"column\":\"3\"}],\"hotels\":[{\"hotel\":\"American\",\"tiles\":[{\"row\":\"B\",\"column\":\"3\"}]}]}}}";

        when(gson.toJson(any())).thenReturn("{\"response\": \"success\"}");

        String result = commandProcessorService.executePlace(jsonCommand);

        assertEquals("{\"response\": \"success\"}", result);

        verify(gson).toJson(any());
    }
}

