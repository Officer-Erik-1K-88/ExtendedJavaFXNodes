package com.airent.extendedjavafxnodes.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class Attributes {
    private HashMap<String, String> attributes = new HashMap<>();
    private final boolean onlyPassable;

    public Attributes() {
        this(false);
    }

    public Attributes(boolean onlyPassable) {
        this.onlyPassable = onlyPassable;
    }

    public Attributes(Map<String, String> attrs) {
        this(attrs, false);
    }

    public Attributes(Map<String, String> attrs, boolean onlyPassable) {
        this.attributes.putAll(attrs);
        this.onlyPassable = onlyPassable;
    }

    public Attributes(Attributes attrs) {
        this(attrs, false);
    }
    public Attributes(@NotNull Attributes attrs, boolean onlyPassable) {
        this.attributes = (HashMap<String, String>) attrs.getAttributes();
        this.onlyPassable = onlyPassable;
    }

    public Map<String, String> getAttributes() {
        return new HashMap<>(attributes);
    }

    public Map<String, String> getAttributesOnlyPassable() {
        return removeUnpassable(getAttributes());
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

    public String get(String key) {
        return attributes.get(key);
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
