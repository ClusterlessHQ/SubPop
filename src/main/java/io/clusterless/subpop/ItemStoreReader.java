/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.clusterless.subpop;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.exceptions.CsvValidationException;
import io.clusterless.subpop.algorithm.items.ItemStore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class ItemStoreReader {
    protected char separator = ',';
    protected boolean hasHeader = true;
    protected String className = null;
    protected boolean retainCol = true;

    public ItemStoreReader() {
    }

    public static Builder builder() {
        return Builder.builder();
    }

    public RFC4180Parser parser() {
        return new RFC4180ParserBuilder()
                .withSeparator(separator)
                .build();
    }

    public ItemStore read(List<File> files) throws CsvValidationException, IOException {
        ItemStore itemStore = null;

        for (File file : files) {
            if (itemStore == null) {
                itemStore = read(Files.newInputStream(file.toPath()));
            } else {
                itemStore = read(itemStore, Files.newInputStream(file.toPath()));
            }
        }

        return itemStore;
    }

    public ItemStore read(ItemStore itemStore, InputStream inputStream) throws IOException, CsvValidationException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             CSVReader reader = createReader(inputStreamReader)) {

            if (hasHeader) reader.readNext();

            reader.iterator()
                    .forEachRemaining(itemStore::insert);

            return itemStore;
        }
    }

    public ItemStore read(InputStream inputStream) throws IOException, CsvValidationException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             CSVReader reader = createReader(inputStreamReader)) {

            String[] headers = hasHeader ? reader.readNext() : new String[reader.peek().length];

            ItemStore itemStore = createItemStore(headers);

            reader.iterator()
                    .forEachRemaining(itemStore::insert);

            return itemStore;
        }
    }

    protected ItemStore createItemStore(String[] headers) {
        Integer classIndex = safeParse(className);

        if (hasHeader && classIndex == null && className != null) {
            classIndex = Arrays.asList(headers).indexOf(className);
        }

        if (classIndex == null || classIndex < 0) {
            throw new IllegalArgumentException("class index not found");
        }

        return new ItemStore(classIndex, headers, retainCol);
    }

    private Integer safeParse(String className) {
        try {
            return Integer.parseInt(className);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private CSVReader createReader(InputStreamReader inputStreamReader) {
        return new CSVReaderBuilder(inputStreamReader)
                .withCSVParser(parser())
                .withSkipLines(0)
                .build();
    }

    public static final class Builder {
        protected char separator = ',';
        protected boolean hasHeader = true;
        protected String className = null;
        protected boolean retainCol = true;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withSeparator(char separator) {
            this.separator = separator;
            return this;
        }

        public Builder withHasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
            return this;
        }

        public Builder withClassName(String className) {
            this.className = className;
            return this;
        }

        public Builder withRetainCol(boolean retainCol) {
            this.retainCol = retainCol;
            return this;
        }

        public ItemStoreReader build() {
            ItemStoreReader itemStoreReader = new ItemStoreReader();
            itemStoreReader.separator = this.separator;
            itemStoreReader.retainCol = this.retainCol;
            itemStoreReader.hasHeader = this.hasHeader;
            itemStoreReader.className = this.className;
            return itemStoreReader;
        }
    }
}
