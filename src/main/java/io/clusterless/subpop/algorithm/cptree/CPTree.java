package io.clusterless.subpop.algorithm.cptree;

import com.google.common.collect.BiMap;
import io.clusterless.subpop.algorithm.Pattern;
import io.clusterless.subpop.algorithm.cptree.print.PrintNode;
import io.clusterless.subpop.algorithm.items.Item;
import io.clusterless.subpop.algorithm.items.ItemSet;
import io.clusterless.subpop.algorithm.items.ItemStore;
import org.barfuin.texttree.api.TextTree;
import org.barfuin.texttree.api.TreeOptions;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CPTree {
    private final ItemStore itemStore;
    private final BiMap<String, Integer> classesIndex;
    private final BiMap<Integer, String> indexClasses;

    public class Node {
        Map<Item, Entry> entries;

        Node() {
            entries = new LinkedHashMap<>();
        }

        public boolean isLeaf() {
            return entries.isEmpty();
        }

        public Entry getEntry(Item item) {
            return entries.get(item);
        }

        public void addEntry(Entry entry) {
            entries.put(entry.item, entry);
        }

        public Collection<Entry> entries() {
            Comparator<Entry> reversed = Comparator.comparingDouble(Entry::itemSupport).reversed();
            return entries.values()
                    .stream()
                    .sorted(reversed)
                    .collect(Collectors.toList());
        }

        public void mergeWith(Node t1) {
            for (Entry t1Entry : t1.entries()) {
                Entry t2Entry = entries.get(t1Entry.item);
                if (t2Entry != null) {
                    t2Entry.addClassCounts(t1Entry.classCounts());
                } else {
                    t2Entry = new Entry(t1Entry);
                    entries.put(t1Entry.item, t2Entry);
                }

                t2Entry.child.mergeWith(t1Entry.child);
            }
        }
    }

    public class Entry {
        private final Item item;
        private final double itemSupport;
        private final int[] classCounts;
        private final Node child;

        public Entry(Entry other) {
            this.item = other.item;
            this.itemSupport = other.itemSupport;
            // new array to avoid sharing the same array
            this.classCounts = Arrays.copyOf(other.classCounts, other.classCounts.length);
            // epm-framework moved from a deep copy to a shallow copy
            // but the shallow has zero counts per class, so unsure if the copy is even necessary
            this.child = new Node();
        }

        public Entry(Item item, double itemSupport) {
            this.item = item;
            this.itemSupport = itemSupport;
            this.classCounts = new int[itemStore.numClasses()];
            this.child = new Node();
        }

        public double itemSupport() {
            return itemSupport;
        }

        public Item item() {
            return item;
        }

        public Node child() {
            return child;
        }

        public int[] classCounts() {
            return classCounts;
        }

        public void addClassCounts(int[] counts) {
            for (int i = 0; i < counts.length; i++) {
                this.classCounts[i] += counts[i];
            }
        }

        public void incrementClassCount(int index) {
            classCounts[index]++;
        }
    }

    Node root;

    public CPTree(ItemStore itemStore) {
        this.itemStore = itemStore;
        this.classesIndex = itemStore.classesIndex();
        this.indexClasses = itemStore.classesIndex().inverse();
        this.root = new Node();
    }

    public List<Pattern> findPatterns(int minSupport) {
        Predicate<Integer> isMin = count -> count >= minSupport;
        BiPredicate<Integer, Integer> isSJEP = (lhs, rhs) -> isMin.test(lhs) && rhs == 0;

        return findPatterns(isMin, isSJEP);
    }

    private List<Pattern> findPatterns(Predicate<Integer> isMin, BiPredicate<Integer, Integer> isSJEP) {
        itemStore.forEach(this::insert);

        List<Pattern> patterns = new LinkedList<>();

        mergeSubTreeOn(root, patterns, new Pattern(), isMin, isSJEP);

        return patterns;
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

            entry.incrementClassCount(index);

            last = entry;
        }
    }

    private void mergeSubTreeOn(Node t2, List<Pattern> patterns, Pattern alpha, Predicate<Integer> isMin, BiPredicate<Integer, Integer> isSJEP) {

        int totalItems = itemStore().size();

        for (Entry entry : t2.entries()) {
            Node t1 = entry.child;

            t2.mergeWith(t1);

            Pattern beta = new Pattern(alpha, entry.item);

            int allItemCounts = Arrays.stream(entry.classCounts).sum();
            boolean test = false;
            for (int i = 0; i < entry.classCounts.length; i++) {
                int currentItemCount = entry.classCounts[i];
                int remainingItemCount = allItemCounts - currentItemCount;

                int classCount = itemStore().classCount(indexClasses.get(i));
                int remainingClassCount = totalItems - classCount;

                test = isSJEP.test(currentItemCount, remainingItemCount);
                if (test) {
                    beta.setClassIndex(i);
                    beta.setSupport(currentItemCount);
                    patterns.add(new Pattern(beta));
                    break;
                }
            }

            if (!test && Arrays.stream(entry.classCounts).anyMatch(isMin::test)) {
                mergeSubTreeOn(t1, patterns, beta, isMin, isSJEP);
            }
        }
    }

    public String print() {
        TreeOptions options = new TreeOptions();

        return TextTree.newInstance(options)
                .render(new PrintNode(root, indexClasses));
    }
}
