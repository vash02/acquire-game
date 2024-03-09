package com.oosd.project06;

import GameObjects.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oosd.project06.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandProcessor {

    private static Gson gson = new Gson();

    public static void main(String[] args) {
        // Sample JSON commands
        String jsonSetup = "{\"request\":\"setup\",\"players\":[\"Player1\",\"Player2\"]}";
        String jsonPlace = "{\"request\":\"place\",\"row\":\"A\",\"column\":\"1\",\"state\":{}}";
        String jsonBuy = "{\"request\":\"buy\",\"shares\":[\"American\"],\"state\":{}}";
        String jsonDone = "{\"request\":\"done\",\"state\":{}}";

        // Process each command
        processCommand(jsonSetup);
        processCommand(jsonPlace);
        processCommand(jsonBuy);
        processCommand(jsonDone);
    }

    public static void processCommand(String jsonCommand) {
        // Parse JSON command
        JsonObject command = JsonParser.parseString(jsonCommand).getAsJsonObject();
        String request = command.get("request").getAsString();

        // Execute the appropriate method based on the request
        switch (request) {
            case "setup":
                executeSetup(command);
                break;
            case "place":
                executePlace(command);
                break;
            case "buy":
                executeBuy(command);
                break;
            case "done":
                executeDone(command);
                break;
            default:
                System.out.println("{\"error\":\"Invalid command\"}");
        }
    }


    public static int getRowNumber(String row) {
        return row.charAt(0) - 'A';
    }

    public static int getColumnNumber(String column) {
        return Integer.parseInt(column) - 1;
    }

    public static Board initializeBoard(JsonObject command) {
        JsonObject stateObject = command.getAsJsonObject("state");
        JsonObject boardObject = stateObject.getAsJsonObject("board");
        JsonArray tilesArray = boardObject.getAsJsonArray("tiles");
        Board board = new Board();

        // Process tiles
        for (int i = 0; i < tilesArray.size(); i++) {
            JsonObject tile = tilesArray.get(i).getAsJsonObject();
            int row = getRowNumber(tile.get("row").getAsString());
            int column = getColumnNumber(tile.get("column").getAsString());
            board.occupyTile(row, column);
        }

        // Process hotels
        JsonArray hotelsArray = boardObject.getAsJsonArray("hotels");
        for (int i = 0; i < hotelsArray.size(); i++) {
            JsonObject hotel = hotelsArray.get(i).getAsJsonObject();
            String hotelName = hotel.get("hotel").getAsString();
            int hotelLabel = board.getHotelLabel(hotelName);
            JsonArray tilesArrayForHotel = hotel.getAsJsonArray("tiles");
            for (int j = 0; j < tilesArrayForHotel.size(); j++) {
                JsonObject tile = tilesArrayForHotel.get(j).getAsJsonObject();
                int row = getRowNumber(tile.get("row").getAsString());
                int column = getColumnNumber(tile.get("column").getAsString());
                board.setHotel(row, column, hotelLabel);
            }
        }
        return board;
    }


    public static JsonObject convertToOutputFormat(Board board) {
        // Create a JsonObject to hold the output
        JsonObject output = new JsonObject();

        // Convert tiles
        JsonArray tilesArray = new JsonArray();
        for (char row = 'A'; row <= 'I'; row++) {
            for (int column = 1; column <= 12; column++) {
                JsonObject tile = new JsonObject();
                if (board.isTileOccupied(row - 65, column - 1)) {
                    tile.addProperty("row", String.valueOf(row));
                    tile.addProperty("column", String.valueOf(column));
                    tilesArray.add(tile);
                }
            }
        }
        output.add("tiles", tilesArray);

        // Convert hotels
        JsonArray hotelsArray = new JsonArray();
        for (int i = 1; i <= 7; i++) {
            JsonObject hotel = new JsonObject();
            hotel.addProperty("hotel", board.getHotelName(i));
            JsonArray hotelTilesArray = new JsonArray();
            for (char row = 'A'; row <= 'I'; row++) {
                for (int column = 1; column <= 12; column++) {
                    if (board.getHotels()[row - 'A'][column - 1] == i) {
                        JsonObject tile = new JsonObject();
                        if (board.isTileOccupied(row - 'A', column - 1)) {
                            tile.addProperty("row", String.valueOf(row));
                            tile.addProperty("column", String.valueOf(column));
                            hotelTilesArray.add(tile);
                        }
                    }
                }
            }
            if (!hotelTilesArray.isEmpty()) {
                hotel.add("tiles", hotelTilesArray);
                hotelsArray.add(hotel);
            }
        }
        output.add("hotels", hotelsArray);

        return output;
    }


    public static String executeSetup(JsonObject command) {
        List<Tile> availableTiles = getAllTiles();
        List<Player> players = new ArrayList<>();
        Board board = new Board();
        JsonArray playersArray = command.getAsJsonArray("players");
        for (int i = 0; i < playersArray.size(); i++) {
            String playerName = playersArray.get(i).getAsString();
            Player player = new Player(playerName);
            allocateInitialTilesToPlayer(player, availableTiles);
            player.setCash(6000);
            players.add(player);
        }

        State state = new State(board, players);
        return convertToStateOutput(state);
    }

    private static void allocateInitialTilesToPlayer(Player player, List<Tile> availableTiles) {
        Collections.shuffle(availableTiles); // Shuffle the tiles to randomize allocation
        for (int i = 0; i < 6; i++) {
            Tile tile = availableTiles.remove(0);
            player.addTile(tile);
        }
    }

    private static List<Tile> getAllTiles() {
        List<Tile> availableTiles = new ArrayList<>();
        for (int row = 0; row <= 8; row++) {
            for (int column = 0; column <= 11; column++) {
                availableTiles.add(new Tile(row, column));
            }
        }
        return availableTiles;
    }

    public static String executePlace(JsonObject command) {
        State state = extractState(command);
        int row = getRowNumber(command.get("row").getAsString());
        int column = getColumnNumber(command.get("column").getAsString());
        String hotelLabel = null;

        if (command.has("hotel")) {
            hotelLabel = command.get("hotel").getAsString();
        }

        Pair inspectionResult = GameOperations.inspect(state, row, column);
        String operation = inspectionResult.getFirst();
        switch (operation) {
            case "occupied":
                throw new IllegalStateException("Tile position is already occupied.");
            case "singleton":
                GameOperations.singleton(state, row, column);
                break;
            case "found":
                if(hotelLabel == null)
                    throw new IllegalStateException("Hotel Label is not provided!");
                GameOperations.founding(state, row, column, state.getBoard().getHotelLabel(hotelLabel));
                break;
            case "grow":
                GameOperations.growing(state, row, column);
                break;
            case "merge":
                GameOperations.merging(state, row, column,state.getBoard().getHotelLabel(hotelLabel), inspectionResult.getSecond());
                break;
            case "impossible":
                throw new IllegalStateException("Tile cannot be placed at the position.");
            default:
                throw new IllegalStateException("Invalid operation.");

        }
        return convertToStateOutput(state);
    }
    public static String executeBuy (JsonObject command){
        State state = extractState(command);
        JsonArray sharesArray = command.getAsJsonArray("shares");

        List<String> sharesList = new ArrayList<>();
        for (int i = 0; i < sharesArray.size(); i++) {
            sharesList.add(sharesArray.get(i).getAsString());
        }
        GameOperations.buyStocks(state, sharesList);
        return convertToStateOutput(state);
    }

    public static String executeDone (JsonObject command){
        State state = extractState(command);
        GameOperations.replaceTile(state);
        return convertToStateOutput(state);
    }

    public static State extractState(JsonObject jsonObject) {
        Board board = CommandProcessor.initializeBoard(jsonObject);

        JsonObject stateObject = jsonObject.getAsJsonObject("state");
        JsonArray playersArray = stateObject.getAsJsonArray("players");
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playersArray.size(); i++) {
            JsonObject playerObj = playersArray.get(i).getAsJsonObject();
            Player player = extractPlayer(playerObj);
            players.add(player);
        }

        return new State(board, players);
    }

    public static Player extractPlayer(JsonObject playerObj) {
        String playerName = playerObj.get("player").getAsString();
        double cash = playerObj.getAsJsonPrimitive("cash").getAsDouble();

        JsonArray sharesArray = playerObj.getAsJsonArray("shares");
        List<Share> shares = new ArrayList<>();
        for (int i = 0; i < sharesArray.size(); i++) {
            JsonObject shareObj = sharesArray.get(i).getAsJsonObject();
            Share share = new Share(shareObj.get("share").getAsString(), shareObj.get("count").getAsInt());
            shares.add(share);
        }

        JsonArray tilesArray = playerObj.getAsJsonArray("tiles");
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < tilesArray.size(); i++) {
            JsonObject tileObj = tilesArray.get(i).getAsJsonObject();
            Tile tile = new Tile(tileObj.get("row").getAsString().charAt(0) - 65, tileObj.get("column").getAsInt() - 1);
            tiles.add(tile);
        }

        return new Player(playerName, cash, shares, tiles);
    }

    public static String convertToStateOutput(State state) {
        JsonObject result = new JsonObject();
        result.add("board", CommandProcessor.convertToOutputFormat(state.getBoard()));
        result.add("players", convertPlayersToJsonArray(state.getPlayers()));
        return gson.toJson(result);
    }

    public static JsonArray convertPlayersToJsonArray (List<Player> players) {
        JsonArray jsonArray = new JsonArray();
        for (Player player : players) {
            JsonObject playerObject = new JsonObject();
            playerObject.addProperty("player", player.getPlayerName());
            playerObject.addProperty("cash", player.getCash());

            // Convert shares to JSON array
            JsonArray sharesArray = new JsonArray();
            for (Share share : player.getShares()) {
                JsonObject shareObject = new JsonObject();
                shareObject.addProperty("share", share.getLabel());
                shareObject.addProperty("count", share.getCount());
                sharesArray.add(shareObject);
            }
            playerObject.add("shares", sharesArray);

            // Convert tiles to JSON array
            JsonArray tilesArray = new JsonArray();
            for (Tile tile : player.getTiles()) {
                JsonObject tileObject = new JsonObject();
                tileObject.addProperty("row", (char)('A' + tile.getRow()));
                tileObject.addProperty("column", String.valueOf(1 + tile.getColumn()));
                tilesArray.add(tileObject);
            }
            playerObject.add("tiles", tilesArray);

            jsonArray.add(playerObject);
        }
        return jsonArray;
    }

}

