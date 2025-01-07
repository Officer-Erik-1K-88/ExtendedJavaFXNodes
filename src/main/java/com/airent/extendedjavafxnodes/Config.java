package com.airent.extendedjavafxnodes;

import org.reflections.util.ConfigurationBuilder;

public class Config {
    public static final ConfigurationBuilder nodeConfig = new ConfigurationBuilder();
    static {
        nodeConfig.forPackage("com.airent.extendedjavafxnodes.gaxml.nodes.tags");
    }
}
