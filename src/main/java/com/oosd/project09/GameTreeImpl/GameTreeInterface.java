package com.oosd.project09.GameTreeImpl;

import GameObjects.Hotel;
import GameObjects.State;
import GameObjects.Tile;
import com.oosd.project09.utils.GameTreeNode;

import java.util.List;
import java.util.Set;

public interface GameTreeInterface {
    GameTreeNode generateChildNodes(State state);

    List<GameTreeNode> generateChildNodesForAction(State state, Tile tile);

    Set<List<String>> generateStockBuyingCombinations(State state);
}

