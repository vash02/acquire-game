package com.oosd.project09.GameFunctions;

import GameObjects.*;
import com.oosd.project09.utils.Pair;

import java.util.*;

public class RandomPlayerStrategy extends PlayerStrategy {

    private final Random randomGenerator;
    private Set<Tile> previousTiles;
    private List<Integer> availableHotels;
    private int[] hotelCounts;

    private List<Object> playTurnResult;

    public RandomPlayerStrategy() {
        this.randomGenerator = new Random(System.currentTimeMillis());
        this.previousTiles = new HashSet<>();
    }

    @Override
    public List<Object> playTurn(State state) {
        // Initialize strategy-specific variables
        initStrategy(state);

        Player player = state.getPlayers().get(state.getCurrentPlayerIndex());
        boolean executed = false;
        int attempts = 0;
        int MAX_ATTEMPTS = 6;

        while (!executed && attempts <= MAX_ATTEMPTS) {
            executed = true;
            playTurnResult = new ArrayList<>();
            if (previousTiles.size() == player.getTiles().size()) {
                System.out.println("No tile could be placed, replacing the tile");
                GameOperations.replaceTile(state);
                playTurnResult.add(null);
                break;
            }

            Tile selectedTile = nextTile(state);
            playTurnResult.add(selectedTile);
            Pair inspectionResult = GameOperations.inspect(state, selectedTile.getRow(), selectedTile.getColumn());
            String operation = inspectionResult.getFirst();

            switch (operation) {
                case "singleton":
                    GameOperations.singleton(state, selectedTile.getRow(), selectedTile.getColumn());
                    break;
                case "found":
                    GameOperations.founding(state, selectedTile.getRow(), selectedTile.getColumn(), null);
                    break;
                case "grow":
                    GameOperations.growing(state, selectedTile.getRow(), selectedTile.getColumn());
                    break;
                case "merge":
                    GameOperations.merging(state, selectedTile.getRow(), selectedTile.getColumn(),
                            inspectionResult.getSecond(), null);
                    break;
                case "impossible":
                    System.out.println("Tile cannot be placed at the position, trying next tile: Random");
                    executed = false;
                    attempts++;
                    break; // Skip the rest of the loop and try the next tile
            }
        }

        if (executed) {
            // Buying random stocks
            if (playTurnResult != null)
                playTurnResult.add(buyStrategyStocks(state));

            // Simulating a player drawing a random tile
            GameOperations.executePlayerDraw(state);
        }

        // Update current player index for the next turn
        return playTurnResult;
    }

    private void initStrategy(State state) {
        this.hotelCounts = GameOperations.calcHotelCounts(state.getBoard());
        this.availableHotels = new ArrayList<>();

        for (int i = 1; i < hotelCounts.length; i++) {
            if (hotelCounts[i] != 0) {
                availableHotels.add(i);
            }
        }
    }

    @Override
    public List<String> buyStrategyStocks(State state) {
        if (availableHotels.isEmpty()) {
            System.out.println("No available hotel chains to buy");
            return new ArrayList<>();
        }

        Player currentPlayer = state.getPlayers().get(state.getCurrentPlayerIndex());
        int randomNumberHotels = randomGenerator.nextInt(availableHotels.size());
        Hotel[] hotelsInfo = state.getBoard().getHotelsInfo();
        List<String> sharesToBuy = new ArrayList<>();
        int sharesCount = 0;
        double playerCash = currentPlayer.getCash();

        for (int i = 0; i < randomNumberHotels; i++) {
            int randomHotel = availableHotels.get(randomGenerator.nextInt(availableHotels.size()));
            Hotel selectedHotel = hotelsInfo[randomHotel];
            int sharePrice = GameOperations.calcSharePrice(selectedHotel.getLabel(), hotelCounts[randomHotel]);
            int randomNumberOfShares = randomGenerator.nextInt(4);

            if (randomNumberOfShares == 0) continue;

            for (int j = 0; j < randomNumberOfShares; j++) {
                if (playerCash >= (randomNumberOfShares * sharePrice) &&
                        selectedHotel.getStocksCertificates() >= randomNumberOfShares && sharesCount < 3) {
                    sharesToBuy.add(selectedHotel.getLabel());
                    sharesCount++;
                }
            }
            playerCash -= (randomNumberOfShares * sharePrice);
        }

        try {
            GameOperations.buyStocks(state, sharesToBuy);
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal share buy request");
        }
        return new ArrayList<>();
    }

    @Override
    public Tile nextTile(State state) {
        Player player = state.getPlayers().get(state.getCurrentPlayerIndex());
        if (previousTiles.isEmpty()) {
            return player.getTiles().get(randomGenerator.nextInt(player.getTiles().size()));
        }

        List<Tile> playerTiles = player.getTiles();
        Tile selectedTile;

        do {
            selectedTile = playerTiles.get(randomGenerator.nextInt(playerTiles.size()));
        } while (previousTiles.contains(selectedTile));

        previousTiles.add(selectedTile);
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
                // Get a random index within the bounds of the list size
                int randomIndex = new Random().nextInt(hotelFilterList.size());
                // Retrieve the hotel at the random index
                Hotel randomHotel = hotelFilterList.get(randomIndex);
                // Get the index of the chosen hotel
                hotelFilter = board.getHotelIndex(randomHotel.getLabel());
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
