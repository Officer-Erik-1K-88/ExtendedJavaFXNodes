package com.airent.extendedjavafxnodes.gaxml.nodes;

import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Orientation.VERTICAL;

public class Cell extends Parent {
    private final Node actual; // The actual content of the Cell
    private final Parent parent; // The parent node containing this Cell
    private final StackPane wrapper; // Wrapper to manage sizing without modifying the content node

    public Cell(Node actual, Parent parent, Orientation orientation) {
        super();
        if (actual == null || parent == null || orientation == null) {
            throw new IllegalArgumentException("ContentNode, parentNode, and orientation must not be null.");
        }
        this.actual = actual;
        this.parent = parent;
        this.wrapper = new StackPane();

        // Add the contentNode to the wrapper
        wrapper.getChildren().add(this.actual);
        getChildren().add(wrapper);

        // Add a listener to update the size of the wrapper
        this.parent.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> update());
        setOrientation(orientation);
        update();
    }

    public final Node getActual() {
        return actual;
    }

    public StackPane getWrapper() {
        return wrapper;
    }

    /* *************************************************************************
     *                                                                         *
     *                               Properties                                *
     *                                                                         *
     **************************************************************************/

    /**
     * The overall alignment of the GeneralFlow's content within its width and height.
     * <p>For a horizontal GeneralFlow, each row will be aligned within the GeneralFlow's width
     * using the alignment's hpos value, and the rows will be aligned within the
     * GeneralFlow's height using the alignment's vpos value.
     * <p>For a vertical GeneralFlow, each column will be aligned within the GeneralFlow's height
     * using the alignment's vpos value, and the columns will be aligned within the
     * GeneralFlow's width using the alignment's hpos value.
     * @return the overall alignment of the GeneralFlow's content within its width
     * and height
     */
    public final ObjectProperty<Pos> alignmentProperty() {
        if (alignment == null) {
            alignment = new StyleableObjectProperty<Pos>(Pos.TOP_LEFT) {

                @Override
                public void invalidated() {
                    update();
                    requestLayout();
                }

                @Override
                public CssMetaData<Cell, Pos> getCssMetaData() {
                    return StyleableProperties.ALIGNMENT;
                }

                @Override
                public Object getBean() {
                    return Cell.this;
                }

                @Override
                public String getName() {
                    return "alignment";
                }
            };
        }
        return alignment;
    }

    private ObjectProperty<Pos> alignment;
    public final void setAlignment(Pos value) { alignmentProperty().set(value); }
    public final Pos getAlignment() { return alignment == null ? Pos.TOP_LEFT : alignment.get(); }

    /**
     * The orientation of this GeneralFlow.
     * A horizontal GeneralFlow lays out children left to right, wrapping at the
     * GeneralFlow's width boundary.   A vertical GeneralFlow lays out children top to
     * bottom, wrapping at the GeneralFlow's height.
     * The default is horizontal.
     * @return the orientation of this GeneralFlow
     */
    public final ObjectProperty<Orientation> orientationProperty() {
        if (orientation == null) {
            orientation = new StyleableObjectProperty(HORIZONTAL) {
                @Override
                public void invalidated() {
                    update();
                    requestLayout();
                }

                @Override
                public CssMetaData<Cell, Orientation> getCssMetaData() {
                    return StyleableProperties.ORIENTATION;
                }

                @Override
                public Object getBean() {
                    return Cell.this;
                }

                @Override
                public String getName() {
                    return "orientation";
                }
            };
        }
        return orientation;
    }

    private ObjectProperty<Orientation> orientation;
    public final void setOrientation(Orientation value) {
        orientationProperty().set(value);
    }
    public final Orientation getOrientation() {
        return orientation == null ? HORIZONTAL : orientation.get();
    }

    /* *************************************************************************
     *                                                                         *
     *                           Computing Sizes                               *
     *                                                                         *
     **************************************************************************/

    private void update() {
        wrapper.setAlignment(getAlignment());
        if (getOrientation() == VERTICAL) {
            wrapper.setPrefWidth(parent.getLayoutBounds().getWidth());
        } else {
            wrapper.setPrefWidth(Region.USE_COMPUTED_SIZE);
        }
        // Request layout update
        wrapper.autosize();
    }

    @Override
    protected double computeMinWidth(double height) {
        return minWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        return minHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return prefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width) {
        return prefHeight(width);
    }

    @Override
    public double minWidth(double height) {
        if (getOrientation() == HORIZONTAL) return getActual().minWidth(height);
        return parent.minWidth(height);
    }

    @Override
    public double minHeight(double width) {
        return getActual().minHeight(width);
    }

    @Override
    public double prefWidth(double height) {
        if (getOrientation() == HORIZONTAL) return getActual().prefWidth(height);
        return parent.prefWidth(height);
    }

    @Override
    public double prefHeight(double width) {
        return getActual().prefHeight(width);
    }

    @Override
    public double maxWidth(double height) {
        if (getOrientation() == HORIZONTAL) return getActual().maxWidth(height);
        return parent.maxWidth(height);
    }

    @Override
    public double maxHeight(double width) {
        return getActual().maxHeight(width);
    }

    /* *************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/

    @Override
    public Node getStyleableNode() {
        return getWrapper();
    }

    private static class StyleableProperties {
        private static final CssMetaData<Cell, Pos> ALIGNMENT =
                new CssMetaData<>("-fx-alignment",
                        new EnumConverter<>(Pos.class), Pos.TOP_LEFT) {

                    @Override
                    public boolean isSettable(Cell node) {
                        return node.alignment == null || !node.alignment.isBound();
                    }

                    @Override
                    public StyleableProperty<Pos> getStyleableProperty(Cell node) {
                        return (StyleableProperty<Pos>)node.alignmentProperty();
                    }

                };

        private static final CssMetaData<Cell,Orientation> ORIENTATION =
                new CssMetaData<>("-fx-orientation",
                        new EnumConverter<>(Orientation.class),
                        Orientation.HORIZONTAL) {

                    @Override
                    public Orientation getInitialValue(Cell node) {
                        // A vertical flow pane should remain vertical
                        return node.getOrientation();
                    }

                    @Override
                    public boolean isSettable(Cell node) {
                        return node.orientation == null || !node.orientation.isBound();
                    }

                    @Override
                    public StyleableProperty<Orientation> getStyleableProperty(Cell node) {
                        return (StyleableProperty<Orientation>)node.orientationProperty();
                    }

                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Parent.getClassCssMetaData());
            styleables.add(ALIGNMENT);
            styleables.add(ORIENTATION);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
}
