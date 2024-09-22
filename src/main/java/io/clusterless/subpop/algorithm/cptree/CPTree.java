package io.clusterless.subpop.algorithm.cptree;

import com.google.common.collect.BiMap;
import io.clusterless.subpop.algorithm.cptree.print.PrintNode;
import io.clusterless.subpop.algorithm.items.Item;
import io.clusterless.subpop.algorithm.items.ItemSet;
import io.clusterless.subpop.algorithm.items.ItemStore;
import org.barfuin.texttree.api.TextTree;
import org.barfuin.texttree.api.TreeOptions;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CPTree {
    private final ItemStore itemStore;
    private final BiMap<String, Integer> classesIndex;

    public class Node {
        Map<Item, Entry> entries;

        Node() {
            entries = new LinkedHashMap<>();
        }

        public boolean isLeaf(){
            return entries.isEmpty();
        }

        public Entry getEntry(Item item) {
            return entries.get(item);
        }

        public void addEntry(Entry entry) {
            entries.put(entry.item, entry);
        }

        public Collection<Entry> entries() {
            Comparator<Entry> reversed = Comparator.comparingDouble(Entry::support).reversed();
            return entries.values()
                    .stream()
                    .sorted(reversed)
                    .collect(Collectors.toList());
        }
    }

    public class Entry {
        private final Item item;
        private final double support;
        private final int[] counts;
        private final Node child;

        public Entry(Item item, double support) {
            this.item = item;
            this.support = support;
            this.counts = new int[itemStore.numClasses()];
            this.child = new Node();
        }

        public double support() {
            return support;
        }

        public Item item() {
            return item;
        }

        public Node child() {
            return child;
        }

        public int[] counts() {
            return counts;
        }

        public void incrementClass(int index) {
            counts[index]++;
        }
    }

    Node root;

    public CPTree(ItemStore itemStore) {
        this.itemStore = itemStore;
        this.classesIndex = itemStore.classesIndex();
        this.root = new Node();

        itemStore.forEach(this::insert);
    }

    public ItemStore itemStore() {
        return itemStore;
    }

    public void insert(ItemSet itemSet) {
        Item[] items = itemSet.items();
        Integer index = classesIndex.get(itemSet.classValue());

        Entry last = null;

        for (Item item : items) {
            Node current;
            if (last == null) {
                current = root;
            } else {
                current = last.child;
            }

            Entry entry = current.getEntry(item);

            if (entry == null) {
                entry = new Entry(item, itemStore.itemSupport(item));
                current.addEntry(entry);
            }

            entry.incrementClass(index);

            last = entry;
        }
    }

    public String print() {
        TreeOptions options = new TreeOptions();

        return TextTree.newInstance(options)
                .render(new PrintNode(root, classesIndex.inverse()));
    }
}
