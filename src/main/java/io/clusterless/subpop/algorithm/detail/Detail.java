/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.clusterless.subpop.algorithm.detail;

import com.google.common.collect.Comparators;
import io.clusterless.subpop.algorithm.Pattern;
import io.clusterless.subpop.algorithm.cptree.CPTree;
import io.clusterless.subpop.algorithm.items.Item;
import io.clusterless.subpop.algorithm.items.ItemStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class Detail {
    CPTree cpTree;
    List<Pattern> patterns;

    class PatternDetail implements Comparable<PatternDetail> {
        private final Pattern pattern;
        private final String classValue;
        private final boolean retainCol;
        private final float supportRatio;

        private List<String> cols;

        public PatternDetail(Pattern pattern) {
            this.pattern = pattern;
            this.classValue = cpTree.classValue(classIndex());
            this.retainCol = cpTree.itemStore().retainCol();
            int support = this.pattern.support();
            this.supportRatio = (float) support / cpTree.classSize(classValue);
        }

        public int classIndex() {
            return pattern.classIndex();
        }

        public String classValue() {
            return classValue;
        }

        public Pattern pattern() {
            return pattern;
        }

        public float supportRatio() {
            return supportRatio;
        }

        public List<String> cols() {
            if (cols != null) {
                return cols;
            }

            String[] strings = new String[cpTree.itemStore().headers().length - 1];

            if (!retainCol) {
                int i = 0;
                for (Item item : pattern().items()) {
                    strings[i++] = item.value();
                }
            } else {
                for (Item item : pattern().items()) {
                    strings[item.col() > classIndex() ? item.col() - 1 : item.col()] = item.value();
                }
            }

            this.cols = Arrays.asList(strings);

            return this.cols;
        }

        @Override
        public int compareTo(PatternDetail other) {
            return Comparator
                    .comparing(PatternDetail::classValue)
                    .thenComparing(Comparator.comparing(PatternDetail::supportRatio).reversed())
                    .thenComparing(PatternDetail::cols, Comparators.lexicographical(Comparator.nullsLast(String::compareToIgnoreCase)))
                    .compare(this, other);
        }
    }

    public Detail(CPTree cpTree, List<Pattern> patterns) {
        this.cpTree = cpTree;
        this.patterns = patterns;
    }

    /**
     * The default support ratio is 5 / N where N is the number of items in the item store.
     *
     * See "Fast discovery and the generalization of strong jumping emerging patterns for building compact and accurate classifiers"
     *
     * @return the default support ratio
     */
    public float defaultSupportRation() {
        return Math.max(0.01f, 5.0f / cpTree.itemStore().size());
    }

    public String[] header() {
        ItemStore itemStore = cpTree.itemStore();
        List<String> results = new ArrayList<>();

        results.add(itemStore.className());

        int classIndex = itemStore.classIndex();

        for (int i = 0; i < itemStore.headers().length; i++) {
            if (i == classIndex) {
                continue;
            }
            results.add(itemStore.headers()[i]);
        }

        results.add("support");

        return results.toArray(new String[0]);
    }

    public String[] row(Pattern pattern) {
        PatternDetail patternDetail = new PatternDetail(pattern);

        return row(patternDetail);
    }

    private String[] row(PatternDetail patternDetail) {
        List<String> results = new ArrayList<>();

        results.add(patternDetail.classValue());

        results.addAll(patternDetail.cols());

        results.add(String.valueOf(patternDetail.supportRatio()));

        return results.toArray(new String[0]);
    }

    public Iterable<String[]> rowsAll() {
        return rows(patternDetail -> true);
    }

    public Iterable<String[]> rows() {
        return rows(hasMinSupport(defaultSupportRation()));
    }

    public Iterable<String[]> rows(float minSupportRatio) {
        return rows(hasMinSupport(minSupportRatio));
    }

    private Iterable<String[]> rows(Predicate<PatternDetail> predicate) {
        return () -> patterns.stream()
                .map(PatternDetail::new)
                .filter(predicate)
                .sorted()
                .map(this::row)
                .iterator();
    }

    private Predicate<PatternDetail> hasMinSupport(float minSupportRatio) {
        return patternDetail -> patternDetail.supportRatio() >= minSupportRatio;
    }
}
