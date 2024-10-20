/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.clusterless.subpop;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {

    @Test
    public void testWithCommandLineOption() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)) {
            String[] args = new String[]{
                    "--input", "src/test/resources/data/mushrooms.csv",
                    "--input-header",
                    "--class-col", "0",
                    "--class-value", "EDIBLE",
                    "--min-ratio", ".4",
            };
            PicocliRunner.run(Main.class, ctx, args);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            LineNumberReader lnr = new LineNumberReader(new InputStreamReader(bais));

            Assertions.assertEquals(10, lnr.lines().peek(System.out::println).count());
        }
    }
}
