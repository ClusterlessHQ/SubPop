package io.clusterless.subpop.algorithm.items;

import java.util.Arrays;
import java.util.Comparator;

public record ItemSet(String classValue, Item... items) {
    public ItemSet orderBy(Comparator<Item> comparator) {
        Item[] array = Arrays.stream(items)
                .sorted(comparator)
                .toArray(Item[]::new);

        return new ItemSet(classValue, array);
    }
}
