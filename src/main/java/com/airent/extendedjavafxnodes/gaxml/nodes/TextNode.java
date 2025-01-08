package com.airent.extendedjavafxnodes.gaxml.nodes;

import com.airent.extendedjavafxnodes.gaxml.Attributes;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class TextNode extends Text implements Element {

    private final org.w3c.dom.Text text;

    private final Attributes baseFormat;

    public TextNode(org.w3c.dom.Text text, Attributes baseFormat) {
        super(text.getData());
        this.baseFormat = baseFormat;
        this.text = text;
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (!this.text.getData().equals(newValue)) {
                this.text.setData(newValue);
            }
        });
    }

    @Override
    public Node getNode() {
        return text;
    }

    @Override
    public boolean isDisplayable() {
        return true;
    }

    @Override
    public @NotNull Attributes getBaseFormat() {
        return baseFormat;
    }
}
