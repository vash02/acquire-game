package GameObjects;

import com.oosd.project09.GameFunctions.GameOperations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Hotel {
    private String label;
    private List<Tile> tiles;
    private int stocksCertificates = 25;

    public Hotel(String label) {
        this.label = label;
        this.tiles = new ArrayList<>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public int getStocksCertificates() {
        return stocksCertificates;
    }
    public void setStocksCertificates(int stocksCertificates) {
         this.stocksCertificates = stocksCertificates;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return stocksCertificates == hotel.stocksCertificates &&
                Objects.equals(label, hotel.label) &&
                Objects.equals(tiles, hotel.tiles);
    }
}
