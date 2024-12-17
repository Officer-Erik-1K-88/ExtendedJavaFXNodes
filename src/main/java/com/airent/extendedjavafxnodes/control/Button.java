package com.airent.extendedjavafxnodes.control;

import javafx.scene.Node;

public class Button extends javafx.scene.control.Button {
    public Button() {
        this("");
    }

    public Button(String text) {
        super(text);
    }

    public Button(Node graphic) {
        this();
        setGraphic(graphic);
    }
}
