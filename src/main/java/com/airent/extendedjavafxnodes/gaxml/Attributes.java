package com.airent.extendedjavafxnodes.gaxml;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class Attributes {
    private Node node;
    private HashMap<String, String> attributes = new HashMap<>();
    private boolean onlyPassable;

    @SafeVarargs
    public Attributes(Map.Entry<String, String> @NotNull ... attrs) {
        this.node = null;
        this.onlyPassable = false;
        for (Map.Entry<String, String> attr : attrs) {
            put(attr.getKey(), attr.getValue());
        }
    }

    public Attributes(Node node, @NotNull Attributes attr) {
        this(node, false);
        this.attributes.putAll(attr.getAttributes());
    }

    public Attributes(Node node) {
        this(node, false);
    }

    public Attributes(Node node, boolean onlyPassable) {
        this.node = node;
        this.onlyPassable = onlyPassable;
        updateAttributes();
    }

    public Attributes(Map<String, String> attrs) {
        this(attrs, false);
    }

    public Attributes(Map<String, String> attrs, boolean onlyPassable) {
        this.node = null;
        this.attributes.putAll(attrs);
        this.onlyPassable = onlyPassable;
        updateAttributes();
    }

    public Attributes(Attributes attrs) {
        this(attrs, false);
    }
    public Attributes(@NotNull Attributes attrs, boolean onlyPassable) {
        this.node = attrs.getNode();
        this.attributes = (HashMap<String, String>) attrs.getAttributes();
        this.onlyPassable = onlyPassable;
        updateAttributes();
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
        updateAttributes();
    }

    public NamedNodeMap getBaseAttributes() {
        if (node == null) return null;
        return node.getAttributes();
    }

    public Map<String, String> getAttributes() {
        return new HashMap<>(attributes);
    }

    public Map<String, String> getAttributesOnlyPassable() {
        return removeUnpassable(getAttributes());
    }

    public void  updateAttributes() {
        updateAttributes(this.onlyPassable);
    }

    public void  updateAttributes(boolean onlyPassable) {
        updateAttributes(onlyPassable, false);
    }

    public void updateAttributes(boolean onlyPassable, boolean override) {
        if (node != null) {
            if (override) attributes = new HashMap<>();
            this.onlyPassable = onlyPassable;
            NamedNodeMap attrs = getBaseAttributes();
            if (attrs != null) {
                for (int i=0; i < attrs.getLength(); i++) {
                    Node attr = attrs.item(i);
                    attributes.put(attr.getNodeName(), attr.getNodeValue());
                }
            }
        }
        if (onlyPassable) {
            removeUnpassable(attributes);
        }
    }

    // map
    public boolean containsKey(String key) {
        return attributes.containsKey(key);
    }

    public boolean check(String attrName, String attrValue) {
        if (containsKey(attrName)) {
            return get(attrName).equals(attrValue);
        }
        return false;
    }

    public int size() {
        return attributes.size();
    }

    public int length() {
        if (getBaseAttributes() != null) return getBaseAttributes().getLength();
        return 0;
    }

    public String get(String key) {
        return attributes.get(key);
    }

    public String getAttr(String key) {
        if (getBaseAttributes() != null) return getBaseAttributes().getNamedItem(key).getNodeValue();
        return null;
    }

    public String put(String key, String value) {
        return attributes.put(key, value);
    }

    public void putAll(@NotNull Attributes attr) {
        attributes.putAll(attr.getAttributes());
    }

    public String remove(String key) {
        return attributes.remove(key);
    }

    public String remove(String key, boolean inNode) {
        if (node != null && inNode) {
            node.getAttributes().removeNamedItem(key);
        }
        return remove(key);
    }

    // static
    private static final String[] unpassable = new String[]{
            "combine", "type", "path", "href", "onclick", "display", "id"
    };

    @NotNull
    @Contract("_ -> param1")
    public static Map<String, String> removeUnpassable(@NotNull Map<String, String> attrs) {
        for (String unpassable : unpassable) {
            attrs.remove(unpassable);
        }
        return attrs;
    }

    public static Map<String, String> removeUnpassable(@NotNull Attributes attrs) {
        for (String unpassable : unpassable) {
            attrs.remove(unpassable);
        }
        return attrs.getAttributes();
    }
}
