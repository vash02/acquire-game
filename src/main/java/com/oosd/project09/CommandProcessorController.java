package com.oosd.project09;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommandProcessorController {

    @Autowired
    private CommandProcessorService commandProcessorService;

    @PostMapping("/setup")
    public String setup(@RequestBody String jsonCommand) {
        return commandProcessorService.executeSetup(jsonCommand);
    }

    @PostMapping("/place")
    public String place(@RequestBody String jsonCommand) {
        return commandProcessorService.executePlace(jsonCommand);
    }

    @PostMapping("/buy")
    public String buy(@RequestBody String jsonCommand) {
        return commandProcessorService.executeBuy(jsonCommand);
    }

    @PostMapping("/done")
    public String done(@RequestBody String jsonCommand) {
        return commandProcessorService.executeDone(jsonCommand);
    }


    @PostMapping("/start")
    public String start(@RequestBody String jsonCommand){
        return commandProcessorService.executeStart(jsonCommand);
    }

    @PostMapping("/turn")
    public String turn(@RequestBody String jsonCommand){
        return commandProcessorService.executeTurn(jsonCommand);
    }
}
