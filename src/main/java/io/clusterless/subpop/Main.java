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
import io.clusterless.subpop.util.Verbosity;
import io.clusterless.subpop.util.VersionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "subpop",
        description = "a tool for diffing datasets",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class,
        sortOptions = false
)
public class Main implements Callable<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @CommandLine.Mixin
    protected Verbosity verbosity = new Verbosity();

    @CommandLine.Mixin
    protected InputOptions inputOptions = new InputOptions();
    @CommandLine.Mixin
    protected OutputOptions outputOptions = new OutputOptions();

    @CommandLine.Option(names = {"--class-col"}, description = "class column name or index")
    protected String classIndex = "0";

    @CommandLine.Option(names = {"--class-value"}, description = "class value")
    protected String[] classValue = new String[0];

    @CommandLine.Option(names = {"--min-support"}, description = "minimum support")
    protected int support = 2;

    @CommandLine.Option(names = {"--min-ratio"}, description = "minimum support ratio")
    protected Float supportRatio = null;

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        CommandLine commandLine = new CommandLine(main);

        try {
            commandLine.parseArgs(args);
        } catch (CommandLine.MissingParameterException | CommandLine.UnmatchedArgumentException e) {
            System.err.println(e.getMessage());
            commandLine.usage(System.out);
            System.exit(-1);
        }

        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            return;
        } else if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            return;
        }

        int exitCode = 0;

        try {
            exitCode = commandLine.execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());

            if (main.verbosity.isVerbose()) {
                e.printStackTrace(System.err);
            }

            System.exit(-1); // get exit code from exception
        }

        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        if (inputOptions.inputs().isEmpty() && hasStdIn() == 0) {
            LOG.info("no input data");
            throw new IllegalArgumentException("no input data");
        }

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
            ItemStore itemStore;
            if (inputOptions.inputs().isEmpty()) {
                LOG.info("reading from stdin");
                itemStore = itemStoreReader.read(System.in);
            } else {
                LOG.info("reading from files: {}", inputOptions.inputs());
                itemStore = itemStoreReader.read(inputOptions.inputs());
            }

            if (!itemStore.containsAllClasses(classValue)) {
                throw new IllegalArgumentException("class value not found: {}" + Arrays.toString(classValue));
            }

            CPTree cpTree = new CPTree(itemStore);

            List<Pattern> patterns = cpTree.findPatterns(support, classValue);

            Detail detail = new Detail(cpTree, patterns);

            detailWriter.write(detail);

        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    private static int hasStdIn() {
        try {
            return System.in.available();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }
}
