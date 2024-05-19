package com.oosd.project09.GameFunctions;

import GameObjects.*;
import com.oosd.project09.utils.Pair;
import com.oosd.project09.utils.StrategyMapper;

import java.io.*;
import java.util.*;

public class GameOperations{
    public static Pair inspect(State state, int row, int column) {
        Pair result;
        int currentPlayerIndex = state.getCurrentPlayerIndex();
        for (Player player : state.getPlayers()) {
            for (Tile tile : player.getTiles()) {
                if (tile.getRow() == row && tile.getColumn() == column)
                    break;
            }
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
        executePlayerTurn(row, column, state.getPlayers().get(state.getCurrentPlayerIndex()));
    }

    public static void founding(State state, int row, int column, Integer label) {
        int[] hotelCounts = calcHotelCounts(state.getBoard());
        if (label == null) {
            for (int i = 1; i < hotelCounts.length; i++) {
                if (hotelCounts[i] == 0) {
                    label = i;
                    break;
                }
            }
        }
        String hotelName = state.getBoard().getHotelName(label);
        Hotel hotel = getHotelInfo()[label];
        Board board = state.getBoard();
        ArrayList<Tile> neighbors = occupiedNeighbors(board, row, column);
        board.occupyTile(row, column);
        executePlayerTurn(row, column, state.getPlayers().get(state.getCurrentPlayerIndex()));
        if (hotelCounts[label] == 0) {
            board.setHotel(row, column, label);
            for (Tile neighbor : neighbors) {
                board.setHotel(neighbor.getRow(), neighbor.getColumn(), label);
            }
            if (hotel.getStocksCertificates() != 0) {
                hotel.setStocksCertificates(hotel.getStocksCertificates() - 1);
                Player player = state.getPlayers().get(state.getCurrentPlayerIndex());
                for (Share share : player.getShares()) {
                    if (share.getLabel().equals(hotelName)) {
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
        for (Tile neighbor : neighbors) {
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
        executePlayerTurn(row, column, state.getPlayers().get(state.getCurrentPlayerIndex()));
    }

    public static void merging(State state, int row, int column, List<Integer> hotels, Integer label) {
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
        int max_value = Integer.MIN_VALUE;
        int max_hotel_index = Integer.MAX_VALUE;

        for (Integer neighborHotel : neighborHotels) {
            int a = hotelCounts[neighborHotel];
            if (max_value < a || (max_value == a && neighborHotel < max_hotel_index)) {
                max_value = a;
                max_hotel_index = neighborHotel;
            }
        }

        List<Integer> sortedNeighborHotels = new ArrayList<>(neighborHotels);
        sortedNeighborHotels.sort((hotel1, hotel2) -> {
            int count1 = hotelCounts[hotel1];
            int count2 = hotelCounts[hotel2];
            // Sort by count in descending order
            if (count1 != count2) {
                return Integer.compare(count2, count1);
            } else {
                // If counts are equal, sort by hotel index in ascending order
                return Integer.compare(hotel1, hotel2);
            }
        });

        if (label != null && label != max_hotel_index && label != 0 && hotelCounts[label] != max_value)
            throw new IllegalArgumentException("Provided Hotel is not acquired!");

        if(max_hotel_index==Integer.MAX_VALUE){
            System.out.println(max_hotel_index);
        }

        label = max_hotel_index;


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
        executePlayerTurn(row, column, state.getPlayers().get(state.getCurrentPlayerIndex()));
        if (state.getPlayers().size() > 1){
            for (int i = 1; i < sortedNeighborHotels.size(); i++) {
                payBonuses(state, state.getBoard().getHotelName(sortedNeighborHotels.get(i)), hotelCounts[sortedNeighborHotels.get(i)]);
            }
        }
    }

    public static void buyStocks(State state, List<String> sharesList) {
        Board board = state.getBoard();
        Player player = state.getPlayers().get(state.getCurrentPlayerIndex());
        Hotel[] hotelsInfo = board.getHotelsInfo();
        int [] hotelsCount = calcHotelCounts(board);

        int sharePriceSum = 0;
        for (String hotelLabel : sharesList) {
            int hotelIndex = board.getHotelIndex(hotelLabel);

            if (hotelsInfo[hotelIndex].getStocksCertificates() == 0)
                throw new IllegalArgumentException("Stock Certificate for " + hotelLabel + " not available!");

            sharePriceSum += calcSharePrice(hotelLabel, hotelsCount[hotelIndex]);
        }
        if (player.getCash() < sharePriceSum)
            throw new IllegalArgumentException("Not enough cash available!");

        player.setCash(player.getCash() - sharePriceSum);

        for (String hotelLabel : sharesList) {
            int hotelIndex = board.getHotelIndex(hotelLabel);

            boolean foundShare = false;
            for (Share share : player.getShares()) {
                if (share.getLabel().equals(hotelLabel)) {
                    share.setCount(share.getCount() + 1);
                    foundShare = true;
                    break;
                }
            }
            if (!foundShare)
                player.getShares().add(new Share(hotelLabel, 1));
            hotelsInfo[hotelIndex].setStocksCertificates(hotelsInfo[hotelIndex].getStocksCertificates() - 1);
        }
    }

    public static boolean isGameOver(State state) {
        // Check if any hotel chain has 41 tiles or more
        int[] hotelCounts = calcHotelCounts(state.getBoard());
        for (int i = 1; i <= 7; i++) {
            if (hotelCounts[i] >= 41) {
                return true;
            }
        }

        // Check if all hotel chains are safe
        if (areAllHotelChainsSafe(hotelCounts)) {
            return true;
        }

        return false;
    }

    public static boolean areAllHotelChainsSafe(int[] hotelCounts) {
        // Check if all hotel chains are safe
        Boolean allHotelsSafe = true;
        for (int i = 1; i <= 7; i++) { // Assuming hotel labels range from 1 to 7
            if (hotelCounts[i] < 11) {
                allHotelsSafe = false;
            }
        }
        return allHotelsSafe;
    }

    public static void replaceTile(State state) {
        List<Tile> availableTiles = getAvailableTiles(state);
        if(availableTiles.isEmpty()){
            return;
        }
        int currentPlayerIndex = state.getCurrentPlayerIndex();
        Collections.shuffle(availableTiles);
        Player player = state.getPlayers().get(currentPlayerIndex);
        Random random = new Random();
        System.out.println("player tiles size "+player.getTiles().size());
        int randomIndex = random.nextInt(player.getTiles().size());
        player.removeTile(player.getTiles().get(randomIndex));
        player.addTile(availableTiles.get(0));
//        state.setCurrentPlayerIndex((currentPlayerIndex + 1) % state.getPlayers().size());
    }

    public static List<Tile> getAvailableTiles(State state) {
        boolean[][] isTilePresent = new boolean[9][12];
        Board board = state.getBoard();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 12; j++) {
                if (board.isTileOccupied(i, j))
                    isTilePresent[i][j] = true;
            }
        }
        for (Player player : state.getPlayers()) {
            for (Tile tile : player.getTiles()) {
                isTilePresent[tile.getRow()][tile.getColumn()] = true;
            }
        }

        List<Tile> availableTiles = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 12; column++) {
                if (!isTilePresent[row][column])
                    availableTiles.add(new Tile(row, column));
            }
        }
        return availableTiles;
    }

    public static Hotel[] getHotelInfo() {
        Hotel[] hotelsInfo = new Hotel[8];
        hotelsInfo[0] = new Hotel(null);
        hotelsInfo[1] = new Hotel("American");
        hotelsInfo[2] = new Hotel("Continental");
        hotelsInfo[3] = new Hotel("Festival");
        hotelsInfo[4] = new Hotel("Imperial");
        hotelsInfo[5] = new Hotel("Sackson");
        hotelsInfo[6] = new Hotel("Tower");
        hotelsInfo[7] = new Hotel("Worldwide");

        return hotelsInfo;
    }

    public static int[] calcHotelCounts(Board board) {
        int[] hotelsCount = new int[8];
            for (int newRow = 0; newRow < 9; newRow++) {
                for (int newCol = 0; newCol < 12; newCol++) {
                    int hotelIndex = board.getHotel(newRow, newCol);
                    if (hotelIndex >= 0 && hotelIndex < 8) {
                        hotelsCount[hotelIndex]++;
                    } else {
                        // Handle invalid hotel index

                        System.err.println("Invalid hotel index: " + hotelIndex);
                    }
                }
            }
        return hotelsCount;
    }

    public static List<Integer> getAvailableHotelIndices(Board board){
        int[] hotelsCount = calcHotelCounts(board);
        List<Integer> availableHotels = new ArrayList<>();
        for(int i=0; i < hotelsCount.length;i++){
            if (hotelsCount[i] == 0)
                availableHotels.add(i);
        }
        return availableHotels;
    }

    public static ArrayList<Tile> occupiedNeighbors(Board board, int row, int column) {
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

    private static boolean canFound(Board board, int row, int column, int[] hotelsCount) {
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

    private static ArrayList<Integer> canMerge(Board board, int row, int column, int[] hotelsCount) {

        ArrayList<Tile> neighbors = occupiedNeighbors(board, row, column);
        if(neighbors.size()<=1)return null;
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
        for (int i = 1; i < 8; i++) {
            if (hotelsFound[i] && i != maxTileHotel) {
                mergingList.add(i);
            }
        }

        if(mergingList.size()==1){
            return null;
        }

        return mergingList; // For demonstration purposes, assuming merging can always be initiated
    }

    // Helper method to check if the specified tile can initiate a growing task
    private static int canGrow(Board board, int row, int column) {
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
    private static boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 9 && col >= 0 && col < 12;
    }


    public static void executePlayerTurn(int row, int column, Player player) {
        for (Tile tile : player.getTiles()) {
            if (tile.getRow() == row && tile.getColumn() == column) {
                player.removeTile(tile);
                System.out.println("Tile removed");
                break;
            }
        }
    }

    public static void executePlayerDraw(State state) {
        List<Tile> availableTiles = getAvailableTiles(state);
        if (availableTiles.isEmpty()) {
            System.out.println("No tiles available to draw");
        } else {
            // Create a Random object
            Random random = new Random();

            // Generate a random index within the bounds of the list
            int randomIndex = random.nextInt(availableTiles.size());

            Player player = state.getPlayers().get(state.getCurrentPlayerIndex());
            // Return the tile at the randomly selected index
            Tile tileToDraw = availableTiles.get(randomIndex);
            player.addTile(tileToDraw);
            System.out.println("Tile added");
        }
    }

    public static void payBonuses(State state, String acquiredHotelLabel, int acquiredTileSize) {
        int currentPrice = calcSharePrice(acquiredHotelLabel, acquiredTileSize);

        TreeMap<Integer, ArrayList<Player>> map = new TreeMap<>(Collections.reverseOrder());

        // Synchronize access to the map
        synchronized (map) {
            for (Player player : state.getPlayers()) {
                int shares = countShares(player, acquiredHotelLabel);
                if (!map.containsKey(shares)) {
                    map.put(shares, new ArrayList<>());
                }
                map.get(shares).add(player);
                System.out.println("inside pay bonuses " + map.get(shares));
            }
        }

        // Retrieve and remove the first entry from the map
        Map.Entry<Integer, ArrayList<Player>> firstEntry = map.pollFirstEntry();
        List<Player> majorityOwners = (firstEntry != null) ? firstEntry.getValue() : new ArrayList<>();

        // Retrieve and remove the second entry from the map
        Map.Entry<Integer, ArrayList<Player>> secondEntry = map.pollFirstEntry();
        List<Player> minorityOwners = (secondEntry != null) ? secondEntry.getValue() : new ArrayList<>();


        if (majorityOwners.size() >= 2) {
            long bonusPerOwner = Math.round((15.0 * currentPrice) / majorityOwners.size());
            for (Player player : majorityOwners) {
                player.setCash(player.getCash() + bonusPerOwner);

            }
        } else if (majorityOwners.size() == 1 && minorityOwners.size() >= 2) {
            long majorityBonus = 10 * currentPrice;
            Player majorityPlayer = majorityOwners.get(0);
            majorityPlayer.setCash(majorityPlayer.getCash() + majorityBonus);

            long minorityBonus = Math.round((5.0 * currentPrice) / minorityOwners.size());
            for (Player player : minorityOwners) {
                player.setCash(player.getCash() + minorityBonus);
            }
        } else {
            long majorityBonus = 10 * currentPrice;
            Player majorityPlayer = majorityOwners.get(0);
            majorityPlayer.setCash(majorityPlayer.getCash() + majorityBonus);

            long minorityBonus = 5 * currentPrice;
            Player minorityPlayer = minorityOwners.get(0);
            minorityPlayer.setCash(minorityPlayer.getCash() + minorityBonus);
        }
    }

    public static void performStockBuyback(State state) {
        // Iterate through each player
        for (Player player : state.getPlayers()) {
            // Create an iterator for the player's shares
            Iterator<Share> iterator = player.getShares().iterator();
            while (iterator.hasNext()) {
                Share share = iterator.next();
                String hotelLabel = share.getLabel();
                int shareCount = share.getCount();

                // Calculate the current share price
                int currentPrice = calcSharePrice(hotelLabel, shareCount);

                // Buy back shares from the player and adjust cash balance
                int buybackAmount = currentPrice * shareCount;
                player.setCash(player.getCash() + buybackAmount);

                // Remove the current share from the player
                iterator.remove();
            }
        }
    }

    public static List<String> selectMaxOrderedShares(Board board, Player player, List<List<Object>> availableSharesList) {
        List<String> selectedShares = new ArrayList<>();
        double totalValue = 0;
        int numStocks = Math.min(3, availableSharesList.size()); // Ensure not to select more shares than available
        int [] hotelsCount = GameOperations.calcHotelCounts(board);

        // Sort available shares alphabetically by label
        availableSharesList.sort(Comparator.comparing(o -> (String) o.get(0)));

        for (List<Object> shareInfo : availableSharesList) {
            String shareLabel = (String) shareInfo.get(0);
            int hotelIndex = board.getHotelIndex(shareLabel);
            int availableCount = (int) shareInfo.get(1);

            // Calculate the value of the selected shares and check if it exceeds the player's cash
            double shareValue = numStocks * calcSharePrice(shareLabel, availableCount);

            if (totalValue + shareValue <= player.getCash() && selectedShares.size() < numStocks && hotelsCount[hotelIndex] != 0) {
                totalValue += shareValue;
                selectedShares.add(shareLabel);
            }
        }
        return selectedShares;
    }



    public static Player determineWinner(List<Player> players) {
        Player winner = null;
        double maxCash = Double.MIN_VALUE;

        for (Player player : players) {
            if (player.getCash() > maxCash) {
                maxCash = player.getCash();
                winner = player;
            }
        }

        return winner;
    }

    private static int countShares(Player player, String hotelLabel) {
        for (Share share : player.getShares()) {
            if (share.getLabel().equals(hotelLabel)) {
                return share.getCount();
            }
        }
        return 0; // Player does not own shares in the specified hotel
    }

    public static int calcSharePrice(String hotelName, int hotelTileCount) {
        HashMap<Integer, Integer> priceMap;
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

    public static boolean updateGameResultFile(Player winner){
        File currentDirectory = new File(System.getProperty("user.dir"));
        File outputDirectory = createOutputDirectory(currentDirectory);
        if(outputDirectory==null){
            return false;
        }
        File outputFile = outputDirectory.toPath().resolve("game-results.txt").toFile(),
                newOutputFile = outputDirectory.toPath().resolve("new-game-results.txt").toFile();
        try {
            if(!outputFile.exists())
                outputFile.createNewFile();
        } catch (IOException e) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(outputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(newOutputFile),true);
             ){

            int totalGames = writeTotalGames(reader,writer);
            writeStatisticsTable(reader, writer, totalGames,winner);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        outputFile.delete();
        newOutputFile.renameTo(outputFile);
        System.out.println(outputDirectory.getAbsoluteFile());
        return true;
    }

    public static int writeTotalGames(BufferedReader reader,PrintWriter writer) throws IOException{
        String line = reader.readLine();
        if(line==null || line.equals("")){
            writer.printf("%-15s = %-10s\n\n", "Total Games",1);
            return 1;
        }else{
            String[] elements = line.replace("\s","").split("=");
            int totalGames = Integer.valueOf(elements[1]);
            writer.printf("%-15s = %-10s\n\n", "Total Games",++totalGames);
            return totalGames;
        }

    }

    public static void writeStatisticsTable(BufferedReader reader,PrintWriter writer,int totalGames,Player winner) throws IOException{
        reader.skip(1);
        String line = reader.readLine();
        HashMap<String,Object[]> playerStats = new HashMap<>();
        String lineBreaker = new String(new char[104]).replace('\0','-');
        StrategyMapper mapper = new StrategyMapper();
        while ((line=reader.readLine())!=null){
            String[] rowElements = line.replace(lineBreaker, "").replaceAll("[|]|%","").split("\s+");
            if(rowElements.length==1 && rowElements[0].equals("")){
                continue;
            }
            playerStats.put(rowElements[1],
                    new Object[]{Double.valueOf(rowElements[2]),(Double.valueOf(rowElements[2])/(double)totalGames)*100, String.valueOf(rowElements[4])}
            );
        }
        if(!playerStats.containsKey(winner.getPlayerName())){
            playerStats.put(winner.getPlayerName(),
                    new Object[]{0d,(0d/(double) totalGames), mapper.getStrategyName(winner.getStrategy()) }
            );
        }
        Object[] stats = playerStats.get(winner.getPlayerName());
        stats[0] = (double)stats[0]+1;
        stats[1] = ((double)stats[0]/(double)totalGames)*100;
        String tableFormat = "%-20s| %-20s| %-20s| %-20s| %-20s\n";
        writer.printf(tableFormat,"S.NO", "Player Name","Win Total","Success Rate","Strategy");

        writer.println();
        writer.println(lineBreaker);
        Iterator<String> it = playerStats.keySet().stream().sorted(
                Comparator.comparing(player -> (Double)playerStats.get(player)[0]).reversed()
                ).iterator();
        long serialNum = 1;
        while (it.hasNext()){
            String name = it.next();
            stats = playerStats.get(name);
            String successRate = stats[1].toString();
            writer.printf(tableFormat,serialNum++,name,stats[0],
                    successRate.length()<5?successRate+"%":successRate.substring(0, 5)+"%",stats[2].toString());
            writer.println(lineBreaker);
        }
    }

    public static File createOutputDirectory(File currentDirectory) {
        File staticDirectory = currentDirectory.toPath().resolve("static/output").toFile();
        staticDirectory.mkdirs();
        return staticDirectory;

    }


}

