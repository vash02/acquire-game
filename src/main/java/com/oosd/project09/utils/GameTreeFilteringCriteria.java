package com.oosd.project09.utils;

import GameObjects.Tile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class GameTreeFilteringCriteria {
    private Tile tilePlayed;
    private int hotelNodeFilter;
    private List<String> buyStrategyStocks;

    public GameTreeFilteringCriteria(Tile tile, int hotelNodeFilter, List<String> strategyStocks) {
        this.tilePlayed = tile;
        this.hotelNodeFilter = hotelNodeFilter;
        this.buyStrategyStocks = strategyStocks;
    }

    // Constructor, getters, and setters
}

