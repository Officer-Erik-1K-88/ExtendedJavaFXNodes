package com.airent.extendedjavafxnodes.gaxml.nodes;

import com.airent.extendedjavafxnodes.Config;
import javafx.scene.Parent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ElementNode extends Parent implements Element {
    private final Node node;

    private final ArrayList<Element> elements;

    public ElementNode(Node node) {
        this.node = parse(node);
        this.elements = new ArrayList<>();
    }

    public String tagName() {
        return "element";
    }

    @Override
    public final Node getNode() {
        return node;
    }

    protected Node parse(Node node) {
        if (node == null) return null;
        // TODO: Build the parser
        Map<String, Class<? extends ElementNode>> tags = ElementNode.getAllElementNodes();
        loopChildren(node, tags);
        return node;
    }

    protected final void loopChildren(@NotNull Node node, Map<String, Class<? extends ElementNode>> tags) {
        for (int i=0; i < node.getChildNodes().getLength(); i++) {
            Node item = node.getChildNodes().item(i);
            if (item instanceof org.w3c.dom.Element elm) {
                if (tags.containsKey(elm.getTagName())) {
                    try {
                        elements.add(tags.get(elm.getTagName()).getConstructor(Node.class).newInstance(elm));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                // TODO: Make actual text parsing here.
                elements.add(new ElementNode(item));
            }
        }
    }

    @NotNull
    private static Map<String, Class<? extends ElementNode>> getAllElementNodes() {
        Reflections reflections = new Reflections(Config.nodeConfig);
        Set<Class<? extends ElementNode>> types = reflections.getSubTypesOf(ElementNode.class);
        HashMap<String, Class<? extends ElementNode>> storedTypes = new HashMap<>(types.size());
        for (Class<? extends ElementNode> type : types) {
            String name;
            try {
                Method tagName = type.getMethod("tagName");
                name = (String) tagName.invoke(null);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                name = type.getName();
            }
            storedTypes.put(name, type);
        }
        return storedTypes;
    }
}
