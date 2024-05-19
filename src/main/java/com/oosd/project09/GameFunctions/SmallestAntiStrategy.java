package com.oosd.project09.GameFunctions;

import GameObjects.*;
import com.oosd.project09.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SmallestAntiStrategy extends PlayerStrategy {

    private int nextTileIndex;

    private List<Object> playTurnResult;
    @Override
    public List<Object> playTurn(State state) {
        int currentPlayerIndex = state.getCurrentPlayerIndex();
        nextTileIndex = 0;
        boolean executed = false;
        while (!executed) {
            executed = true;
            playTurnResult = new ArrayList<>();
            if(nextTileIndex == state.getPlayers().get(currentPlayerIndex).getTiles().size()){
                System.out.println("No tile could be placed, replacing the tile");
                GameOperations.replaceTile(state);
                playTurnResult.add(null);
                break;
            }
            Tile nextTile = nextTile(state);
            playTurnResult.add(nextTile);
            Pair inspectionResult = GameOperations.inspect(state, nextTile.getRow(), nextTile.getColumn());
            String operation = inspectionResult.getFirst();
            System.out.println("Operation: " + operation);

            switch (operation) {
                case "singleton":
                    GameOperations.singleton(state, nextTile.getRow(), nextTile.getColumn());
                    break;
                case "found":
                    GameOperations.founding(state, nextTile.getRow(), nextTile.getColumn(), null);
                    break;
                case "grow":
                    GameOperations.growing(state, nextTile.getRow(), nextTile.getColumn());
                    break;
                case "merge":
                    GameOperations.merging(state, nextTile.getRow(), nextTile.getColumn(), inspectionResult.getSecond(), null);
                    break;
                case "impossible":
                    System.out.println("Tile cannot be placed at the position, trying next tile:Ordered");
                    nextTileIndex++; // Increment the index to try the next tile
                    executed = false;
                    continue; // Skip the rest of the loop and try the next tile
            }
            //Replacing the tile of no tile can be placed by player and moving to next player's turn

        }

        buyStrategyStocks(state);
        GameOperations.executePlayerDraw(state);

        // Update current player index for the next turn
        return playTurnResult;
    }

    @Override
    public List<String> buyStrategyStocks(State state) {
        int[] hotelsCount = GameOperations.calcHotelCounts(state.getBoard());
        Hotel[] hotelsInfo = state.getBoard().getHotelsInfo();
        List<String> sharesToBuy = new ArrayList<>();
        Player currentPlayer = state.getPlayers().get(state.getCurrentPlayerIndex());
        double runningCash = currentPlayer.getCash();
        for(int i=hotelsCount.length-1;i>=1;i--){
            int runningShareCapCount = hotelsInfo[i].getStocksCertificates();
            while(hotelsCount[i]!=0 && sharesToBuy.size()<3 && runningShareCapCount>0){
                int sharePrice = GameOperations.calcSharePrice(hotelsInfo[i].getLabel(),hotelsCount[i]);
                if(runningCash<sharePrice){
                    break;
                }
                runningCash-=sharePrice;
                sharesToBuy.add(hotelsInfo[i].getLabel());
                runningShareCapCount--;
            }
        }
        if (playTurnResult != null)
            playTurnResult.add(sharesToBuy);
        GameOperations.buyStocks(state, sharesToBuy);
        return sharesToBuy;
    }

    @Override
    public Tile nextTile(State state) {
        // Get the available tiles on the board
        List<Tile> playerTiles = state.getPlayers().get(state.getCurrentPlayerIndex()).getTiles();

        // Sort the available tiles based on row and column
        Collections.sort(playerTiles, Comparator.comparing(Tile::getRow).thenComparing(Tile::getColumn));

        // Select the smallest tile to place on the board
        Tile selectedTile = playerTiles.get(nextTileIndex);

        return selectedTile;
    }

    @Override
    public int getHotelNodeFilter(State state){
        Tile nextTile = nextTile(state);
        Board board = state.getBoard();
        Pair inspectionResult = GameOperations.inspect(state, nextTile.getRow(), nextTile.getColumn());
        String operation = inspectionResult.getFirst();
        int hotelFilter;
        switch (operation) {
            case "found":
                List<Hotel> hotelFilterList = board.getAvailableHotels();
                // Sort the list in ascending order based on hotel labels
                Collections.sort(hotelFilterList, Comparator.comparing(Hotel::getLabel));
                Hotel smallestHotel = hotelFilterList.get(0);
                hotelFilter = board.getHotelIndex(smallestHotel.getLabel());
                break;
            case "grow":
                hotelFilter = inspectionResult.getSecond().get(0);
                break;
            case "merge":
                hotelFilter = inspectionResult.getSecond().get(0);
                break;
            case "impossible":
                System.out.println("Tile cannot be placed at the position, trying next tile:Ordered");
                hotelFilter = 0;
            default:
                hotelFilter = 0;
        }
        return hotelFilter;

    }
}
