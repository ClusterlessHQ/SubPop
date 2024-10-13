/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.clusterless.subpop.algorithm.items;

public record Item(int col, String value) {
    public Item {
    }

    public Item(String value) {
        this(-1, value);
    }

    @Override
    public String toString() {
        if (col == -1) {
            return value;
        }

        return "%d:%s".formatted(col, value);
    }
}
