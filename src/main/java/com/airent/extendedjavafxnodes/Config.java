package com.airent.extendedjavafxnodes;

import org.reflections.util.ConfigurationBuilder;

public class Config {
    public static final ConfigurationBuilder gTagConfig = new ConfigurationBuilder();
    static {
        gTagConfig.forPackage("com.airent.extendedjavafxnodes.gaxml.nodes.tags");
    }
}
