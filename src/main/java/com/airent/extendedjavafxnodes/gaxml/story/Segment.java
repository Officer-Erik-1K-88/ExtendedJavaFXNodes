package com.airent.extendedjavafxnodes.gaxml.story;

import com.airent.extendedjavafxnodes.gaxml.Attributes;
import com.airent.extendedjavafxnodes.gaxml.XML;
import com.airent.extendedjavafxnodes.gaxml.XMLProcessor;
import com.airent.extendedjavafxnodes.utils.Named;
import com.airent.extendedjavafxnodes.utils.Pair;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Segment implements Named {
    private final XML gaxml;
    private XMLProcessor processor;
    private final String name;

    public Segment(String name, XML gaxml) {
        this.name = name;
        this.gaxml = gaxml;
    }
    public Segment(String name, @NotNull XMLProcessor processor) {
        this.name = name;
        this.gaxml = processor.getFile();
        this.processor = processor;
    }

    @Override
    public String getName() {
        return name;
    }

    public XML getGaxml() {
        return gaxml;
    }

    public List<Node> load(Attributes attributes) {
        if (processor == null) {
            //System.out.println(gaxml.getFile().getPath());
            processor = new XMLProcessor(gaxml);
        }
        return processor.load(attributes);
    }
}
