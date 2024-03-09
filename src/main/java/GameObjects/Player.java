package GameObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
    private String playerName;
    private double cash;
    private List<Share> shares;
    private List<Tile> tiles;

    public Player(String playerName) {
        this.playerName = playerName;
        this.cash = 0.0;
        this.shares = new ArrayList<>();
        this.tiles = new ArrayList<>();
    }

    public Player(String playerName, double cash, List<Share> shares, List<Tile> tiles) {
        this.playerName = playerName;
        this.cash = cash;
        this.shares = shares;
        this.tiles = tiles;
    }

    public String getPlayerName() {
        return playerName;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public List<Share> getShares() {
        return shares;
    }

    public void addShare(Share share) {
        shares.add(share);
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void addTile(Tile tile) {
        tiles.add(tile);
    }

    public void removeTile(Tile tile) {
        tiles.remove(tile);
    }

    public void printPlayerInfo() {
        System.out.println("Player Name: " + playerName);
        System.out.println("Cash: $" + cash);
        System.out.println("Shares: ");
        for (Share share : shares) {
            System.out.println(share.getLabel() + ": " + share.getCount());
        }
        System.out.println("Tiles: ");
        for (Tile tile : tiles) {
            System.out.println("Row: " + tile.getRow() + ", Column: " + tile.getColumn());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Double.compare(player.cash, cash) == 0 &&
                Objects.equals(playerName, player.playerName) &&
                Objects.equals(shares, player.shares) &&
                Objects.equals(tiles, player.tiles);
    }


}
