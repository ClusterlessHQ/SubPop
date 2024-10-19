/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.clusterless.subpop.algorithm;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.exceptions.CsvValidationException;
import io.clusterless.subpop.algorithm.cptree.CPTree;
import io.clusterless.subpop.algorithm.detail.Detail;
import io.clusterless.subpop.algorithm.items.Item;
import io.clusterless.subpop.algorithm.items.ItemStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class JEPTest {
    private final RFC4180Parser parser = new RFC4180ParserBuilder()
            .withSeparator(',')
            .build();

    @Test
    void mushroom() throws IOException, CsvValidationException {
        ItemStore itemStore = handle("data/mushrooms.csv", true, true);
        Assertions.assertEquals(8416, itemStore.size());

        CPTree cpTree = new CPTree(itemStore);

        List<Pattern> patterns = cpTree.findPatterns(2);

        Detail detail = new Detail(cpTree, patterns);

        System.out.println(Arrays.toString(detail.header()));
        detail.rows(.4f).forEach(row -> System.out.println(Arrays.toString(row)));
    }

    @Test
    void twoClass() throws IOException, CsvValidationException {
        ItemStore itemStore = handle("data/two-class-example.csv", true, false);

        Assertions.assertEquals(8, itemStore.size());

        CPTree cpTree = new CPTree(itemStore);

        List<Pattern> patterns = cpTree.findPatterns(2);

        Assertions.assertEquals(3, patterns.size());
        Assertions.assertTrue(patterns.contains(new Pattern(0, List.of(new Item("e"), new Item("b")), 2)));
        Assertions.assertTrue(patterns.contains(new Pattern(0, List.of(new Item("e"), new Item("c"), new Item("d")), 2)));
        Assertions.assertTrue(patterns.contains(new Pattern(1, List.of(new Item("a"), new Item("b")), 2)));

        Detail detail = new Detail(cpTree, patterns);

        System.out.println(Arrays.toString(detail.header()));
        detail.rows().forEach(row -> System.out.println(Arrays.toString(row)));
    }

    private ItemStore handle(String filename, boolean hasHeader, boolean retainCol) throws IOException, CsvValidationException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename)) {
            assert inputStream != null;
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                 CSVReader reader = createReader(inputStreamReader)) {

                int classIndex = 0;
                String[] headers = hasHeader ? reader.readNext() : new String[0];
                ItemStore itemStore = new ItemStore(classIndex, headers, retainCol);

                reader.iterator()
                        .forEachRemaining(itemStore::insert);

                return itemStore;
            }
        }
    }

    private CSVReader createReader(InputStreamReader inputStreamReader) {
        return new CSVReaderBuilder(inputStreamReader)
                .withCSVParser(parser)
                .withSkipLines(0)
                .build();
    }
}
