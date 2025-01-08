package com.airent.extendedjavafxnodes.gaxml.nodes;

import com.airent.extendedjavafxnodes.gaxml.Attributes;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;

public class DisplayHolder extends Parent implements ENode {
    private Parent actual;

    private final Attributes baseFormat;

    public DisplayHolder(boolean block) {
        baseFormat = new Attributes();
        if (block) {
            actual = new VBox();
        } else {
            actual = new HBox();
        }
    }

    @Override
    public boolean isDisplayable() {
        return true;
    }

    @NotNull
    @Override
    public Attributes getBaseFormat() {
        return baseFormat;
    }
}
