/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.clusterless.subpop.algorithm.items;


import com.google.common.collect.*;

import java.util.*;

public class ItemStore implements Iterable<ItemSet> {
    private final Multiset<String> classes = LinkedHashMultiset.create();
    private final Multiset<Item> items = HashMultiset.create();
    private final Multiset<ItemSet> itemSets = LinkedHashMultiset.create();

    private final int classIndex;
    private final String[] headers;
    private final boolean retainCol;

    public ItemStore(int classIndex, String[] headers, boolean retainCol) {
        this.classIndex = classIndex;
        this.headers = headers;
        this.retainCol = retainCol;
    }

    public int classIndex() {
        return classIndex;
    }

    public String[] headers() {
        return headers;
    }

    public String className() {
        return headers[classIndex];
    }

    public boolean retainCol() {
        return retainCol;
    }

    public String columnName(int index) {
        return headers[index];
    }

    public void insert(String[] record) {
        String classValue = record[classIndex];

        classes.add(classValue);

        Item[] values = new Item[record.length - 1];
        int count = 0;
        for (int i = 0; i < record.length; i++) {
            if (i == classIndex) {
                continue;
            }

            String recordValue = record[i];

            if (recordValue == null || recordValue.isEmpty()) {
                continue;
            }

            Item item = new Item(retainCol ? i : -1, recordValue);
            items.add(item); // how we calculate support
            values[count++] = item;
        }

        // remove nulls
        if (count != values.length) {
            values = Arrays.copyOf(values, count);
        }

        itemSets.add(new ItemSet(classValue, values));
    }

    public int numClasses() {
        return classes.elementSet().size();
    }

    public Set<String> classes() {
        return classes.elementSet();
    }

    public int classCount(String className) {
        return classes.count(className);
    }

    public BiMap<String, Integer> classesIndex() {
        BiMap<String, Integer> result = HashBiMap.create();

        final int[] count = {0};
        classes.elementSet().forEach(c -> result.put(c, count[0]++));

        return result;
    }

    public int itemCount(Item item) {
        return items.count(item);
    }

    public double itemSupport(Item item) {
        return (double) itemCount(item) / items.size();
    }

    @Override
    public Iterator<ItemSet> iterator() {
        return Iterators.transform(
                itemSets.iterator(),
                i -> i.orderBy(this::compareItems)
        );
    }

    public int size() {
        return itemSets.size();
    }

    public int compareItems(Item lhs, Item rhs) {
        // reverse order
        return -1 * Double.compare(itemSupport(lhs), itemSupport(rhs));
    }

    public boolean containsAllClasses(String[] classValue) {
        return classes.containsAll(Arrays.asList(classValue));
    }
}
