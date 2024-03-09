package com.oosd.project06.IntegrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import GameObjects.State;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oosd.project06.CommandProcessor;
import com.oosd.project06.CommandProcessorService;
import com.oosd.project06.CommandProcessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CommandProcessorServiceIntegrationTest {

    @Mock
    private Gson gson;

    private CommandProcessorService commandProcessorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        commandProcessorService = new CommandProcessorServiceImpl(gson);
    }

    @Test
    void testExecuteCommands() {
        // Given
        String jsonSetup = "{\"request\":\"setup\",\"players\":[\"Player1\",\"Player2\"]}";
        commandProcessorService.executeSetup(jsonSetup);

        // When
        String placeResult = commandProcessorService.executePlace(getSamplePlaceInputStateJson());
        JsonObject placeResultStateJson = new JsonObject();
        JsonObject placeResultJson = JsonParser.parseString(placeResult).getAsJsonObject();
        placeResultStateJson.add("state", placeResultJson);
        State stateAfterPlace = CommandProcessor.extractState(placeResultStateJson);
        JsonObject expectedPlaceOutputJson = JsonParser.parseString(getExpectedSamplePlaceOutputJson()).getAsJsonObject();
        State expectedStateAfterPlace = CommandProcessor.extractState(expectedPlaceOutputJson);

        // Then
        assertEquals(stateAfterPlace, expectedStateAfterPlace);


        // When
        String buyResult = commandProcessorService.executeBuy(getSampleBuyInputStateJson());
        JsonObject buyResultStateJson = new JsonObject();
        JsonObject buyResultJson = JsonParser.parseString(buyResult).getAsJsonObject();
        buyResultStateJson.add("state", buyResultJson);
        State stateAfterBuy = CommandProcessor.extractState(buyResultStateJson);
        JsonObject expectedBuyOutputJson = JsonParser.parseString(getExpectedSampleBuyOutputJson()).getAsJsonObject();
        State expectedStateAfterBuy = CommandProcessor.extractState(expectedBuyOutputJson);

        // Then
        assertEquals(stateAfterBuy, expectedStateAfterBuy);
    }

    public String getSamplePlaceInputStateJson() {
        String sampleStateJson =
                "{" +
                        "  \"state\": {" +
                        "    \"board\": {" +
                        "      \"tiles\": [{\"row\": \"B\", \"column\": \"2\"}]," +
                        "      \"hotels\": []" +
                        "    }," +
                        "    \"players\": [" +
                        "      {" +
                        "        \"player\": \"One\"," +
                        "        \"cash\": 6000," +
                        "        \"shares\": []," +
                        "        \"tiles\": [" +
                        "          {\"row\": \"B\", \"column\": \"3\"}," +
                        "          {\"row\": \"H\", \"column\": \"11\"}," +
                        "          {\"row\": \"H\", \"column\": \"12\"}," +
                        "          {\"row\": \"I\", \"column\": \"10\"}," +
                        "          {\"row\": \"I\", \"column\": \"11\"}," +
                        "          {\"row\": \"I\", \"column\": \"12\"}" +
                        "        ]" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  \"request\": \"place\"," +
                        "  \"row\": \"B\"," +
                        "  \"column\": \"3\"," +
                        "  \"hotel\": \"American\"" +
                        "}";

        return sampleStateJson;
    }

    public String getExpectedSamplePlaceOutputJson() {
        String samplePlaceOutputStateJson =
                "{" +
                        "    \"state\": {" +
                        "      \"board\": {" +
                        "        \"tiles\": [" +
                        "          {\"row\": \"B\", \"column\": \"2\"}," +
                        "          {\"row\": \"B\", \"column\": \"3\"}" +
                        "        ]," +
                        "        \"hotels\": [" +
                        "          {" +
                        "            \"hotel\": \"American\"," +
                        "            \"tiles\": [" +
                        "              {\"row\": \"B\", \"column\": \"2\"}," +
                        "              {\"row\": \"B\", \"column\": \"3\"}" +
                        "            ]" +
                        "          }" +
                        "        ]" +
                        "      }," +
                        "      \"players\": [" +
                        "        {" +
                        "          \"player\": \"One\"," +
                        "          \"cash\": 6000," +
                        "          \"shares\": [{ \"share\" : \"American\", \"count\" : 1 }]," +
                        "          \"tiles\": [" +
                        "            {\"row\": \"H\", \"column\": \"11\"}," +
                        "            {\"row\": \"H\", \"column\": \"12\"}," +
                        "            {\"row\": \"I\", \"column\": \"10\"}," +
                        "            {\"row\": \"I\", \"column\": \"11\"}," +
                        "            {\"row\": \"I\", \"column\": \"12\"}" +
                        "          ]" +
                        "        }" +
                        "      ]" +
                        "    }" +
                        "  }";

        return samplePlaceOutputStateJson;
    }

    public String getSampleBuyInputStateJson(){
        String sampleStateJson =
                "{" +
                        "  \"state\": {" +
                        "    \"board\": {" +
                        "      \"tiles\": [" +
                        "        {\"row\": \"B\", \"column\": \"2\"}," +
                        "        {\"row\": \"B\", \"column\": \"3\"}" +
                        "      ]," +
                        "      \"hotels\": [" +
                        "        {" +
                        "          \"hotel\": \"American\"," +
                        "          \"tiles\": [" +
                        "            {\"row\": \"B\", \"column\": \"2\"}," +
                        "            {\"row\": \"B\", \"column\": \"3\"}" +
                        "          ]" +
                        "        }" +
                        "      ]" +
                        "    }," +
                        "    \"players\": [" +
                        "      {" +
                        "        \"player\": \"One\"," +
                        "        \"cash\": 6000," +
                        "        \"shares\": []," +
                        "        \"tiles\": [" +
                        "          {\"row\": \"A\", \"column\": \"1\"}," +
                        "          {\"row\": \"H\", \"column\": \"11\"}," +
                        "          {\"row\": \"H\", \"column\": \"12\"}," +
                        "          {\"row\": \"I\", \"column\": \"10\"}," +
                        "          {\"row\": \"I\", \"column\": \"11\"}," +
                        "          {\"row\": \"I\", \"column\": \"12\"}" +
                        "        ]" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  \"request\": \"buy\"," +
                        "  \"shares\": [\"American\",\"American\"]" +
                        "}";

        return sampleStateJson;
    }

    public String getExpectedSampleBuyOutputJson(){
        String sampleBuyOutputStateJson =
                "{" +
                        "  \"state\": {" +
                        "    \"board\": {" +
                        "      \"tiles\": [" +
                        "        {\"row\": \"B\", \"column\": \"2\"}," +
                        "        {\"row\": \"B\", \"column\": \"3\"}" +
                        "      ]," +
                        "      \"hotels\": [" +
                        "        {" +
                        "          \"hotel\": \"American\"," +
                        "          \"tiles\": [" +
                        "            {\"row\": \"B\", \"column\": \"2\"}," +
                        "            {\"row\": \"B\", \"column\": \"3\"}" +
                        "          ]" +
                        "        }" +
                        "      ]" +
                        "    }," +
                        "    \"players\": [" +
                        "      {" +
                        "        \"player\": \"One\"," +
                        "        \"cash\": 5400," +
                        "        \"shares\": [" +
                        "          {\"share\": \"American\", \"count\": 2}" +
                        "        ]," +
                        "        \"tiles\": [" +
                        "          {\"row\": \"A\", \"column\": \"1\"}," +
                        "          {\"row\": \"H\", \"column\": \"11\"}," +
                        "          {\"row\": \"H\", \"column\": \"12\"}," +
                        "          {\"row\": \"I\", \"column\": \"10\"}," +
                        "          {\"row\": \"I\", \"column\": \"11\"}," +
                        "          {\"row\": \"I\", \"column\": \"12\"}" +
                        "        ]" +
                        "      }" +
                        "    ]" +
                        "  }" +
                        "}";
        return sampleBuyOutputStateJson;
    }
}
