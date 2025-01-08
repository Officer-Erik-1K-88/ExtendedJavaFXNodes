package com.airent.extendedjavafxnodes.gaxml.nodes;

import com.airent.extendedjavafxnodes.gaxml.Attributes;
import org.jetbrains.annotations.NotNull;

public interface ENode {
    boolean isDisplayable();

    @NotNull
    Attributes getBaseFormat();

    @NotNull
    default Attributes getBaseFormat(Attributes addedFormat) {
        Attributes format = new Attributes(getBaseFormat());
        format.putAll(addedFormat);
        return format;
    }
}
