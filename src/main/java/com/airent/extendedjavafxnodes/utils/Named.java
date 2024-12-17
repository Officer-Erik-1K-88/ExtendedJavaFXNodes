package com.airent.extendedjavafxnodes.utils;

public interface Named {
    String getName();

    default boolean isNameUnique() {
        return true;
    }

    default String getId() {
        return getName();
    }
}
