package com.oosd.project09.GameTreeImpl;

import GameObjects.*;
import com.oosd.project09.utils.GameTreeFilteringCriteria;
import com.oosd.project09.utils.GameTreeNode;

import java.util.*;

public class PlayerStrategyUsingGameTree {
    // Method to execute a player move using the game tree and player's strategy
    public GameTreeNode executePlayerMove(GameTreeNode rootNode, State state) {
        int currentPlayerIndex = state.getCurrentPlayerIndex();
        Player currentPlayer = state.getPlayers().get(currentPlayerIndex);
//        GameTree.printLeafNodes(rootNode);
        return executePlayerMoveHelper(rootNode, currentPlayer.getStrategy(), state,null);
    }

    // Helper method to execute a player move using the game tree and player's strategy
    public GameTreeNode executePlayerMoveHelper(GameTreeNode rootNode,PlayerStrategy strategy, State currentState, GameTreeFilteringCriteria filteringCriteria) {
        Deque<GameTreeNode> queue = new ArrayDeque<>();
        List<GameTreeNode> filteredNodes;
        queue.offer(rootNode);

//        System.out.println("available hotels: "+currentState.getBoard().getAvailableHotels().get(0).getLabel());

        // Level-wise filtering criteria
        if(filteringCriteria==null){
            filteringCriteria = new GameTreeFilteringCriteria(strategy.nextTile(currentState),
                    strategy.getHotelNodeFilter(currentState),
                    strategy.buyStrategyStocks(currentState));
        }

        int level = 0;

        while (!queue.isEmpty()) {
            level++;
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                GameTreeNode currentNode = queue.poll();
                // Filter nodes at the current level based on the criteria
                filteredNodes = filterNodes(currentNode, filteringCriteria, level,
                        currentState.getPlayers().get(currentState.getCurrentPlayerIndex()).getTiles());
//                filteredNodes.get(0).getValue().getPlayers().get(0).printPlayerInfo();

                // If there are no more child nodes, return the current node
                if (filteredNodes.isEmpty()) {
//                    currentNode.getValue().getPlayers().get(0).printPlayerInfo();
                    return currentNode;
                }
                queue.addAll(filteredNodes);

            }
        }

        // If no leaf node is found, return null
        return null;
    }

    // Method to filter child nodes based on the filtering criteria
    private List<GameTreeNode> filterNodes(GameTreeNode currentNode, GameTreeFilteringCriteria filteringCriteria, int level,List<Tile> tiles) {
        List<GameTreeNode> filteredNodes = new ArrayList<>();
        int randomIndex = (int) (Math.random() * currentNode.getChildren().size());
        int i = 0;

        for (GameTreeNode child : currentNode.getChildren()) {
            boolean shouldAddNode = true;
            i++;

            // Apply filtering based on the level
            switch (level) {
                case 1:
                    // Level 1: Tile played by the player
                    Tile tilePlayed = filteringCriteria.getTilePlayed();
                    if(tilePlayed==null){
                        break;
                    }
                    if (!matchesTile(child, tilePlayed)) {
                        shouldAddNode = false;
                    }
                    break;
                case 2:
                    // Level 2: Hotel involved in the request
                    Tile placedTile = filteringCriteria.getTilePlayed();
                    int hotelNodeFilter = filteringCriteria.getHotelNodeFilter();
                    if(placedTile==null){
                        break;
                    }
                    if (hotelNodeFilter!=-1 && !matchesHotel(child, placedTile, hotelNodeFilter)) {
                        shouldAddNode = false;
                    }
                    break;
                case 3:
                    // Level 3: Shares to buy combination
                    List<String> buyStrategyStocks = filteringCriteria.getBuyStrategyStocks();
                    if (!matchesSharesToBuy(child, buyStrategyStocks)) {
                        shouldAddNode = false;
                    }
                    break;
                case 4:
                    // Level 4: Randomly reallocated tile
                    if (i == randomIndex)
                        filteredNodes.add(child);
                        shouldAddNode = false; // No need to process further
                        break;
                default:
                    // Invalid level
                    break;
            }

            // Add the node if it passes the filtering criteria
            if (shouldAddNode) {
                filteredNodes.add(child);
            }
        }

        return filteredNodes;
    }

    private boolean matchesTile(GameTreeNode child, Tile tilePlayed) {
        State previousState = child.getParent().getValue();
        State currentState = child.getValue();
        // Get the difference in tiles between the initial state and the current state
        List<Tile> previousPlayerTiles = previousState.getPlayers().get(previousState.getCurrentPlayerIndex()).getTiles();
        List<Tile> currentPlayerTiles = currentState.getPlayers().get(currentState.getCurrentPlayerIndex()).getTiles();

        // Find the missing tile in the current player's tiles, which represents the tile played
        Tile playedTile = null;
        for (Tile tile : previousPlayerTiles) {
            if (!currentPlayerTiles.contains(tile)) {
                playedTile = tile;
                break;
            }
        }

        // Compare the played tile with the specified tile played
        if (playedTile != null) {
            // Check if the played tile matches the specified tile played
            return playedTile.getRow() == tilePlayed.getRow() &&
                    playedTile.getColumn() == tilePlayed.getColumn();
        }

        // No tile played, return false
        return false;
    }

    private boolean matchesHotel(GameTreeNode child, Tile tilePlayed, int hotelNodeFilter) {
        State state = child.getValue();
        Board board = state.getBoard();

        // Retrieve the hotel for the current placed tile in the state
        int currentTileHotelIndex = board.getHotels()[tilePlayed.getRow()][tilePlayed.getColumn()];

        // Check if the current tile hotel matches the filtering criteria hotel
        return currentTileHotelIndex == hotelNodeFilter;
    }

    private boolean matchesSharesToBuy(GameTreeNode child, List<String> buyStrategyStocks){
        State parentState = child.getParent().getValue();
        State currentState = child.getValue();

        List<Share> previousPlayerShares = parentState.getPlayers().get(parentState.getCurrentPlayerIndex()).getShares();
        List<Share> currentPlayerShares = currentState.getPlayers().get(currentState.getCurrentPlayerIndex()).getShares();

        List<String> sharesBought = getSharesDifference(previousPlayerShares, currentPlayerShares);

        List<String> sortedSharesBought = new ArrayList<>(sharesBought);
        List<String> sortedBuyStrategyStocks = new ArrayList<>(buyStrategyStocks);
        Collections.sort(sortedSharesBought);
        Collections.sort(sortedBuyStrategyStocks);

        return sortedSharesBought.equals(sortedBuyStrategyStocks);

    }

    private List<String> getSharesDifference(List<Share> previousPlayerShares, List<Share> currentPlayerShares) {
        List<String> hotelsToAdd = new ArrayList<>();

        // Iterate over the shares in the previous state
        for (Share previousStock : previousPlayerShares) {
            String hotel = previousStock.getLabel();
            int previousSharesCount = previousStock.getCount();

            // Find the shares count for the same hotel in the current state
            int currentSharesCount = 0;
            for (Share currentStock : currentPlayerShares) {
                if (currentStock.getLabel().equals(hotel)) {
                    currentSharesCount = currentStock.getCount();
                    break;
                }
            }

            // Calculate the difference in shares count
            int sharesDifference = currentSharesCount - previousSharesCount;

            // If the difference is greater than 0, add the hotel name to the list multiple times
            for (int i = 0; i < Math.abs(sharesDifference); i++) {
                hotelsToAdd.add(hotel);
            }
        }

        // Include hotels that were not in the previous list but are now with the player
        for (Share currentStock : currentPlayerShares) {
            String hotel = currentStock.getLabel();
            boolean isNewHotel = true;
            for (Share previousStock : previousPlayerShares) {
                if (previousStock.getLabel().equals(hotel)) {
                    isNewHotel = false;
                    break;
                }
            }
            // If the hotel is not in the previous list but is now with the player, add it to the list
            if (isNewHotel) {
                for (int i = 0; i < currentStock.getCount(); i++) {
                    hotelsToAdd.add(hotel);
                }
            }
        }

        return hotelsToAdd;
    }


}