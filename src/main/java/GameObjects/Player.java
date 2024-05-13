package GameObjects;

import com.oosd.project09.utils.StrategyMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
    private String playerName;

    private PlayerStrategy strategy;

    private int playerIndex;
    private double cash;
    private List<Share> shares;
    private List<Tile> tiles;

    public Player(String playerName, PlayerStrategy strategy) {
        this.playerName = playerName;
        this.strategy = strategy;
        this.cash = 0.0;
        this.shares = new ArrayList<>();
        this.tiles = new ArrayList<>();
    }

    public Player(String playerName){
        this.playerIndex = 0;
        this.playerName = playerName;
        this.cash = 0.0;
        this.shares = new ArrayList<>();
        this.tiles = new ArrayList<>();

    }

    public Player(int playerIndex, String playerName, double cash, List<Share> shares, List<Tile> tiles) {
        this.playerIndex = playerIndex;
        this.playerName = playerName;
        this.cash = cash;
        this.shares = shares;
        this.tiles = tiles;
    }

    public Player(int playerIndex, String playerName, double cash, List<Share> shares, List<Tile> tiles, PlayerStrategy strategy) {
        this.playerIndex = playerIndex;
        this.playerName = playerName;
        this.cash = cash;
        this.shares = shares;
        this.tiles = tiles;
        this.strategy = strategy;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerIndex() {return playerIndex;}

    public void setPlayerIndex(int playerIndex) {this.playerIndex = playerIndex;}

    public Player getPlayerDeepCopy(){
        List<Share> newShare = new ArrayList<>(shares.size());
        for(Share share:shares){
            newShare.add(new Share(share.getLabel(), share.getCount()));
        }
        Player playerClone = new Player(this.playerIndex, this.playerName, this.cash, newShare, new ArrayList<>(this.tiles), this.strategy);
        return playerClone;
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

    public void removeShare(String hotelName) {
        // Iterate through the list of shares
        for (int i = 0; i < shares.size(); i++) {
            Share share = shares.get(i);
            // Check if the share has the specified hotel name
            if (share.getLabel().equals(hotelName)) {
                // Remove the share from the list
                shares.remove(i);
                // Exit the loop after removing the share
                break;
            }
        }
    }

    public PlayerStrategy getStrategy() {return this.strategy;}

    public void setStrategy(String strategyName) {this.strategy = StrategyMapper.getStrategy(strategyName);}


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
