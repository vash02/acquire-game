package com.oosd.project09.GameFunctions;

import GameObjects.*;
import com.oosd.project09.utils.Pair;

import java.util.*;

public class OrderedPlayerStrategy extends PlayerStrategy {

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

        if (executed) {
            //Buy stocks
            buyStrategyStocks(state);

            GameOperations.executePlayerDraw(state);
        }

        // Update current player index for the next turn
        return playTurnResult;
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
    public List<String> buyStrategyStocks(State state) {
        Board board = state.getBoard();
        Player currentPlayer = state.getPlayers().get(state.getCurrentPlayerIndex());
        List<String> sharesToBuy = new ArrayList<>();
        List<String> sharesBought = new ArrayList<>();
//        System.out.println("BOARD ");
//        state.getBoard().printBoard();

        // Calculate hotel counts on the board
        int[] hotelCounts = GameOperations.calcHotelCounts(board);
        int sharesCount = 0;

        Hotel[] hotelsInfo = board.getHotelsInfo();
        double playerCash = currentPlayer.getCash();

        // Iterate through hotel labels in alphabetical order
        for (int hotelIndex=1; hotelIndex < hotelCounts.length; hotelIndex++) {
            if (hotelCounts[hotelIndex] != 0) {
                String hotelName = board.getHotelName(hotelIndex);
                Hotel hotel = hotelsInfo[hotelIndex];
//                System.out.println("hotel name: "+ hotelName);

                // Check if the hotel has stocks available and player can afford them
                int sharePrice = GameOperations.calcSharePrice(hotelName, hotelCounts[hotelIndex]);
                int hotelStockCertificates = hotel.getStocksCertificates();

                if (playerCash < sharePrice || sharesCount == 3)
                    break;

                sharesToBuy.clear();
                while (hotelStockCertificates > 0 && playerCash >= sharePrice && sharesCount <3) {
                    sharesToBuy.add(hotelName);
                    sharesCount++;
                    hotelStockCertificates--;
                    playerCash = playerCash - sharePrice;
                }
                GameOperations.buyStocks(state, sharesToBuy);
                sharesBought.addAll(sharesToBuy);
            }

        }
        if (playTurnResult != null)
            playTurnResult.add(sharesToBuy);
//        System.out.println("PLAYER INFO ");
//        state.getPlayers().get(state.getCurrentPlayerIndex()).printPlayerInfo();
        System.out.println("inside ordered : "+ sharesBought);
        return sharesBought;
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
