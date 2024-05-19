package com.oosd.project09.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;

import static java.lang.reflect.Array.set;

public class RequestValidator {

    public boolean isValidSetup(JsonObject setup) {
        if (!setup.has("request") || !setup.get("request").getAsString().equals("setup")) {
            return false;
        }
        JsonArray players = setup.getAsJsonArray("players");
        Set<String> playersSet = new HashSet<>();
        for (int i=0; i<players.size(); i++){

            // safeguards set in place to check the validity of setup request
            if ( players.get(i).isJsonObject()){
                JsonObject playerNameElement = players.get(i).getAsJsonObject();
                String name = playerNameElement.get("name").getAsString();
                if(name.length() > 20) return false;
                playersSet.add(name);
                continue;
            }
            if (players.get(i).getAsString().length() > 20)
                    return false;
            playersSet.add(players.get(i).getAsString());
        }
        return players != null && !players.isEmpty() && players.size() <= 6 && playersSet.size() == players.size();
    }

    public boolean isValidPlace(JsonObject place) {
        if (!place.has("request") || !place.get("request").getAsString().equals("place")) {
            return false;
        }
        if (!place.has("row") || !place.has("column") || !place.has("state")) {
            return false;
        }
        String row = place.get("row").getAsString();
        String column = place.get("column").getAsString();
        if (row.isEmpty() || column.isEmpty()) {
            return false;
        }
        if (place.has("hotel")) {
            String hotel = place.get("hotel").getAsString();
            return isValidLabel(hotel) && isValidState(place.getAsJsonObject("state"));
        }
        return isValidState(place.getAsJsonObject("state"));
    }

    public boolean isValidBuy(JsonObject buy) {
        if (!buy.has("request") || !buy.get("request").getAsString().equals("buy")) {
            return false;
        }
        JsonArray shares = buy.getAsJsonArray("shares");
        return shares != null && shares.size() >= 1 && shares.size() <= 3 && isValidState(buy.getAsJsonObject("state"));
    }

    public boolean isValidDone(JsonObject done) {
        if (!done.has("request") || !done.get("request").getAsString().equals("done")) {
            return false;
        }
        return isValidState(done.getAsJsonObject("state"));
    }

    private boolean isValidState(JsonObject state) {
        if (!state.has("board") || !state.has("players")) {
            return false;
        }
        if (!isValidBoard(state.getAsJsonObject("board"))) {
            return false;
        }
        JsonArray players = state.getAsJsonArray("players");
        if (players == null || players.size() == 0) {
            return false;
        }
        Set<String> playersSet = new HashSet<>();
        for (int i=0; i< players.size(); i++){
            playersSet.add(players.get(i).getAsJsonObject().get("player").getAsString());
        }
        if (!(playersSet.size() == players.size()))
            return false;
        for (JsonElement player : players) {
            if (!isValidPlayer(player.getAsJsonObject())) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidBoard(JsonObject board) {
        if (!board.has("tiles") || !board.has("hotels")) {
            return false;
        }
        JsonArray tiles = board.getAsJsonArray("tiles");
        JsonArray hotels = board.getAsJsonArray("hotels");
        return tiles != null && hotels != null;
    }

    private static boolean isValidPlayer(JsonObject player) {
        if (!player.has("player") || !player.has("cash") || !player.has("shares") || !player.has("tiles")) {
            return false;
        }
        String playerName = player.get("player").getAsString();
        double cash = player.get("cash").getAsDouble();
        JsonArray shares = player.getAsJsonArray("shares");
        JsonArray tiles = player.getAsJsonArray("tiles");
        if (playerName.length() >20 || playerName.isEmpty() || cash < 0 || shares == null || tiles == null) {
            return false;
        }
        return true;
    }

    private boolean isValidLabel(String label) {
        // Implement label validation logic if needed
        return true;
    }

    public boolean isValidStart(JsonObject start){
        if(!start.has("turns") || !start.get("turns").isJsonPrimitive()){
            return false;
        }
        try{
            int turns = start.get("turns").getAsInt();
            if(turns==0)return false;
            return true;

        }catch (ClassCastException e){
            return false;
        }
    }

    public static boolean isValidTurn(JsonObject command) {
        // Check if all required fields are present
        if (!command.has("player") || !command.has("board") || !command.has("tile") ||
                !command.has("share") || !command.has("xhotel")) {
            return false;
        }

        // Validate player object
        JsonObject playerObj = command.getAsJsonObject("player");
        if (!isValidPlayer(playerObj)) {
            return false;
        }

        // Validate board object
        JsonObject boardObj = command.getAsJsonObject("board");
        if (!isValidBoard(boardObj)) {
            return false;
        }

        // Validate tile array
        JsonArray tileArray = command.getAsJsonArray("tile");
        if (!isValidTileArray(tileArray)) {
            return false;
        }

        // Validate share array
        JsonArray shareArray = command.getAsJsonArray("share");
        if (!isValidShareArray(shareArray)) {
            return false;
        }

        // Validate xhotel array
        JsonArray xhotelArray = command.getAsJsonArray("xhotel");
        if (!isValidXHotelArray(xhotelArray)) {
            return false;
        }

        // All checks passed, return true
        return true;
    }

    private static boolean isValidTileArray(JsonArray tileArray) {
        // Check if tile array is not empty
        return tileArray != null && tileArray.size() > 0;
    }

    private static boolean isValidShareArray(JsonArray shareArray) {
        // Check if share array is not empty
        return shareArray != null && shareArray.size() > 0;
    }

    private static boolean isValidXHotelArray(JsonArray xhotelArray) {
        // Check if xhotel array is not empty
        return xhotelArray != null && xhotelArray.size() > 0;
    }
}

