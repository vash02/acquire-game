package com.oosd.project09.GameTreeImpl;

import GameObjects.*;
import com.oosd.project09.GameFunctions.GameOperations;
import com.oosd.project09.utils.GameTreeNode;
import com.oosd.project09.utils.Pair;

import java.util.*;

public class GameTree implements GameTreeInterface{

    public GameTreeNode generateChildNodes(State state) {
        List<GameTreeNode> childNodes = new ArrayList<>();
        State stateCopy = new State(state.getBoard().getBoardCopy(), state.getPlayersDeepCopy());
        GameTreeNode rootNode = new GameTreeNode(state);
        Player currentPlayer = stateCopy.getPlayers().get(stateCopy.getCurrentPlayerIndex());
        List<Tile> playerTiles = new ArrayList<>(currentPlayer.getTiles());

        // For each possible action (found, grow, merge, singleton)
        for (Tile tileToPlay : playerTiles) {
            // Generate child nodes based on the action
            GameTreeNode playTileNode = new GameTreeNode(GameTreeHelper.generateWhatIfState(stateCopy, tileToPlay));
            List<GameTreeNode> actionNodes = generateChildNodesForAction(stateCopy, tileToPlay);
            playTileNode.setChildren(actionNodes);
            childNodes.add(playTileNode);
        }

        rootNode.setChildren(childNodes);
        return rootNode;
    }

    public List<GameTreeNode> generateChildNodesForAction(State state, Tile tile) {
        List<GameTreeNode> actionNodes = new ArrayList<>();
        // Inspect the tile and get the action result
        Pair inspectionResult = GameOperations.inspect(state, tile.getRow(), tile.getColumn());
        String actionType = inspectionResult.getFirst();
        Object actionValue = inspectionResult.getSecond();

        // Generate child nodes based on the action type
        switch (actionType) {
            case "found":
                // Generate child nodes for found action
                List<Hotel> possibleFoundHotels = state.getBoard().getAvailableHotels();
                for (Hotel hotel : possibleFoundHotels) {
                    // Create a node for the hotel involved
                    State newState = new State(state.getBoard().getBoardCopy(), state.getPlayersDeepCopy());
                    newState.setCurrentPlayerIndex(state.getCurrentPlayerIndex());
                    State foundState = simulateFoundAction(newState, tile, newState.getBoard().getHotelIndex(hotel.getLabel()));

                    GameTreeNode hotelNode = new GameTreeNode(foundState);

                    // Generate stock buying combinations
                    Set<List<String>> stockBuyingCombinations = generateStockBuyingCombinations(foundState);

                    // Create child nodes for each stock buying combination
                    for (List<String> combination : stockBuyingCombinations) {

                        // Create a node for the final state after buying stocks
                        State finalState = new State(foundState.getBoard().getBoardCopy(), state.getPlayersDeepCopy());
                        finalState.setCurrentPlayerIndex(state.getCurrentPlayerIndex());
                        try{
                            GameOperations.buyStocks(finalState, combination);
                        }catch (IllegalArgumentException e){
                            System.out.println(e.getMessage());
                        }

                        GameTreeNode combinationNode = new GameTreeNode(finalState);

                        // Add the combination node as a child of the hotel node
                        hotelNode.addChild(combinationNode);

                        List<GameTreeNode> tileHandoverNodes = simulateTileAllocationAndPlayerChange(combinationNode);
                        combinationNode.setChildren(tileHandoverNodes);
                    }

                    actionNodes.add(hotelNode);
                }
                break;
            case "grow":
                // Generate child nodes for grow action
                Integer growHotelIndex =  ((List<Integer>)actionValue).get(0);
                if (growHotelIndex != null) {
                    // Create a node for the hotel involved

                    State newState = new State(state.getBoard().getBoardCopy(), state.getPlayersDeepCopy());
                    newState.setCurrentPlayerIndex(state.getCurrentPlayerIndex());
                    State growState = simulateGrowAction(newState, tile);

                    GameTreeNode hotelNode = new GameTreeNode(growState);

                    // Generate stock buying combinations
                    Set<List<String>> stockBuyingCombinations = generateStockBuyingCombinations(growState);

                    // Create child nodes for each stock buying combination
                    for (List<String> combination : stockBuyingCombinations) {

                        // Create a node for the final state after buying stocks
                        State finalState = new State(growState.getBoard().getBoardCopy(), growState.getPlayersDeepCopy());
                        finalState.setCurrentPlayerIndex(state.getCurrentPlayerIndex());
                        try{
                            GameOperations.buyStocks(finalState, combination);
                        }catch (IllegalArgumentException e){
                            System.out.println(e.getMessage());
                        }

                        GameTreeNode combinationNode = new GameTreeNode(finalState);

                        // Add the combination node as a child of the hotel node
                        hotelNode.addChild(combinationNode);

                        List<GameTreeNode> tileHandoverNodes = simulateTileAllocationAndPlayerChange(combinationNode);
                        combinationNode.setChildren(tileHandoverNodes);
                    }

                    // Add the hotel node as a child of the action node
                    actionNodes.add(hotelNode);
                }
                break;
            case "merge":
                // Generate child nodes for merge action
                List<Integer> mergeHotels = (List<Integer>) actionValue;

                State newState = new State(state.getBoard().getBoardCopy(), state.getPlayersDeepCopy());
                newState.setCurrentPlayerIndex(state.getCurrentPlayerIndex());
                Hotel hotel = newState.getBoard().getHotelsInfo()[mergeHotels.get(0)];
                State mergeState = simulateMergeAction(newState, tile, inspectionResult.getSecond(), hotel);

                GameTreeNode hotelNode = new GameTreeNode(mergeState);

                // Generate stock buying combinations
                Set<List<String>> stockBuyingCombinations = generateStockBuyingCombinations(mergeState);

                // Create child nodes for each stock buying combination
                for (List<String> combination : stockBuyingCombinations) {

                    // Create a node for the final state after buying stocks
                    State finalState = new State(mergeState.getBoard().getBoardCopy(), mergeState.getPlayersDeepCopy());
                    ;
                    finalState.setCurrentPlayerIndex(state.getCurrentPlayerIndex());
                    try{
                        GameOperations.buyStocks(finalState, combination);
                    }catch (IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }

                    GameTreeNode combinationNode = new GameTreeNode(finalState);

                    // Add the combination node as a child of the hotel node
                    hotelNode.addChild(combinationNode);

                    List<GameTreeNode> tileHandoverNodes = simulateTileAllocationAndPlayerChange(combinationNode);
                    combinationNode.setChildren(tileHandoverNodes);
                }

                // Add the hotel node as a child of the action node
                actionNodes.add(hotelNode);
                break;

            case "singleton":
                newState = new State(state.getBoard().getBoardCopy(), state.getPlayersDeepCopy());
                newState.setCurrentPlayerIndex(state.getCurrentPlayerIndex());
                // Generate child node for singleton action
                State singletonState = simulateSingletonAction(newState, tile);

                hotelNode = new GameTreeNode(singletonState);

                // Generate stock buying combinations
                stockBuyingCombinations = generateStockBuyingCombinations(singletonState);

                // Create child nodes for each stock buying combination
                for (List<String> combination : stockBuyingCombinations) {

                    // Create a node for the final state after buying stocks
                    State finalState = new State(singletonState.getBoard().getBoardCopy(), singletonState.getPlayersDeepCopy());
                    finalState.setCurrentPlayerIndex(singletonState.getCurrentPlayerIndex());
                    try{
                        GameOperations.buyStocks(finalState, combination);
                    }catch (IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }

                    GameTreeNode combinationNode = new GameTreeNode(finalState);

                    hotelNode.addChild(combinationNode);

                    List<GameTreeNode> tileHandoverNodes = simulateTileAllocationAndPlayerChange(combinationNode);
                    combinationNode.setChildren(tileHandoverNodes);

                }

                // Add the combination node as a child of the action node
                actionNodes.add(hotelNode);
                break;
            default:
                // Handle other action types or impossible actions
                break;
        }
        return actionNodes;
    }

    /** Function adds one more layer of GameTreeNode as child's of combination Node by allocating
     * available tiles randomly and changes the player turn
     * @param combinationNode
     */
    private List<GameTreeNode> simulateTileAllocationAndPlayerChange(GameTreeNode combinationNode) {
//        Random randomGenerator = new Random(System.currentTimeMillis());
        List<GameTreeNode> tileSelectionNodes = new ArrayList<>();
        State internalState = combinationNode.getValue();
        List<Tile> availableTiles = GameOperations.getAvailableTiles(internalState);
        for (Tile tile : availableTiles) {
            State tileSelectionState = new State(internalState.getBoard().getBoardCopy(), internalState.getPlayersDeepCopy());
            tileSelectionState.setCurrentPlayerIndex(internalState.getCurrentPlayerIndex());
            Player currentPlayer = tileSelectionState.getPlayers().get(tileSelectionState.getCurrentPlayerIndex());
            currentPlayer.addTile(tile);
            tileSelectionState.setCurrentPlayerIndex((tileSelectionState.getCurrentPlayerIndex() + 1) % tileSelectionState.getPlayers().size());
            tileSelectionNodes.add(new GameTreeNode(tileSelectionState));
        }
        return tileSelectionNodes;
    }

    private State simulateSingletonAction(State newState, Tile tile) {
        GameOperations.singleton(newState, tile.getRow(), tile.getColumn());
        return newState;
    }

    private State simulateMergeAction(State newState, Tile tile, List<Integer> mergerHotels, Hotel hotel) {
        Board board = newState.getBoard();
        GameOperations.merging(newState, tile.getRow(), tile.getColumn(), mergerHotels, null);
        return newState;

    }

    private State simulateFoundAction(State newState, Tile tile, Integer hotelIndex) {
        GameOperations.founding(newState, tile.getRow(), tile.getColumn(), hotelIndex);
        return newState;
    }

    private State simulateGrowAction(State newState, Tile tile) {
        GameOperations.growing(newState, tile.getRow(), tile.getColumn());
        return newState;
    }

    // Method to generate all possible combinations of buying 1, 2, or 3 stocks
    public Set<List<String>> generateStockBuyingCombinations(State nextState) {
        Set<List<String>> combinations = new HashSet<>();
        List<Integer> availableHotelShares = nextState.getBoard().getAvaiableHotelShares();
        int[] hotelsCount = GameOperations.calcHotelCounts(nextState.getBoard());
        double playerCash = nextState.getPlayers().get(nextState.getCurrentPlayerIndex()).getCash();


        // Generate combinations for 1, 2, and 3 shares
        for (int i = 1; i <= 3; i++) {
            generateCombinationsHelper(availableHotelShares, i, new ArrayList<>(), combinations, hotelsCount, playerCash, nextState);
        }

        return combinations;
    }

    // Helper method to generate combinations recursively
    private static void generateCombinationsHelper(List<Integer> availableHotelShares, int numShares, List<String> currentCombination, Set<List<String>> combinations, int[] hotelsCount, double playerCash, State nextState) {
        if (currentCombination.size() == numShares) {
            int totalSharePrice = 0;
            for (int i=0; i < currentCombination.size(); i++){
                totalSharePrice += GameOperations.calcSharePrice(currentCombination.get(i), hotelsCount[i]);
            }
            if(totalSharePrice <= playerCash) {
                Collections.sort(currentCombination);
                combinations.add(new ArrayList<>(currentCombination));
            }
            return;
        }

        for (int i = 1; i < availableHotelShares.size(); i++) {
            String hotelName = nextState.getBoard().getHotelName(i);
            int maxShares = availableHotelShares.get(i);
            if (maxShares > 0) {
                currentCombination.add(hotelName);
                generateCombinationsHelper(availableHotelShares, numShares, currentCombination, combinations, hotelsCount, playerCash, nextState);
                currentCombination.remove(currentCombination.size() - 1);
            }
        }
    }

    public void printGameTree(GameTreeNode root) {
        GameTreeHelper.printGameTreeHelper(root, 0);
    }

    public static void printLeafNodes(GameTreeNode root) {
        if (root == null) return;

        Queue<GameTreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        int level = 0; // Track the level of each node

        while (!queue.isEmpty()) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                GameTreeNode current = queue.poll();

                // Check if the current node is a leaf node (has no children)
                if (current.getChildren().isEmpty()) {
                    System.out.println("Leaf node at level " + level + ": " + current.getValue().getPlayers().get(0).getTiles()); // Print the value of the leaf node
                } else {
                    // Enqueue children nodes
                    for (GameTreeNode child : current.getChildren()) {
                        queue.offer(child);
                    }
                }
            }
            level++; // Move to the next level
        }
    }
}

