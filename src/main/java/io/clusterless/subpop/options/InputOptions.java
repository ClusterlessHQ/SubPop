/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.clusterless.subpop.options;

import picocli.CommandLine;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class InputOptions {
    @CommandLine.Option(names = {"--input"}, description = "input data")
    private List<File> inputs = new LinkedList<>();

    @CommandLine.Option(names = {"--input-header"}, description = "has header")
    private boolean hasHeader = false;

    @CommandLine.Option(names = {"--input-delimiter"}, description = "delimiter")
    private char delimiter = ',';

    public List<File> inputs() {
        return inputs;
    }

    public boolean hasHeader() {
        return hasHeader;
    }

    public char delimiter() {
        return delimiter;
    }
}
