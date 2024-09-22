package io.clusterless.subpop.algorithm.cptree.print;

import com.google.common.collect.BiMap;
import com.google.common.collect.Iterables;
import io.clusterless.subpop.algorithm.cptree.CPTree;
import org.barfuin.texttree.api.Node;

import javax.annotation.CheckForNull;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class PrintNode implements Node {
    private CPTree.Node root;
    private BiMap<Integer, String> classesIndex;
    private CPTree.Entry entry = null;

    public PrintNode(CPTree.Node root, BiMap<Integer, String> classesIndex) {
        this.root = root;
        this.classesIndex = classesIndex;
    }

    public PrintNode(CPTree.Entry entry) {
        this.entry = entry;
    }

    @CheckForNull
    @Override
    public String getText() {
        if (entry == null) {
            return "root[%s]".formatted(classesIndex.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .collect(Collectors.joining(",")));
        }

        return entry.item()
                       .toString() + Arrays.toString(entry.counts());
    }

    @CheckForNull
    @Override
    public Iterable<? extends Node> getChildren() {
        CPTree.Node child = root != null ? root : entry.child();
        return Iterables.transform(child.entries(), PrintNode::new);
    }
}
