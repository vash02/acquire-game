package GameObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class State {
    private static State instance;
    private Board board;
    private List<Player> players;

    private int currentPlayerIndex;

    public State(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
    }

    public static State getInstance() {
        if (instance == null) {
            // If the instance is null, create a new State object
            // Here you need to provide the necessary arguments to create the State object
            // For example, you could pass a default Board and an empty list of Players
            instance = new State(new Board(), new ArrayList<>());
        }
        return instance;
    }

    public static void setInstance(State newState) {
        instance = newState;
    }

    public List<Player> getPlayersDeepCopy(){
        List<Player> clone = new ArrayList<>(players.size());
        for(Player player:players){
            clone.add(player.getPlayerDeepCopy());
        }
        return clone;
    }

    // Getters and setters
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }



    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }


    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(board, state.board) &&
                Objects.equals(players, state.players);
    }

}
