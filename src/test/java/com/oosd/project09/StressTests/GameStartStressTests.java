package com.oosd.project09.StressTests;

import com.google.gson.Gson;
import com.oosd.project09.CommandProcessorServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class GameStartStressTests {

    @Mock
    Gson gson;

    @InjectMocks
    CommandProcessorServiceImpl commandProcessorService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteStart(){

        // Mock the response from Gson.toJson method
        when(gson.toJson(any())).thenReturn("{\"response\": \"success\"}");

        // Executing a setup request
        String startCommand = "{\"request\":\"start\",\"turns\":-1,\"players\":[{\"name\":\"Player1\",\"strategy\":\"random\"},{\"name\":\"Player2\",\"strategy\":\"smallest-anti\"},{\"name\":\"Player3\",\"strategy\":\"largest-alpha\"},{\"name\":\"Player4\",\"strategy\":\"ordered\"}]}";

//        commandProcessorService.executeStart(startCommand);
        for(int i=0;i<100;i++) {
            String result = commandProcessorService.executeStart(startCommand);
            System.out.println("winner "+ result);

            Assertions.assertNotNull(result);
        }
    }
}
