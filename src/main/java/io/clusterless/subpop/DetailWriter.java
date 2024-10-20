/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.clusterless.subpop;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import io.clusterless.subpop.algorithm.detail.Detail;

import java.io.*;

public class DetailWriter {
    protected char separator = ',';
    protected boolean hasHeader = true;
    protected Float supportRatio = null;
    protected PrintStream printStream = System.out;

    public static Builder builder() {
        return Builder.builder();
    }

    public void write(Detail detail) {
        CSVWriterBuilder writerBuilder = new CSVWriterBuilder(new PrintWriter(printStream))
                .withSeparator(separator);

        try (ICSVWriter writer = writerBuilder.build()) {

            if (hasHeader) {
                writer.writeNext(detail.header());
            }

            writer.writeAll(supportRatio == null ? detail.rows() : detail.rows(supportRatio));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static final class Builder {
        protected char separator = ',';
        protected boolean hasHeader = true;
        protected Float supportRatio = null;
        protected PrintStream printStream = System.out;

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

        public Builder withSupportRatio(Float supportRatio) {
            this.supportRatio = supportRatio;
            return this;
        }

        public Builder withPrintStream(PrintStream printStream) {
            this.printStream = printStream;
            return this;
        }

        public DetailWriter build() {
            DetailWriter detailWriter = new DetailWriter();
            detailWriter.separator = this.separator;
            detailWriter.hasHeader = this.hasHeader;
            detailWriter.supportRatio = this.supportRatio;
            detailWriter.printStream = this.printStream;
            return detailWriter;
        }
    }
}
