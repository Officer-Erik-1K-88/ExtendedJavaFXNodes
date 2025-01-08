package com.airent.extendedjavafxnodes.gaxml.nodes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.text.TextFlow;

public class GeneralFlowPane extends TextFlow {
    private final DoubleProperty spacing; // spacing between nodes

    public GeneralFlowPane() {
        super();
        this.spacing = new DoublePropertyBase(5) {
            @Override
            public Object getBean() {
                return this;
            }

            @Override
            public String getName() {
                return "spacing";
            }

            @Override
            protected void invalidated() {
                requestLayout();
            }
        };
        setAccessibleRole(AccessibleRole.PARENT);
    }

    public GeneralFlowPane(Node... children) {
        this();
        getChildren().addAll(children);
    }

    public GeneralFlowPane(double spacing, Node... children) {
        this(children);
        this.spacing.set(spacing);
    }

    public final DoubleProperty spacingProperty() {
        return spacing;
    }
    public final double getSpacing() {
        return spacingProperty().get();
    }
    public final void setSpacing(double spacing) {
        this.spacingProperty().set(spacing);
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth();
        double x = 0;
        double y = 0;
        double rowHeight = 0;

        for (Node child : getChildren()) {
            if (!child.isManaged()) continue;

            Bounds childBounds = child.getLayoutBounds();
            double childWidth = childBounds.getWidth();
            double childHeight = childBounds.getHeight();

            // If the current row cannot fit the child, move to the next row
            if (x + childWidth > width) {
                x = 0;
                y += rowHeight + getSpacing();
                rowHeight = 0;
            }

            // Position the child
            child.relocate(x, y);

            // Update position for the next child
            x += childWidth + getSpacing();
            rowHeight = Math.max(rowHeight, childHeight);
        }
    }

    @Override
    protected double computePrefWidth(double height) {
        double width = 0;
        double rowWidth = 0;

        for (Node child : getChildren()) {
            if (!child.isManaged()) continue;

            Bounds childBounds = child.getLayoutBounds();
            double childWidth = childBounds.getWidth();

            if (rowWidth + childWidth > width) {
                width = Math.max(width, rowWidth);
                rowWidth = 0;
            }

            rowWidth += childWidth + getSpacing();
        }

        return Math.max(width, rowWidth);
    }

    @Override
    protected double computePrefHeight(double width) {
        double height = 0;
        double rowHeight = 0;
        double x = 0;

        for (Node child : getChildren()) {
            if (!child.isManaged()) continue;

            Bounds childBounds = child.getLayoutBounds();
            double childWidth = childBounds.getWidth();
            double childHeight = childBounds.getHeight();

            if (x + childWidth > width) {
                x = 0;
                height += rowHeight + getSpacing();
                rowHeight = 0;
            }

            x += childWidth + getSpacing();
            rowHeight = Math.max(rowHeight, childHeight);
        }

        return height + rowHeight;
    }
}
