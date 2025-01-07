package com.airent.extendedjavafxnodes.gaxml.nodes;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Document implements Element {
    private org.w3c.dom.Document document;
    private Node root;

    @Override
    public Node getNode() {
        if (document == null) return root;
        return document;
    }

    public boolean isRootDocument() {
        return this.root.isEqualNode(this.document) || this.root == this.document;
    }

    public Node getRoot() {
        return root;
    }

    private void setDocument(org.w3c.dom.Document document) {
        this.document = document;
        this.root = this.document;
    }

    public void setRoot(Node root) {
        if (root == null) {
            setRoot(this.document.getDocumentElement());
        } else {
            if (root.getOwnerDocument() != this.document) {
                throw new RuntimeException("Root Node must be of the same document as current root Node.");
            }
            this.root = root;
        }
    }

    public org.w3c.dom.Element create(String tagName) {
        return createElement(tagName);
    }

    public org.w3c.dom.Element create(String tagName, Node newChild) {
        org.w3c.dom.Element elm = create(tagName);
        elm.appendChild(newChild);
        return elm;
    }

    public Node append(@NotNull Node parent, Node child) {
        return parent.appendChild(child);
    }
    public Node append(Node child) {
        return append(this.root, child);
    }

    public Node add(Node root, String tagName, String data) {
        org.w3c.dom.Element elm = create(tagName, createTextNode(data));
        return append(root, elm);
    }
    public Node add(String tagName, String data) {
        return add(this.root, tagName, data);
    }

    public Node remove(@NotNull Node root, Node oldChild) {
        return root.removeChild(oldChild);
    }
    public Node remove(Node oldChild) {
        return remove(this.root, oldChild);
    }

    @Override
    public NamedNodeMap getAttributes() {
        if (this.root.getAttributes() == null) {
            return Element.super.getAttributes();
        }
        return this.root.getAttributes();
    }

    @NotNull
    @Override
    public NodeList getChildNodes() {
        return this.root.getChildNodes();
    }
}
