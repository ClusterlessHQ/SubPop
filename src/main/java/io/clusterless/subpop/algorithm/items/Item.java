package io.clusterless.subpop.algorithm.items;

public record Item(int col, String value) {
    @Override
    public String toString() {
        if (col == -1) {
            return value;
        }

        return "%d:%s".formatted(col, value);
    }
}
