package com.oosd.project09.UnitTests;

import GameObjects.State;
import com.google.gson.JsonObject;
import com.oosd.project09.CommandProcessor;
import com.oosd.project09.CommandProcessorService;
import com.oosd.project09.CommandProcessorServiceImpl;
import com.oosd.project09.GameTreeImpl.GameTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static com.oosd.project09.CommandProcessorService.gson;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

public class GameTreeTest {

//    @InjectMocks
//    private CommandProcessorServiceImpl commandProcessorService;

    private CommandProcessorService commandProcessorService;

    private GameTree gameTree;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        commandProcessorService = new CommandProcessorServiceImpl(gson);
    }

    @Test
    void testExecuteStrategyGameTreeMove() {
        // Create a sample command JsonObject for testing

//        String jsonString ="{\"state\":{\"board\":{\"tiles\":[{\"row\":\"B\",\"column\":\"3\"}],\"hotels\":[{\"hotel\":\"American\",\"tiles\":[{\"row\":\"B\",\"column\":\"3\"}]}]},\"players\":[{\"player\":\"Player1\",\"cash\":6000,\"shares\":[{\"share\":\"Festival\",\"count\":2}],\"tiles\":[{\"row\":\"H\",\"column\":\"11\"}]}]}}";

        String jsonString = "{\"state\": {\"board\": {\"tiles\": [{\"row\": \"B\", \"column\": \"1\"},{\"row\": \"B\", \"column\": \"2\"},{\"row\": \"A\", \"column\": \"3\"},{\"row\": \"B\", \"column\": \"4\"},{\"row\": \"B\", \"column\": \"5\"},{\"row\": \"B\", \"column\": \"6\"},{\"row\": \"C\", \"column\": \"3\"},{\"row\": \"D\", \"column\": \"3\"},{\"row\": \"E\", \"column\": \"3\"}],\"hotels\": [{\"hotel\": \"American\",\"tiles\": [{\"row\": \"B\", \"column\": \"4\"},{\"row\": \"B\", \"column\": \"5\"},{\"row\": \"B\", \"column\": \"6\"}]},{\"hotel\": \"Continental\",\"tiles\": [{\"row\": \"C\", \"column\": \"3\"},{\"row\": \"D\", \"column\": \"3\"},{\"row\": \"E\", \"column\": \"3\"}]},{\"hotel\": \"Festival\",\"tiles\": [{\"row\": \"B\", \"column\": \"1\"},{\"row\": \"B\", \"column\": \"2\"}]}]},\"players\": [{\"player\": \"One\",\"cash\": 6000, \"strategy\": \"ordered\", \"shares\": [{\"share\": \"Continental\",\"count\": 10},{\"share\": \"Festival\",\"count\": 5}],\"tiles\": [{\"row\": \"B\", \"column\": \"3\"},{\"row\": \"H\", \"column\": \"11\"},{\"row\": \"H\", \"column\": \"12\"},{\"row\": \"I\", \"column\": \"10\"},{\"row\": \"I\", \"column\": \"11\"},{\"row\": \"I\", \"column\": \"12\"}]},{\"player\": \"Two\",\"cash\": 6000,\"shares\": [{\"share\": \"Continental\",\"count\": 5},{\"share\": \"Festival\",\"count\": 5}],\"tiles\": [{\"row\": \"F\", \"column\": \"10\"},{\"row\": \"F\", \"column\": \"11\"},{\"row\": \"F\", \"column\": \"12\"},{\"row\": \"G\", \"column\": \"10\"},{\"row\": \"G\", \"column\": \"11\"},{\"row\": \"G\", \"column\": \"12\"}]}]}}";


        JsonObject command = new JsonObject();
        // Call the method to be tested
        State resultState = commandProcessorService.executeStrategyGameTreeMove(jsonString);

        System.out.println("Result State "+resultState);

        // Assert the expected result based on the input
//        assertNotNull(resultState);
        // Add more assertions based on the expected behavior of the method
    }
}

