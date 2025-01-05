package com.airent.extendedjavafxnodes.gaxml.nodes;

import javafx.scene.Parent;

public abstract class ElementNode extends Parent implements Element {
    private org.w3c.dom.Node node;

    @Override
    public final org.w3c.dom.Node getNode() {
        return node;
    }

    protected final void setNode(org.w3c.dom.Node node) {
        this.node = node;
    }
}
