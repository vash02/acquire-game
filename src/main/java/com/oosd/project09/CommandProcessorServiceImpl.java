package com.oosd.project09;

import GameObjects.State;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oosd.project09.utils.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
@ComponentScan
public class CommandProcessorServiceImpl extends CommandProcessorService {

    @Autowired
    public CommandProcessorServiceImpl(Gson gson) {
        super(gson);
    }


    @Override
    public String executeSetup(String jsonCommand) {
        JsonObject command = JsonParser.parseString(jsonCommand).getAsJsonObject();

        if(!new RequestValidator().isValidSetup(command))
            return errorMessage("Invalid Request!");
        try {
            return CommandProcessor.executeSetup(command);
        }
        catch(IllegalStateException e) {
            return errorMessage(e.getMessage());
        }
    }

    @Override
    public String executePlace(String jsonCommand) {
        JsonObject command = JsonParser.parseString(jsonCommand).getAsJsonObject();
        if(!new RequestValidator().isValidPlace(command))
            return errorMessage("Invalid Request!");
        try {
            return CommandProcessor.executePlace(command);
        }
        catch(IllegalStateException e) {
            return errorMessage(e.getMessage());
        }
    }

    @Override
    public String executeBuy(String jsonCommand) {
        JsonObject command = JsonParser.parseString(jsonCommand).getAsJsonObject();
        if(!new RequestValidator().isValidBuy(command))
            return errorMessage("Invalid Request!");
        try {
            return CommandProcessor.executeBuy(command);
        }
        catch(IllegalStateException | IllegalArgumentException e) {
            return errorMessage(e.getMessage());
        }
    }

    @Override
    public String executeDone(String jsonCommand) {
        JsonObject command = JsonParser.parseString(jsonCommand).getAsJsonObject();
        if(!new RequestValidator().isValidDone(command))
            return errorMessage("Invalid Request!");
        try {
            return CommandProcessor.executeDone(command);
        }
        catch(IllegalStateException e) {
            return errorMessage(e.getMessage());
        }
    }

    @Override
    public String executeStart(String jsonCommand) {
        JsonObject command = JsonParser.parseString(jsonCommand).getAsJsonObject();
        if(!new RequestValidator().isValidStart(command))
            return errorMessage("Invalid Request!");
        try {
            return CommandProcessor.executeStart(command);
        }
        catch(IllegalStateException e) {
            return errorMessage(e.getMessage());
        }
    }

    @Override
    public String executeTurn(String jsonCommand){
        JsonObject command = JsonParser.parseString(jsonCommand).getAsJsonObject();
        if(!RequestValidator.isValidTurn(command))
            return errorMessage("Invalid Request!");
        try {
            return CommandProcessor.executeTurn(command);
        }
        catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    @Override
    public State executeStrategyGameTreeMove(String jsonCommand){
        JsonObject command = JsonParser.parseString(jsonCommand).getAsJsonObject();
        return CommandProcessor.executeStrategyGameTreeMove(command);
    }

    public String errorMessage(String message) {
        JsonObject result = new JsonObject();
        result.addProperty("error", message);
        return gson.toJson(result);
    }
}
