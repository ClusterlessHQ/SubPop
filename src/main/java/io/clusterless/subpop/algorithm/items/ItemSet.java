/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.clusterless.subpop.algorithm.items;

import java.util.Arrays;
import java.util.Comparator;

public record ItemSet(String classValue, Item... items) {
    public ItemSet orderBy(Comparator<Item> comparator) {
        Item[] array = Arrays.stream(items)
                .sorted(comparator)
                .toArray(Item[]::new);

        return new ItemSet(classValue, array);
    }
}
