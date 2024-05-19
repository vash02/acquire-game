package com.oosd.project09;

import GameObjects.*;
import com.google.gson.*;
import com.oosd.project09.GameFunctions.GameOperations;
import GameObjects.PlayerStrategy;
import com.oosd.project09.GameTreeImpl.GameTree;
import com.oosd.project09.GameTreeImpl.GameTreeInterface;
import com.oosd.project09.GameTreeImpl.PlayerStrategyUsingGameTree;
import com.oosd.project09.utils.GameTreeFilteringCriteria;
import com.oosd.project09.utils.GameTreeNode;
import com.oosd.project09.utils.Pair;
import com.oosd.project09.utils.StrategyMapper;
import com.sun.jdi.request.InvalidRequestStateException;

import java.util.*;

import static com.oosd.project09.GameFunctions.GameOperations.*;

public class CommandProcessor {

    private static Gson gson = new Gson();

    private static State state;

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
            case "start":
                executeStart(command);
            case "turn":
                executeTurn(command);
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
            int hotelLabel = board.getHotelIndex(hotelName);
            JsonArray tilesArrayForHotel = hotel.getAsJsonArray("tiles");
            for (int j = 0; j < tilesArrayForHotel.size(); j++) {
                JsonObject tile = tilesArrayForHotel.get(j).getAsJsonObject();
                int row = getRowNumber(tile.get("row").getAsString());
                int column = getColumnNumber(tile.get("column").getAsString());
                board.setHotel(row, column, hotelLabel);
            }
        }
        board.printBoard();
        return board;
    }

    public static void initializeGameState(JsonObject jsonCommand) {
        List<Tile> availableTiles = getAllTiles();
        List<Player> players = new ArrayList<>();
        Board board = new Board();

        if (jsonCommand.has("players")) {
            JsonArray playersArray = jsonCommand.getAsJsonArray("players");
            for (int i = 0; i < playersArray.size(); i++) {
                JsonObject playerObj = playersArray.get(i).getAsJsonObject();
                String name = playerObj.get("name").getAsString();
                String strategy = playerObj.has("strategy") ? playerObj.get("strategy").getAsString() : null;
                Player player = new Player(name);
                if (strategy != null) {
                    StrategyMapper strategyMapper = new StrategyMapper();
                    // Get the strategy object based on the strategy name
                    PlayerStrategy playerStrategy = strategyMapper.getStrategy(strategy);
                    System.out.println("strategy object " + playerStrategy);
                    if (playerStrategy == null) {
                        throw new IllegalArgumentException("Invalid strategy name: " + strategy);
                    }
                    player = new Player(name, playerStrategy);
                }
                allocateInitialTilesToPlayer(player, availableTiles);
                player.setCash(6000);
                players.add(player);
            }
        } else {
            throw new IllegalArgumentException("Invalid command: 'players' tag is missing.");
        }

        state = new State(board, players);
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
        initializeGameState(command);
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
                GameOperations.founding(state, row, column, state.getBoard().getHotelIndex(hotelLabel));
                break;
            case "grow":
                GameOperations.growing(state, row, column);
                break;
            case "merge":
                GameOperations.merging(state, row, column, inspectionResult.getSecond(), state.getBoard().getHotelIndex(hotelLabel));
                break;
            case "impossible":
                throw new IllegalStateException("Tile cannot be placed at the position.");
            default:
                throw new IllegalStateException("Invalid operation.");

        }
        return convertToStateOutput(state);
    }
    public static String executeTurn(JsonObject command) throws InvalidRequestStateException {
        Random randomGenerator  = new Random();
        // Extract information from the turn request
        JsonObject stateObj = new JsonObject();
        JsonObject boardObj = new JsonObject();

        JsonObject playerObj = command.getAsJsonObject("player");
        Player player = extractPlayer(playerObj, 0);
        List<Player> players = new ArrayList<>();
        players.add(player);

        boardObj.add("board",command.get("board"));
        stateObj.add("state",boardObj);

        Board board = initializeBoard(stateObj.getAsJsonObject());
        State turnTemporaryState = new State(board, players);

        GameTree tree = new GameTree();
        GameTreeNode node = tree.generateChildNodes(turnTemporaryState);
        JsonArray availableTilesArray = command.getAsJsonArray("tile");
        JsonArray availableSharesArray = command.getAsJsonArray("share");
        JsonArray availableHotelsArray = command.getAsJsonArray("xhotel");

        JsonObject placeObject = new JsonObject();
        // Check if the player can place a tile
        List<Tile> sortedTiles = new ArrayList<>(player.getTiles());
        sortedTiles.sort(Comparator.comparing(Tile::getRow).thenComparingInt(Tile::getColumn));

        // Choose the smallest tile
        Tile moveTile = sortedTiles.get(0);

        placeObject.addProperty("row", moveTile.getRow());
        placeObject.addProperty("column", moveTile.getColumn());
        List<List<Object>> availableSharesList = new ArrayList<>();

        JsonObject actionObject = new JsonObject();

        if (!availableSharesArray.isEmpty()) {
            for (JsonElement share : availableSharesArray) {
                JsonObject shareObject = share.getAsJsonObject();
                String shareLabel = shareObject.get("share").getAsString();
                int count = shareObject.get("count").getAsInt();

                List<Object> availableSharesInfo = new ArrayList<>();
                availableSharesInfo.add(shareLabel);
                availableSharesInfo.add(count);

                availableSharesList.add(availableSharesInfo);
            }
        }

        List<String> availableHotelsList= new ArrayList<>();
        if(!availableHotelsArray.isEmpty()){
            for (JsonElement hotel: availableHotelsArray){
                JsonObject hotelObj = hotel.getAsJsonObject();
                String hotelLabel = hotelObj.get("hotel").getAsString();

                availableHotelsList.add(hotelLabel);

            }
        }

        Pair inspectionResult = GameOperations.inspect(turnTemporaryState, moveTile.getRow(), moveTile.getColumn());
        String operation = inspectionResult.getFirst();

        switch (operation) {
            case "occupied":
                throw new IllegalStateException("Tile position is already occupied.");
            case "singleton":
                GameOperations.singleton(turnTemporaryState, moveTile.getRow(), moveTile.getColumn());
                break;
            case "found":
                int randomIndex = randomGenerator.nextInt(availableHotelsList.size());
                String selectedHotel = availableHotelsList.get(randomIndex);
                GameOperations.founding(turnTemporaryState, moveTile.getRow(), moveTile.getColumn(), turnTemporaryState.getBoard().getHotelIndex(selectedHotel));
                placeObject.addProperty("hotel",selectedHotel);
                break;
            case "grow":
                ArrayList<Tile> neighbors = GameOperations.occupiedNeighbors(board, moveTile.getRow(), moveTile.getColumn());
                int label = 0;
                for (Tile neighbor : neighbors) {
                    label = board.getHotel(neighbor.getRow(), neighbor.getColumn());
                    if (label != 0) {
                        break;
                    }
                }
                String growingHotel = board.getHotelName(label);
                placeObject.addProperty("hotel", growingHotel);
                GameOperations.growing(turnTemporaryState, moveTile.getRow(), moveTile.getColumn());
                break;
            case "merge":
                GameOperations.merging(turnTemporaryState, moveTile.getRow(), moveTile.getColumn(), inspectionResult.getSecond(), null);
                placeObject.addProperty("hotel",inspectionResult.getSecond().get(0));
                break;
            case "impossible":
                break;
            default:
                throw new IllegalStateException("Invalid operation.");

        }

        List<String> shareToBuy;
        shareToBuy = GameOperations.selectMaxOrderedShares(board, player, availableSharesList);

        GameOperations.buyStocks(turnTemporaryState, shareToBuy);

        checkNewStateWithTreeNodeStates(node, turnTemporaryState,moveTile,shareToBuy);
        if(isGameOver(turnTemporaryState)){
            actionObject.addProperty("win", "true");
        }else{
            actionObject.addProperty("win", "false");
        }

        JsonArray hotelSharesArray = new JsonArray();
        for (String share : shareToBuy) {
            hotelSharesArray.add(share);
        }
        actionObject.add("hotel", hotelSharesArray);
        if (!operation.equals("impossible")){
            actionObject.add("place",placeObject);
        }
        System.out.println("actionObject "+actionObject);
        return actionObject.toString();

    }

    public static void checkNewStateWithTreeNodeStates(GameTreeNode treeNode,State state, Tile tile, List<String> shares) throws RuntimeException{
        PlayerStrategyUsingGameTree strategyFilterCheck = new PlayerStrategyUsingGameTree();
        GameTreeNode foundedPossibility = strategyFilterCheck.executePlayerMoveHelper(treeNode,
                state.getPlayers().get(state.getCurrentPlayerIndex()).getStrategy(),
                state,
                new GameTreeFilteringCriteria(tile,-1,shares));
        if(foundedPossibility==null){
            throw new RuntimeException("Illegal Player Move.");
        }
    }
    private static void runGame(int turns, List<Player> players) throws RuntimeException{
        int currentTurn = 0;
        GameTreeNode treeNode;
        GameTree tree = new GameTree();
        while (!isGameOver(state) && (turns < 0 || currentTurn < turns)) {
            Player currentPlayer = players.get(state.getCurrentPlayerIndex());
            // Run the strategy for the current player
            System.out.println("player strategy:----------"+currentPlayer.getStrategy());
            treeNode = tree.generateChildNodes(state);
            List<Object> result = currentPlayer.getStrategy().playTurn(state);
            if(result.size()==1){
                result.add(new ArrayList<String>());
            }
            checkNewStateWithTreeNodeStates(treeNode, state,(Tile)result.get(0),(List<String>) result.get(1));
            if (GameOperations.getAvailableTiles(state).isEmpty()) {
                break;
            }
            currentTurn++;
            state.setCurrentPlayerIndex((state.getCurrentPlayerIndex() + 1) % state.getPlayers().size());

            if (currentTurn > 1000) {
                break; // Safety break
            }
            System.out.println("Round " + currentTurn);
        }
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

    public static String executeStart(JsonObject command) throws RuntimeException{
        initializeGameState(command);
        int turns = command.get("turns").getAsInt();

        runGame(turns, state.getPlayers());

        performStockBuyback(state);
        Player winner = determineWinner(state.getPlayers());
        GameOperations.updateGameResultFile(winner);

        System.out.println("BOARD at the END");
        state.getBoard().printBoard();
        System.out.println("Winner Player Info");
        winner.printPlayerInfo();

        return winner.getPlayerName();
    }

    public static State executeStrategyGameTreeMove(JsonObject command){
        State initialState = extractState(command);
        GameTree gameTree = new GameTree();
        GameTreeInterface gameTreeInterface = (GameTreeInterface) gameTree;
        GameTreeNode gameTreeRootNode = gameTreeInterface.generateChildNodes(initialState);
        PlayerStrategyUsingGameTree playerStrategyUsingGameTree = new PlayerStrategyUsingGameTree();
//        GameTree.printLeafNodes(gameTreeRootNode);
        GameTreeNode selectedTreeNode = playerStrategyUsingGameTree.executePlayerMove(gameTreeRootNode, initialState);
        State resultState = selectedTreeNode.getValue();
        resultState.getBoard().printBoard();
        resultState.getPlayers().get(0).printPlayerInfo();
        return resultState;

    }


    public static State extractState(JsonObject jsonObject) {
        Board board = CommandProcessor.initializeBoard(jsonObject);
        JsonObject stateObject = jsonObject.getAsJsonObject("state");
        JsonArray playersArray = stateObject.getAsJsonArray("players");
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playersArray.size(); i++) {
            JsonObject playerObj = playersArray.get(i).getAsJsonObject();
            int playerIndex = i;
            Player player = extractPlayer(playerObj, playerIndex);
            players.add(player);
        }

        return new State(board, players);
    }

    public static Player extractPlayer(JsonObject playerObj, int playerIndex) {
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

        String strategy = playerObj.has("strategy") ? playerObj.get("strategy").getAsString() : null;
        PlayerStrategy playerStrategy = null;
        if (strategy != null) {
            StrategyMapper strategyMapper = new StrategyMapper();
            // Get the strategy object based on the strategy name
            playerStrategy = strategyMapper.getStrategy(strategy);
            return new Player(playerIndex, playerName, cash, shares, tiles, playerStrategy);
        }
        // Create a new player instance
        return new Player(playerIndex, playerName, cash, shares, tiles);
    }

    public static String convertToStateOutput(State state) {
        JsonObject result = new JsonObject();
        result.add("board", CommandProcessor.convertToOutputFormat(state.getBoard()));
        result.add("players", convertPlayersToJsonArray(state.getPlayers()));
        return gson.toJson(result);
    }

    public static JsonArray convertPlayersToJsonArray(List<Player> players) {
        JsonArray jsonArray = new JsonArray();
        for (Player player : players) {
            JsonObject playerObject = new JsonObject();
            playerObject.addProperty("player", player.getPlayerName());
            playerObject.addProperty("cash", player.getCash());

            if (player.getStrategy() != null) {
                playerObject.addProperty("strategy", StrategyMapper.getStrategyName(player.getStrategy()));
            }

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
                tileObject.addProperty("row", (char) ('A' + tile.getRow()));
                tileObject.addProperty("column", String.valueOf(1 + tile.getColumn()));
                tilesArray.add(tileObject);
            }
            playerObject.add("tiles", tilesArray);

            jsonArray.add(playerObject);
        }
        return jsonArray;
    }


}

