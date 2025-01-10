package com.airent.extendedjavafxnodes.gaxml.nodes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Orientation.VERTICAL;

public class Gap extends Parent {
    private Region region;

    public Gap(double space) {
        super();
        region = new Region();
        getChildren().add(region);
        setSpace(space);
    }

    public Gap(double space, Orientation orientation) {
        this(space);
        setOrientation(orientation);
    }

    /* *************************************************************************
     *                                                                         *
     *                               Properties                                *
     *                                                                         *
     **************************************************************************/

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
                public CssMetaData<Gap, Orientation> getCssMetaData() {
                    return StyleableProperties.ORIENTATION;
                }

                @Override
                public Object getBean() {
                    return Gap.this;
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

    private DoubleProperty space;

    public DoubleProperty spaceProperty() {
        if (space == null) {
            space = new StyleableDoubleProperty() {

                @Override
                public void invalidated() {
                    update();
                    requestLayout();
                }

                @Override
                public CssMetaData<Gap, Number> getCssMetaData() {
                    return StyleableProperties.SPACE;
                }

                @Override
                public Object getBean() {
                    return Gap.this;
                }

                @Override
                public String getName() {
                    return "space";
                }
            };
        }
        return space;
    }

    public double getSpace() {
        return spaceProperty().get();
    }

    public void setSpace(double space) {
        this.spaceProperty().set(space);
    }

    /* *************************************************************************
     *                                                                         *
     *                           Computing Sizes                               *
     *                                                                         *
     **************************************************************************/

    private void update() {
        /*if (getOrientation() == HORIZONTAL) {
            region.setMinSize(getSpace(), 0.0);
            region.setPrefSize(getSpace(), 0.0);
            region.setMaxSize(getSpace(), 0.0);
        } else {
            region.setMinSize(0.0, getSpace());
            region.setPrefSize(0.0, getSpace());
            region.setMaxSize(0.0, getSpace());
        }*/
        if (getOrientation() == HORIZONTAL) {
            region.setMinWidth(getSpace());
            region.setPrefWidth(getSpace());
            region.setMaxWidth(getSpace());
            region.setMinHeight(Region.USE_COMPUTED_SIZE);
            region.setPrefHeight(Region.USE_COMPUTED_SIZE);
            region.setMaxHeight(Region.USE_COMPUTED_SIZE);
        } else {
            region.setMinHeight(getSpace());
            region.setPrefHeight(getSpace());
            region.setMaxHeight(getSpace());
            region.setMinWidth(Region.USE_COMPUTED_SIZE);
            region.setPrefWidth(Region.USE_COMPUTED_SIZE);
            region.setMaxWidth(Region.USE_COMPUTED_SIZE);
        }
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
        return prefWidth(height);
    }

    @Override
    public double minHeight(double width) {
        return prefHeight(width);
    }

    @Override
    public double prefWidth(double height) {
        if (getOrientation() == HORIZONTAL) return getSpace();
        return 0.0;
    }

    @Override
    public double prefHeight(double width) {
        if (getOrientation() == VERTICAL) return getSpace();
        return 0.0;
    }

    @Override
    public double maxWidth(double height) {
        return prefWidth(height);
    }

    @Override
    public double maxHeight(double width) {
        return prefHeight(width);
    }

    /* *************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/

    @Override
    public Node getStyleableNode() {
        return region;
    }

    private static class StyleableProperties {
        private static final CssMetaData<Gap,Number> SPACE =
                new CssMetaData<>("-fx-space",
                        SizeConverter.getInstance(), 0.0){

                    @Override
                    public boolean isSettable(Gap node) {
                        return node.space == null || !node.space.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(Gap node) {
                        return (StyleableProperty<Number>)node.spaceProperty();
                    }

                };
        private static final CssMetaData<Gap,Orientation> ORIENTATION =
                new CssMetaData<>("-fx-orientation",
                        new EnumConverter<>(Orientation.class),
                        Orientation.HORIZONTAL) {

                    @Override
                    public Orientation getInitialValue(Gap node) {
                        // A vertical flow pane should remain vertical
                        return node.getOrientation();
                    }

                    @Override
                    public boolean isSettable(Gap node) {
                        return node.orientation == null || !node.orientation.isBound();
                    }

                    @Override
                    public StyleableProperty<Orientation> getStyleableProperty(Gap node) {
                        return (StyleableProperty<Orientation>)node.orientationProperty();
                    }

                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>();
            styleables.add(SPACE);
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
