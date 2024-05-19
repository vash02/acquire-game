package com.oosd.project09.UnitTests;

import com.google.gson.Gson;
import com.oosd.project09.CommandProcessorServiceImpl;
import com.oosd.project09.GameFunctions.GameOperations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ExecuteStartTest {

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
        String startCommand = "{\"request\":\"start\",\"turns\":-1,\"players\":[{\"name\":\"Player1\",\"strategy\":\"random\"},{\"name\":\"Player2\",\"strategy\":\"smallest-anti\"}]}";

//        commandProcessorService.executeStart(startCommand);
        String result = commandProcessorService.executeStart(startCommand);

        System.out.println("winner "+ result);

        Assertions.assertNotNull(result);

    }

    @Test
    public void testGameResultsFile(){
        when(gson.toJson(any())).thenReturn("{\"response\": \"success\"}");

        String startCommand = "{\"request\":\"start\",\"turns\":-1,\"players\":[{\"name\":\"Player1\",\"strategy\":\"random\"},{\"name\":\"Player2\",\"strategy\":\"ordered\"}]}";


        String result = commandProcessorService.executeStart(startCommand);

        File currentDirectory = new File(System.getProperty("user.dir"));
        File outputDirectory = GameOperations.createOutputDirectory(currentDirectory);
        assertNotNull(outputDirectory);

        File outputFile = outputDirectory.toPath().resolve("game-results.txt").toFile();
        assertTrue(outputFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(outputFile));
        ){
            String line = reader.readLine();
            String[] elements = line.replace("\s","").split("=");
            assertEquals(elements.length, 2);
            reader.skip(1);
            line = reader.readLine();
            while ((line=reader.readLine())!=null){
                String[] rowElements = line.replace("-", "").replace("|","").split("\s+");
                if(rowElements.length==1 && rowElements[0].equals("")){
                    continue;
                }
                assertEquals(rowElements.length, 5);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
