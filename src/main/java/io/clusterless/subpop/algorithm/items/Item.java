package io.clusterless.subpop.algorithm.items;

public record Item(int col, String value) {
    public Item {
    }

    public Item(String value) {
        this(-1, value);
    }

    @Override
    public String toString() {
        if (col == -1) {
            return value;
        }

        return "%d:%s".formatted(col, value);
    }
}
