package com.oosd.project09.utils;

import GameObjects.State;
import com.oosd.project09.GameFunctions.LargestAlphaStrategy;
import com.oosd.project09.GameFunctions.OrderedPlayerStrategy;
import GameObjects.PlayerStrategy;
import com.oosd.project09.GameFunctions.RandomPlayerStrategy;
import com.oosd.project09.GameFunctions.SmallestAntiStrategy;

import java.util.HashMap;
import java.util.Map;

public class StrategyMapper {

    private static final Map<String, PlayerStrategy> strategyMap = new HashMap<>();

    static {
        strategyMap.put("ordered", new OrderedPlayerStrategy());
        strategyMap.put("random", new RandomPlayerStrategy());
        strategyMap.put("largest-alpha", new LargestAlphaStrategy());
        strategyMap.put("smallest-anti", new SmallestAntiStrategy());
    }

    public StrategyMapper() {
        // Populate the strategy map with strategy name to strategy object mappings


        // Add more mappings as needed
    }

    public static PlayerStrategy getStrategy(String strategyName) {
        return strategyMap.get(strategyName);
    }

    public static String getStrategyName(PlayerStrategy strategy) {
        for (Map.Entry<String, PlayerStrategy> entry : strategyMap.entrySet()) {
            if (entry.getValue().equals(strategy)) {
                return entry.getKey();
            }
        }
        return null; // Or throw an exception if the strategy is not found
    }
}
