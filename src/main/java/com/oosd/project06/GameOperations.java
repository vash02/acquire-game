package com.oosd.project06;

import GameObjects.*;
import com.oosd.project06.utils.Pair;

import java.util.*;

public class GameOperations {
    public static int currentPlayerIndex = 0;

    public static Pair inspect(State state, int row, int column) {
        Pair result;
        for (Player player: state.getPlayers()){
            for (Tile tile: player.getTiles()){
                if (tile.getRow() == row && tile.getColumn() == column)
                    break;
            }
            currentPlayerIndex  = (currentPlayerIndex + 1) % state.getPlayers().size();
        }

        if (state.getBoard().isTileOccupied(row, column)) {
            result = new Pair("occupied", null);
            return result;
        }

        if (occupiedNeighbors(state.getBoard(), row, column).isEmpty()) {
            result = new Pair("singleton", null);
            return result;
        }
        int[] hotelsCount = calcHotelCounts(state.getBoard());

        // Check if the specified tile can initiate founding, merging, or growing
        boolean isFound = canFound(state.getBoard(), row, column, hotelsCount);
        ArrayList<Integer> isMerge = canMerge(state.getBoard(), row, column, hotelsCount);
        int isGrow = canGrow(state.getBoard(), row, column);

        // Construct the response based on the inspection results

        if (isFound) {
            result = new Pair("found", null);
            return result;
        }
        if (isGrow != 0) {
            List<Integer> list = new ArrayList<>();
            list.add(isGrow);
            result = new Pair("grow", list);
            return result;
        }
        if (isMerge != null) {
            result = new Pair("merge", isMerge);
            return result;
        }
        result = new Pair("impossible", null);
        return result;
    }

    public static void singleton(State state, int row, int column) {
            state.getBoard().occupyTile(row, column);
            executePlayerTurn(row, column, state.getPlayers().get(currentPlayerIndex));
    }

    public static void founding(State state, int row, int column, int label) {
        String hotelName = state.getBoard().getHotelName(label);
        Hotel hotel = new Hotel(hotelName);
        int[] hotelCounts = calcHotelCounts(state.getBoard());
        Board board = state.getBoard();
        ArrayList<Tile> neighbors = occupiedNeighbors(board, row, column);
        board.occupyTile(row, column);
        executePlayerTurn(row, column, state.getPlayers().get(currentPlayerIndex));
        if (hotelCounts[label] == 0) {
            board.setHotel(row, column, label);
            for (Tile neighbor : neighbors) {
                board.setHotel(neighbor.getRow(), neighbor.getColumn(), label);
            }
            if(hotel.getStocksCertificates() != 0) {
                hotel.setStocksCertificates(hotel.getStocksCertificates() -  1);
                Player player = state.getPlayers().get(currentPlayerIndex);
                for(Share share : player.getShares()) {
                    if(share.getLabel().equals(hotelName)) {
                        share.setCount(share.getCount() + 1);
                        return;
                    }
                }
                player.addShare(new Share(hotelName, 1));
            }
        }
    }



    public static void growing(State state, int row, int column) {
        Board board = state.getBoard();
        ArrayList<Tile> neighbors = occupiedNeighbors(board, row, column);
        int label = 0;
        for (Tile neighbor: neighbors) {
            label = board.getHotel(neighbor.getRow(), neighbor.getColumn());
            if (label != 0) {
                break;
            }
        }
        board.setHotel(row, column, label);
        board.occupyTile(row, column);
        for (Tile neighbor : neighbors) {
            board.occupyTile(neighbor.getRow(), neighbor.getColumn());
            board.setHotel(neighbor.getRow(), neighbor.getColumn(), label);
        }
        executePlayerTurn(row, column, state.getPlayers().get(currentPlayerIndex));
    }

    public static void merging(State state, int row, int column, int label, List<Integer> hotels) {
        String hotelName = state.getBoard().getHotelName(label);
        Board board = state.getBoard();
        int[] hotelCounts = calcHotelCounts(board);
        ArrayList<Tile> neighbors = occupiedNeighbors(board, row, column);

        Set<Integer> neighborHotels = new HashSet<>();

        for (Tile neighbor : neighbors) {
            int neighborLabel = board.getHotel(neighbor.getRow(), neighbor.getColumn());
            if (neighborLabel != 0) {
                neighborHotels.add(board.getHotel(neighbor.getRow(), neighbor.getColumn()));
            }
        }
        int max_value = 0;
        int max_hotel_index = 0;
        for (Integer neighborHotel : neighborHotels) {
            int a = hotelCounts[neighborHotel];
            if (max_value < a) {
                max_value = a;
                max_hotel_index = neighborHotel;
            }
        }

        if(label == 0) {
            label = max_hotel_index;
        }
        else {
            if(hotelCounts[label] != max_value)
                throw  new IllegalArgumentException("Provided Hotel is not acquirer!");
        }


        board.occupyTile(row, column);
        board.setHotel(row, column, label);
        for (Tile neighbor : neighbors) {
            board.setHotel(neighbor.getRow(), neighbor.getColumn(), label);
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 12; j++) {
                if (neighborHotels.contains(board.getHotel(i, j))) {
                    board.setHotel(i, j, label);
                }
            }
        }
        executePlayerTurn(row, column, state.getPlayers().get(currentPlayerIndex));
        for(int i=1; i<hotels.size(); i++) {
            payBonuses(state, state.getBoard().getHotelName(hotels.get(i)), hotelCounts[hotels.get(i)]);
        }
    }

    public static void buyStocks(State state, List<String> sharesList) {
        Board board = state.getBoard();
        Player player = state.getPlayers().get(currentPlayerIndex);
        Hotel[] hotelsInfo = getHotelInfo();
        int[] hotelCounts = calcHotelCounts(board);

        int sharePriceSum = 0;
        for(String hotelLable : sharesList) {
            int hotelIndex = board.getHotelLabel(hotelLable);

            if(hotelCounts[hotelIndex] == 0)
                throw new IllegalArgumentException("Hotel: " + hotelLable + " not in-play");

            if(hotelsInfo[hotelIndex].getStocksCertificates() == 0)
                throw new IllegalArgumentException("Stock Certificate for "+ hotelLable +" not available!");

            sharePriceSum += calcSharePrice(hotelLable, hotelCounts[hotelIndex]);
        }
        if(player.getCash() < sharePriceSum)
            throw new IllegalArgumentException("Not enough cash available!");

        player.setCash(player.getCash() - sharePriceSum);

        for(String hotelLable : sharesList) {
            int hotelIndex = board.getHotelLabel(hotelLable);

            boolean foundShare = false;
            for(Share share : player.getShares()) {
                if(share.getLabel().equals(hotelLable)) {
                    share.setCount(share.getCount() + 1);
                    foundShare = true;
                    break;
                }
            }
            if(!foundShare)
                player.getShares().add(new Share(hotelLable, 1));
            hotelsInfo[hotelIndex].setStocksCertificates(hotelsInfo[hotelIndex].getStocksCertificates() - 1);
        }
    }

    public static void replaceTile(State state) {
        List<Tile> availableTiles = getAvailableTiles(state);
        Collections.shuffle(availableTiles);
        Player player = state.getPlayers().get(currentPlayerIndex);
        Random random = new Random();
        int randomIndex = random.nextInt(player.getTiles().size());
        player.removeTile(player.getTiles().get(randomIndex));
        player.addTile(availableTiles.get(0));
        currentPlayerIndex  = (currentPlayerIndex + 1) % state.getPlayers().size();
    }

    private static List<Tile> getAvailableTiles(State state) {
        boolean[][] isTilePresent = new boolean[9][12];
        Board board = state.getBoard();

        for(int i=0; i<9; i++) {
            for(int j=0; j<12; j++) {
                if(board.isTileOccupied(i, j))
                    isTilePresent[i][j] = true;
            }
        }
        for(Player player : state.getPlayers()) {
            for(Tile tile : player.getTiles()) {
                isTilePresent[tile.getRow()][tile.getColumn()] = true;
            }
        }

        List<Tile> availableTiles = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 12; column++) {
                if(!isTilePresent[row][column])
                    availableTiles.add(new Tile(row, column));
            }
        }
        return availableTiles;
    }

    public static Hotel[] getHotelInfo() {
        Hotel[] hotelsInfo = new Hotel[8];
        hotelsInfo[1] = new Hotel("American");
        hotelsInfo[2] = new Hotel("Continental");
        hotelsInfo[3] = new Hotel("Festival");
        hotelsInfo[4] = new Hotel("Imperial");
        hotelsInfo[5] = new Hotel("Sackson");
        hotelsInfo[6] = new Hotel("Tower");
        hotelsInfo[7] = new Hotel("Worldwide");

        return hotelsInfo;
    }







    private static int[] calcHotelCounts (Board board){
        int[] hotelsCount = new int[8];
        for (int newRow = 0; newRow < 9; newRow++) {
            for (int newCol = 0; newCol < 12; newCol++) {
                hotelsCount[board.getHotel(newRow, newCol)] += 1;
            }
        }
        return hotelsCount;

    }

    private static ArrayList<Tile> occupiedNeighbors (Board board,int row, int column){
        // Define the array of directions (up, down, left, right) to check for neighboring tiles
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        ArrayList<Tile> neighbours = new ArrayList<>();
        // Iterate over each direction and check if the neighboring tile is occupied
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = column + direction[1];

            // Check if the neighboring tile is within the bounds of the board
            if (isValidPosition(newRow, newCol)) {
                if (board.isTileOccupied(newRow, newCol))
                    neighbours.add(new Tile(newRow, newCol)); // Found a neighboring tile that is occupied
            }
        }
        return neighbours;
    }

    private static boolean canFound (Board board,int row, int column, int[] hotelsCount){
        // Check if any neighboring tiles are occupied
        ArrayList<Tile> neighbors = occupiedNeighbors(board, row, column);
        for (Tile neighbor : neighbors) {
            if (board.getHotel(neighbor.getRow(), neighbor.getColumn()) != 0)
                return false;
        }

        for (int i = 0; i < 8; i++) {
            if (hotelsCount[i] == 0)
                return true;
        }
        return false;
    }

    private static ArrayList<Integer> canMerge (Board board,int row, int column, int[] hotelsCount){
        ArrayList<Tile> neighbors = occupiedNeighbors(board, row, column);
        boolean[] hotelsFound = new boolean[8];
        for (Tile neighbor : neighbors) {
            if (board.getHotel(neighbor.getRow(), neighbor.getColumn()) != 0) {
                hotelsFound[board.getHotel(neighbor.getRow(), neighbor.getColumn())] = true;
            }
        }
        hotelsFound[0] = false;
        int safeHotelTileCount = 0;
        int maxTileHotel = 0;
        int maxHotelTileCount = 0;
        ArrayList<Integer> mergingList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            if (hotelsFound[i] && hotelsCount[i] >= 11)
                safeHotelTileCount += 1;
            if (maxHotelTileCount < hotelsCount[i]) {
                maxTileHotel = i;
                maxHotelTileCount = hotelsCount[i];
            }
        }
        if (safeHotelTileCount > 1)
            return null;

        mergingList.add(maxTileHotel);
        for (int i=1; i<8; i++){
            if(hotelsFound[i] && i != maxTileHotel){
                mergingList.add(i);
            }
        }

        return mergingList; // For demonstration purposes, assuming merging can always be initiated
    }

    // Helper method to check if the specified tile can initiate a growing task
    private static int canGrow (Board board,int row, int column) {
        int hotelsCount = 0;
        ArrayList<Tile> neighbors = occupiedNeighbors(board, row, column);
        boolean[] hotelsFound = new boolean[8];
        for (Tile neighbor : neighbors) {
            if (board.getHotel(neighbor.getRow(), neighbor.getColumn()) != 0) {
                hotelsFound[board.getHotel(neighbor.getRow(), neighbor.getColumn())] = true;
            }
        }
        hotelsFound[0] = false;
        for (boolean hotelFound : hotelsFound) {
            if (hotelFound)
                hotelsCount += 1;
        }
        if (hotelsCount > 1)
            return 0;
        for (int i = 1; i < 8; i++) {
            if (hotelsFound[i])
                return i;

        }
        return 0;
    }

    // Helper method to check if a position is within the bounds of the board
    private static boolean isValidPosition ( int row, int col){
        return row >= 0 && row < 9 && col >= 0 && col < 12;
    }


    public static void executePlayerTurn(int row, int column, Player player){
        for(Tile tile:player.getTiles()){
            if(tile.getRow()==row && tile.getColumn()==column){
                player.removeTile(tile);
                break;
            }
        }
    }
    public static void payBonuses(State state, String acquiredHotelLabel, int acquiredTileSize) {
        int currentPrice = calcSharePrice(acquiredHotelLabel, acquiredTileSize);

        TreeMap<Integer, ArrayList<Player>> map = new TreeMap<>(Collections.reverseOrder());
        for (Player player : state.getPlayers()) {
            int shares = countShares(player, acquiredHotelLabel);
            if(!map.containsKey(shares))
                map.put(shares, new ArrayList<>());
            map.get(shares).add(player);
        }


        List<Player> majorityOwners = map.pollFirstEntry().getValue();
        List<Player> minorityOwners = map.pollFirstEntry().getValue();

        if(majorityOwners.size() >= 2) {
            long bonusPerOwner = Math.round((15.0 * currentPrice) / majorityOwners.size());
            for(Player player : majorityOwners) {
                player.setCash(player.getCash() + bonusPerOwner);

            }
        }
        else if(majorityOwners.size() == 1 && minorityOwners.size() >= 2){
            long majorityBonus = 10 * currentPrice;
            Player majorityPlayer = majorityOwners.get(0);
            majorityPlayer.setCash(majorityPlayer.getCash() + majorityBonus);

            long minorityBonus = Math.round((5.0 * currentPrice) / minorityOwners.size());
            for(Player player : minorityOwners) {
                player.setCash(player.getCash() + minorityBonus);
            }
        }
        else {
            long majorityBonus = 10 * currentPrice;
            Player majorityPlayer = majorityOwners.get(0);
            majorityPlayer.setCash(majorityPlayer.getCash() + majorityBonus);

            long minorityBonus = 5 * currentPrice;
            Player minorityPlayer = minorityOwners.get(0);
            minorityPlayer.setCash(minorityPlayer.getCash() + minorityBonus);
        }
    }

    private static int countShares(Player player, String hotelLabel) {
        for (Share share : player.getShares()) {
            if (share.getLabel().equals(hotelLabel)) {
                return share.getCount();
            }
        }
        return 0; // Player does not own shares in the specified hotel
    }

    private static int calcSharePrice(String hotelName, int hotelTileCount) {
        HashMap<Integer, Integer> priceMap = new HashMap<>();
        if(hotelName.equals("Worldwide") || hotelName.equals("Sackson")) {
            priceMap = new HashMap<>();
            priceMap.put(2, 200);
            priceMap.put(3, 300);
            priceMap.put(4, 400);
            priceMap.put(5, 500);
            priceMap.put(6, 600);
            priceMap.put(11, 700);
            priceMap.put(21, 800);
            priceMap.put(31, 900);
            priceMap.put(41, 1000);
        }
        else if(hotelName.equals("Festival") || hotelName.equals("Imperial") || hotelName.equals("American")) {
            priceMap = new HashMap<>();
            priceMap.put(2, 300);
            priceMap.put(3, 400);
            priceMap.put(4, 500);
            priceMap.put(5, 600);
            priceMap.put(6, 700);
            priceMap.put(11, 800);
            priceMap.put(21, 900);
            priceMap.put(31, 1000);
            priceMap.put(41, 1100);
        }
        else {
            priceMap = new HashMap<>();
            priceMap.put(2, 400);
            priceMap.put(3, 500);
            priceMap.put(4, 600);
            priceMap.put(5, 700);
            priceMap.put(6, 800);
            priceMap.put(11, 900);
            priceMap.put(21, 1000);
            priceMap.put(31, 1100);
            priceMap.put(41, 1200);
        }

        if (hotelTileCount == 2) {
            return priceMap.get(2);
        } else if (hotelTileCount == 3) {
            return priceMap.get(3);
        } else if (hotelTileCount == 4) {
            return priceMap.get(4);
        } else if (hotelTileCount == 5) {
            return priceMap.get(5);
        } else if (hotelTileCount < 11) {
            return priceMap.get(6);
        } else if (hotelTileCount < 21) {
            return priceMap.get(11);
        } else if (hotelTileCount < 31) {
            return priceMap.get(21);
        } else if(hotelTileCount < 41){
            return priceMap.get(31);
        }
        else {
            return  priceMap.get(41);
        }

    }
}

