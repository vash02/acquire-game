package GameObjects;

import java.util.List;
import java.util.Objects;

public class State {
    private Board board;
    private List<Player> players;

    public State(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(board, state.board) &&
                Objects.equals(players, state.players);
    }

}
