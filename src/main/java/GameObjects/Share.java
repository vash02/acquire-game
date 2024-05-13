package GameObjects;

import java.io.Serializable;
import java.util.Objects;

public class Share {
    private String label;
    private int count;

    public Share(String label, int count) {
        this.label = label;
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Share share = (Share) o;
        return count == share.count &&
                Objects.equals(label, share.label);
    }

}
