package GameObjects;

import com.oosd.project09.utils.GameTreeNode;

import java.util.List;

public abstract class PlayerStrategy {

    public abstract List<Object> playTurn(State state);

    public abstract List<String> buyStrategyStocks(State state);

    public abstract Tile nextTile(State state);

    public abstract int getHotelNodeFilter(State state);
}
