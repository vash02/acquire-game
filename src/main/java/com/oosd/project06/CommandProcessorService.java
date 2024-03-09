package com.oosd.project06;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CommandProcessorService {

    public static Gson gson;

    @Autowired
    public CommandProcessorService(Gson gson) {
        CommandProcessorService.gson = gson;
    }
    public abstract String executeSetup(String jsonCommand);
    public abstract String executePlace(String jsonCommand);
    public abstract String executeBuy(String jsonCommand);
    public abstract String executeDone(String jsonCommand);
}

