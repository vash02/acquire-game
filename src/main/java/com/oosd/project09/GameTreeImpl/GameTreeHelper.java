package com.oosd.project09.GameTreeImpl;

import GameObjects.Player;
import GameObjects.State;
import GameObjects.Tile;
import com.oosd.project09.utils.GameTreeNode;

import java.util.List;

public class GameTreeHelper {

    public static State generateWhatIfState(State currentState, Tile tileToPlay) {
        // Create a deep copy of the current state to avoid modifying the original state
        State whatIfState = new State(currentState.getBoard().getBoardCopy(), currentState.getPlayersDeepCopy());

        // Remove the tile to be played from the current player's tiles
        Player currentPlayer = whatIfState.getPlayers().get(whatIfState.getCurrentPlayerIndex());
        currentPlayer.removeTile(tileToPlay);

        return whatIfState;
    }

    // Helper method to print the game tree recursively
    public static void printGameTreeHelper(GameTreeNode node, int depth) {
        StringBuilder sb = new StringBuilder();

        // Add indentation based on depth
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }

        // Append node information to the StringBuilder
        sb.append("State: ").append(node.getValue()).append("\n");

        // Check if the node has children
        if (!node.getChildren().isEmpty()) {
            sb.append("Children:\n");
            for (GameTreeNode child : node.getChildren()) {
                printGameTreeHelper(child, depth + 1);
            }
        }

        System.out.println(sb.toString());
    }
}
