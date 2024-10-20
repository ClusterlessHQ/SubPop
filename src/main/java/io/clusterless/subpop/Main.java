/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.clusterless.subpop;

import com.opencsv.exceptions.CsvValidationException;
import io.clusterless.subpop.algorithm.Pattern;
import io.clusterless.subpop.algorithm.cptree.CPTree;
import io.clusterless.subpop.algorithm.detail.Detail;
import io.clusterless.subpop.algorithm.items.ItemStore;
import io.clusterless.subpop.options.InputOptions;
import io.clusterless.subpop.options.OutputOptions;
import io.micronaut.configuration.picocli.PicocliRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.List;

@Command(
        name = "subpop",
        description = "...",
        mixinStandardHelpOptions = true
)
public class Main implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    @CommandLine.Mixin
    protected InputOptions inputOptions = new InputOptions();
    @CommandLine.Mixin
    protected OutputOptions outputOptions = new OutputOptions();

    @CommandLine.Option(names = {"--class-col"}, description = "class column name or index")
    protected String classIndex = "0";

    @CommandLine.Option(names = {"--class-value"}, description = "class value")
    protected String[] classValue = null;

    @CommandLine.Option(names = {"--min-support"}, description = "minimum support")
    protected int support = 2;

    @CommandLine.Option(names = {"--min-ratio"}, description = "minimum support ratio")
    protected Float supportRatio = null;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(Main.class, args);
    }

    public void run() {
        ItemStoreReader itemStoreReader = ItemStoreReader.builder()
                .withSeparator(inputOptions.delimiter())
                .withHasHeader(inputOptions.hasHeader())
                .withClassName(classIndex)
                .build();

        DetailWriter detailWriter = DetailWriter.builder()
                .withSeparator(outputOptions.delimiter())
                .withHasHeader(outputOptions.hasHeader())
                .withSupportRatio(supportRatio)
                .withPrintStream(System.out)
                .build();

        try {
            ItemStore itemStore = itemStoreReader.read(inputOptions.inputs());

            if(!itemStore.containsAllClasses(classValue)) {
                throw new IllegalArgumentException("class value not found");
            }

            CPTree cpTree = new CPTree(itemStore);

            List<Pattern> patterns = cpTree.findPatterns(support, classValue);

            Detail detail = new Detail(cpTree, patterns);

            detailWriter.write(detail);

        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
