package com.airent.extendedjavafxnodes.gaxml.nodes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Elements extends AbstractList<ENode> implements List<ENode> {
    private final ArrayList<ENode> elements;
    private final ObservableList<Node> displayed;

    public Elements(ObservableList<Node> displayed) {
        if (displayed == null) {
            displayed = new ObservationList<>();
        }
        this.displayed = displayed;
        this.elements = new ArrayList<>();
    }

    @Override
    public ENode get(int index) {
        return elements.get(index);
    }

    @Override
    public ENode set(int index, ENode element) {
        elementCheck(element);
        ENode ret = elements.set(index, element);
        update();
        return ret;
    }

    @Override
    public void add(int index, ENode element) {
        elementCheck(element);
        elements.add(index, element);
        update();
    }

    @Override
    public ENode remove(int index) {
        ENode ret = elements.remove(index);
        update();
        return ret;
    }

    @Override
    public void clear() {
        elements.clear();
        displayed.clear();
    }

    @Override
    public int size() {
        return elements.size();
    }

    private void elementCheck(ENode element) {
        if (element == null) return;
        if (!element.isDisplayable()) {
            if (element instanceof Node node) {
                node.setVisible(false);
            }
        }
    }

    private void update() {
        displayed.clear();
        boolean allText = true;
        for (ENode eNode : elements) {
            if (!(eNode instanceof TextNode)) {
                allText = false;
                break;
            }
        }

    }

    private static class ObservationList<E> extends ModifiableObservableListBase<E> {
        private final ArrayList<E> list = new ArrayList<>();
        @Override
        public E get(int index) {
            return list.get(index);
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        protected void doAdd(int index, E element) {
            list.add(index, element);
        }

        @Override
        protected E doSet(int index, E element) {
            return list.set(index, element);
        }

        @Override
        protected E doRemove(int index) {
            return list.remove(index);
        }
    }
}
