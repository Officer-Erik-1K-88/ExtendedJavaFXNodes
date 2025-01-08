package com.airent.extendedjavafxnodes.gaxml.nodes;

import com.airent.extendedjavafxnodes.Config;
import com.airent.extendedjavafxnodes.gaxml.Attributes;
import com.airent.extendedjavafxnodes.gaxml.themes.Light;
import com.airent.extendedjavafxnodes.gaxml.themes.Theme;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ElementNode extends Parent implements Element {
    private final Node node;

    private final VBox actual;

    private final Elements elements;

    private final Attributes baseFormat;
    private Theme theme = new Light();

    public ElementNode(Node node) {
        this(node, true, null);
    }

    public ElementNode(Node node, boolean canBeDisplayed, Attributes baseFormat) {
        this.node = node;
        this.baseFormat = new Attributes(this.node, baseFormat);
        parse(ElementNode.getAllElementNodes());
        if (canBeDisplayed) {
            this.actual = new VBox();
        } else {
            this.actual = null;
        }
        this.getChildren().add(this.actual);
        this.elements = new Elements(isDisplayable()?this.actual.getChildren():null);
    }

    public String tagName() {
        return "element";
    }

    @Override
    public final Node getNode() {
        return node;
    }

    @NotNull
    @Override
    public Attributes getBaseFormat() {
        return baseFormat;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    @Override
    public final boolean isDisplayable() {
        return actual != null;
    }

    protected void parse(Map<String, Class<? extends ElementNode>> tags) {
        if (node == null) return;
        // TODO: Build the parser
        loopChildren(node, tags);
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
                if (item.getNodeType() == 3) {
                    elements.add(new TextNode((Text) item, getBaseFormat()));
                }
            }
        }
    }

    @NotNull
    private static Map<String, Class<? extends ElementNode>> getAllElementNodes() {
        Reflections reflections = new Reflections(Config.gTagConfig);
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
