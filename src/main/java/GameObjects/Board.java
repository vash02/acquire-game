package GameObjects;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Board {
    private final int ROWS = 9;
    private final int COLUMNS = 12;

    // Array to represent occupied tiles
    private boolean[][] occupiedTiles;

    // Array to represent hotels
    private int[][] hotels;

    private Map<String, Integer> hotelLabels;

    public Board() {
        // Initialize occupiedTiles as 2D array with all elements set to 0
        occupiedTiles = new boolean[ROWS][COLUMNS];

        // Initialize the hotels array
        hotels = new int[ROWS][COLUMNS];

        initializeHotelLabels();
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

    // Method to check if a tile at given row and column is occupied
    public boolean isTileOccupied(int row, int column) {
        return occupiedTiles[row][column];
    }

    public int getHotelLabel(String hotelName) {
        Integer label = hotelLabels.get(hotelName);
        return label != null ? label : 0; // Assuming 0 represents no hotel label
    }

    public String getHotelName(int index) {
        for (Map.Entry<String, Integer> entry : hotelLabels.entrySet()) {
            if (entry.getValue() == index) {
                return entry.getKey();
            }
        }
        return null; // Return null if no hotel name is found for the given index
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