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
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javafx.geometry.Orientation.HORIZONTAL;

public class GeneralFlow extends TextFlow {

    public GeneralFlow() {
        super();
    }

    public GeneralFlow(Node... children) {
        super(children);
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
                public CssMetaData<GeneralFlow, Orientation> getCssMetaData() {
                    return StyleableProperties.ORIENTATION;
                }

                @Override
                public Object getBean() {
                    return GeneralFlow.this;
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

    /**
     * The amount of horizontal space between each node in a horizontal GeneralFlow
     * or the space between columns in a vertical GeneralFlow.
     * @return the amount of horizontal space between each node in a horizontal
     * GeneralFlow or the space between columns in a vertical GeneralFlow
     */
    public final DoubleProperty hgapProperty() {
        if (hgap == null) {
            hgap = new StyleableDoubleProperty() {

                @Override
                public void invalidated() {
                    update();
                    requestLayout();
                }

                @Override
                public CssMetaData<GeneralFlow, Number> getCssMetaData() {
                    return StyleableProperties.HGAP;
                }

                @Override
                public Object getBean() {
                    return GeneralFlow.this;
                }

                @Override
                public String getName() {
                    return "hgap";
                }
            };
        }
        return hgap;
    }

    private DoubleProperty hgap;
    public final void setHgap(double value) { hgapProperty().set(value); }
    public final double getHgap() { return hgap == null ? 0 : hgap.get(); }


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
                public CssMetaData<GeneralFlow, Pos> getCssMetaData() {
                    return GeneralFlow.StyleableProperties.ALIGNMENT;
                }

                @Override
                public Object getBean() {
                    return GeneralFlow.this;
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
    private Pos getAlignmentInternal() {
        Pos localPos = getAlignment();
        return localPos == null ? Pos.TOP_LEFT : localPos;
    }

    /**
     * The horizontal alignment of nodes within each column of a vertical GeneralFlow.
     * The property is ignored for horizontal GeneralFlows.
     * @return the horizontal alignment of nodes within each column of a
     * vertical GeneralFlow
     */
    public final ObjectProperty<HPos> columnHalignmentProperty() {
        if (columnHalignment == null) {
            columnHalignment = new StyleableObjectProperty<HPos>(HPos.CENTER) {

                @Override
                public void invalidated() {
                    update();
                    requestLayout();
                }

                @Override
                public CssMetaData<GeneralFlow, HPos> getCssMetaData() {
                    return GeneralFlow.StyleableProperties.COLUMN_HALIGNMENT;
                }

                @Override
                public Object getBean() {
                    return GeneralFlow.this;
                }

                @Override
                public String getName() {
                    return "columnHalignment";
                }
            };
        }
        return columnHalignment;
    }

    private ObjectProperty<HPos> columnHalignment;
    public final void setColumnHalignment(HPos value) { columnHalignmentProperty().set(value); }
    public final HPos getColumnHalignment() { return columnHalignment == null ? HPos.CENTER : columnHalignment.get(); }
    private HPos getColumnHalignmentInternal() {
        HPos localPos = getColumnHalignment();
        return localPos == null ? HPos.CENTER : localPos;
    }

    /**
     * The vertical alignment of nodes within each row of a horizontal GeneralFlow.
     * If this property is set to VPos.BASELINE, then the GeneralFlow will always
     * resize children to their preferred heights, rather than expanding heights
     * to fill the row height.
     * The property is ignored for vertical GeneralFlows.
     * @return the vertical alignment of nodes within each row of a horizontal
     * GeneralFlow
     */
    public final ObjectProperty<VPos> rowValignmentProperty() {
        if (rowValignment == null) {
            rowValignment = new StyleableObjectProperty<VPos>(VPos.BASELINE) {
                @Override
                public void invalidated() {
                    update();
                    requestLayout();
                }

                @Override
                public CssMetaData<GeneralFlow, VPos> getCssMetaData() {
                    return GeneralFlow.StyleableProperties.ROW_VALIGNMENT;
                }

                @Override
                public Object getBean() {
                    return GeneralFlow.this;
                }

                @Override
                public String getName() {
                    return "rowValignment";
                }
            };
        }
        return rowValignment;
    }

    private ObjectProperty<VPos> rowValignment;
    public final void setRowValignment(VPos value) { rowValignmentProperty().set(value); }
    public final VPos getRowValignment() { return rowValignment == null ? VPos.BASELINE : rowValignment.get(); }
    private VPos getRowValignmentInternal() {
        VPos localPos =  getRowValignment();
        return localPos == null ? VPos.BASELINE : localPos;
    }

    /* *************************************************************************
     *                                                                         *
     *                           Computing Sizes                               *
     *                                                                         *
     **************************************************************************/

    private void update() {
        List<Integer> addGap = new ArrayList<>();
        List<Integer> removeGap = new ArrayList<>();
        List<Node> nodes = getChildren();
        for (int i=0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            // validate that the node is a Cell
            if (node instanceof Cell cell) {
                if (cell.getOrientation() != getOrientation()) {
                    cell.setOrientation(getOrientation());
                }
            } else {
                if (!(node instanceof Gap)) {
                    // make sure that non-gaps are cells
                    nodes.add(i, new Cell(nodes.remove(i), this, getOrientation()));
                }
            }
            // operation of gaps
            if (getHgap() != 0) {
                if (node instanceof Gap gap) {
                    if (gap.getSpace() != getHgap()) {
                        gap.setSpace(getHgap());
                    }
                    if (gap.getOrientation() != getOrientation()) {
                        gap.setOrientation(getOrientation());
                    }
                } else {
                    if (i != nodes.size()-1) {
                        if (!(nodes.get(i+1) instanceof Gap)) {
                            addGap.add(i+1);
                        }
                    }
                }
            } else {
                if (node instanceof Gap) {
                    removeGap.add(i);
                }
            }
        }
        int diff = 0;
        if (!addGap.isEmpty()) {
            for (Integer i: addGap) {
                nodes.add(i+diff, new Gap(getHgap(), getOrientation()));
                diff++;
            }
        } else {
            for (Integer i: removeGap) {
                nodes.remove(i+diff);
            }
        }
    }

    @Override
    public Orientation getContentBias() {
        return getOrientation();
    }

    /* *************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/

    private static class StyleableProperties {
        private static final CssMetaData<GeneralFlow,Pos> ALIGNMENT =
                new CssMetaData<>("-fx-alignment",
                        new EnumConverter<>(Pos.class), Pos.TOP_LEFT) {

                    @Override
                    public boolean isSettable(GeneralFlow node) {
                        return node.alignment == null || !node.alignment.isBound();
                    }

                    @Override
                    public StyleableProperty<Pos> getStyleableProperty(GeneralFlow node) {
                        return (StyleableProperty<Pos>)node.alignmentProperty();
                    }

                };

        private static final CssMetaData<GeneralFlow,HPos> COLUMN_HALIGNMENT =
                new CssMetaData<>("-fx-column-halignment",
                        new EnumConverter<>(HPos.class), HPos.CENTER) {

                    @Override
                    public boolean isSettable(GeneralFlow node) {
                        return node.columnHalignment == null || !node.columnHalignment.isBound();
                    }

                    @Override
                    public StyleableProperty<HPos> getStyleableProperty(GeneralFlow node) {
                        return (StyleableProperty<HPos>)node.columnHalignmentProperty();
                    }

                };

        private static final CssMetaData<GeneralFlow,Number> HGAP =
                new CssMetaData<>("-fx-hgap",
                        SizeConverter.getInstance(), 0.0){

                    @Override
                    public boolean isSettable(GeneralFlow node) {
                        return node.hgap == null || !node.hgap.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(GeneralFlow node) {
                        return (StyleableProperty<Number>)node.hgapProperty();
                    }

                };

        private static final CssMetaData<GeneralFlow,VPos> ROW_VALIGNMENT =
                new CssMetaData<>("-fx-row-valignment",
                        new EnumConverter<>(VPos.class), VPos.BASELINE) {

                    @Override
                    public boolean isSettable(GeneralFlow node) {
                        return node.rowValignment == null || !node.rowValignment.isBound();
                    }

                    @Override
                    public StyleableProperty<VPos> getStyleableProperty(GeneralFlow node) {
                        return (StyleableProperty<VPos>)node.rowValignmentProperty();
                    }

                };

        private static final CssMetaData<GeneralFlow,Orientation> ORIENTATION =
                new CssMetaData<>("-fx-orientation",
                        new EnumConverter<>(Orientation.class),
                        Orientation.HORIZONTAL) {

                    @Override
                    public Orientation getInitialValue(GeneralFlow node) {
                        // A vertical flow pane should remain vertical
                        return node.getOrientation();
                    }

                    @Override
                    public boolean isSettable(GeneralFlow node) {
                        return node.orientation == null || !node.orientation.isBound();
                    }

                    @Override
                    public StyleableProperty<Orientation> getStyleableProperty(GeneralFlow node) {
                        return (StyleableProperty<Orientation>)node.orientationProperty();
                    }

                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(TextFlow.getClassCssMetaData());
            styleables.add(ALIGNMENT);
            styleables.add(COLUMN_HALIGNMENT);
            styleables.add(HGAP);
            styleables.add(ROW_VALIGNMENT);
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

    /* *************************************************************************
     *                                                                         *
     *                                 Classes                                 *
     *                                                                         *
     **************************************************************************/
}
