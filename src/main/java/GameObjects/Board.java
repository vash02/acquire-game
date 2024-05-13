package GameObjects;

import com.oosd.project09.GameFunctions.GameOperations;

import java.util.*;

public class Board {
    private final int ROWS = 9;
    private final int COLUMNS = 12;

    // Array to represent occupied tiles
    private boolean[][] occupiedTiles;

    // Array to represent hotels
    private int[][] hotels;

    private Hotel[] hotelsInfo;

    private Map<String, Integer> hotelLabels;

    public Board() {
        // Initialize occupiedTiles as 2D array with all elements set to 0
        occupiedTiles = new boolean[ROWS][COLUMNS];

        // Initialize the hotels array
        hotels = new int[ROWS][COLUMNS];

        initializeHotelLabels();

        hotelsInfo = GameOperations.getHotelInfo();
    }

    // Method to set the hotel value for a tile at given row and column
    public void setHotel(int row, int column, int hotelValue) {
        hotels[row][column] = hotelValue;
    }

    // Method to get the hotel value for a tile at given row and column
    public int getHotel(int row, int column) {
        return hotels[row][column];
    }

    // Method to occupy a tile at given row and column
    public void occupyTile(int row, int column) {
        occupiedTiles[row][column] = true;
    }

    public Hotel[] getHotelsInfo(){
        return hotelsInfo;
    }

    // Method to check if a tile at given row and column is occupied
    public boolean isTileOccupied(int row, int column) {
        return occupiedTiles[row][column];
    }

    public Integer getHotelIndex(String hotelName) {
        initializeHotelLabels();
        Integer label = hotelLabels.get(hotelName);
        return label != null ? label : 0; // Assuming 0 represents no hotel label
    }

    public String getHotelName(int index) {
        initializeHotelLabels();
        for (Map.Entry<String, Integer> entry : hotelLabels.entrySet()) {
            if (entry.getValue() == index) {
                return entry.getKey();
            }
        }
        return null; // Return null if no hotel name is found for the given index
    }

    public List<Hotel> getAvailableHotels() {
        List<Hotel> availableHotels = new ArrayList<>();
        for (Hotel hotel : hotelsInfo) {
            boolean isOnBoard = false;
            for (int[] row : hotels) {
                for (int hotelIndex : row) {
                    if (hotelIndex == getHotelIndex(hotel.getLabel())) {
                        isOnBoard = true;
                        break;
                    }
                }
                if (isOnBoard) {
                    break;
                }
            }
            if (!isOnBoard) {
                availableHotels.add(hotel);
            }
        }
        return availableHotels;
    }


    public int[][] getHotels(){
        return hotels;
    }

    private void initializeHotelLabels() {
        hotelLabels = new HashMap<>();
        hotelLabels.put("American", 1);
        hotelLabels.put("Continental", 2);
        hotelLabels.put("Festival", 3);
        hotelLabels.put("Imperial", 4);
        hotelLabels.put("Sackson", 5);
        hotelLabels.put("Tower", 6);
        hotelLabels.put("Worldwide", 7);
    }

    public void printBoard() {
        char tileName = 'A';
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 12; j++) {
                System.out.print("[" + (occupiedTiles[i][j] ? "X" : "-") + "]");
                System.out.print((char) (tileName + i) + "" + (j + 1) + " ");
            }
            System.out.println();
        }
    }

    public Map<String, Integer> getHotelLabels() {
        return hotelLabels;
    }

    public List<Integer> getAvaiableHotelShares() {
        List<Integer> availableHotelSharesList = new ArrayList<>();
        for (Hotel hotel : hotelsInfo){
            if (hotel == null)
                availableHotelSharesList.add(null);
            else
                availableHotelSharesList.add(hotel.getStocksCertificates());
        }
        return availableHotelSharesList;
    }

    public Board getBoardCopy(){
        Board boardClone = new Board();
        for(int i=0;i<9;i++){
            for(int j=0;j<12;j++){
                if(occupiedTiles[i][j]==true){
                    boardClone.occupyTile(i, j);
                }
                if(hotels[i][j]!=0){
                    boardClone.setHotel(i, j, hotels[i][j]);
                }
            }
        }
        Hotel[] cloneHotelInfo = boardClone.getHotelsInfo();
        for(int i=0;i< cloneHotelInfo.length;i++){
            cloneHotelInfo[i].setStocksCertificates(hotelsInfo[i].getStocksCertificates());
        }
        return boardClone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return ROWS == board.ROWS &&
                COLUMNS == board.COLUMNS &&
                Arrays.deepEquals(occupiedTiles, board.occupiedTiles) &&
                Arrays.deepEquals(hotels, board.hotels) &&
                Objects.equals(hotelLabels, board.hotelLabels);
    }




}