/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.clusterless.subpop.options;

import picocli.CommandLine;

public class OutputOptions {
    @CommandLine.Option(names = {"--output-header"}, description = "has header")
    private boolean hasHeader = false;

    @CommandLine.Option(names = {"--output-delimiter"}, description = "delimiter")
    private char delimiter = ',';

    public boolean hasHeader() {
        return hasHeader;
    }

    public char delimiter() {
        return delimiter;
    }
}
