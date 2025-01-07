package com.airent.extendedjavafxnodes.gaxml.nodes;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class ListOfNodes implements NodeList {
    private final ArrayList<Node> nodes;

    public ListOfNodes() {
        nodes = new ArrayList<>();
    }

    public int size() {
        return nodes.size();
    }

    @Override
    public int getLength() {
        return nodes.size();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public int indexOf(Node node) {
        return nodes.indexOf(node);
    }

    public int lastIndexOf(Node node) {
        return nodes.lastIndexOf(node);
    }

    @Override
    public Node item(int index) {
        return nodes.get(index);
    }

    public void add(Node node) {
        nodes.add(node);
    }
    public void add(int index, Node node) {
        nodes.add(index, node);
    }

    public void set(int index, Node node) {
        nodes.set(index, node);
    }

    public Node remove(int index) {
        return nodes.remove(index);
    }

    public boolean remove(Node node) {
        return nodes.remove(node);
    }
}
