package com.airent.extendedjavafxnodes.shape;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.ArrayList;

public class Arrow extends Path {
    private static final double defaultArrowHeadSize = 5.0;
    private static final double defaultSize = 4.0;

    public Arrow(double startX, double startY, double endX, double endY, double size, double arrowHeadSize) {
        super();
        setStroke(Color.BLACK);
        setFill(Color.BLACK);

        update(startX, startY, endX, endY, size, arrowHeadSize);
    }

    public Arrow(double startX, double startY, double endX, double endY, double size){
        this(startX, startY, endX, endY, size, defaultArrowHeadSize);
    }

    public Arrow(double startX, double startY, double endX, double endY) {
        this(startX, startY, endX, endY, defaultSize);
    }

    public Arrow(double width, double size) {
        this(0, 0, width, 0, size);
    }

    public void update(double startX, double startY, double endX, double endY, double size, double arrowHeadSize) {
        this.startX.noFireSet(startX);
        this.startY.noFireSet(startY);
        this.endX.noFireSet(endX);
        this.endY.noFireSet(endY);
        this.size.noFireSet(size);
        this.arrowHeadSize.noFireSet(arrowHeadSize);

        build();
    }

    private void build() {
        double startX = getStartX();
        double startY = getStartY();
        double endX = getEndX();
        double endY = getEndY();
        double size = getSize();
        double arrowHeadSize = getArrowHeadSize();

        getElements().clear();

        if (size == 0) {
            // Width
            arrowHeadSize += size;
            if (endX != 0) {
                endX += size;
            }
            if (endY != 0) {
                endY += size;
            }

            //Line
            getElements().add(new MoveTo(startX, startY));
            getElements().add(new LineTo(endX, endY));

            //ArrowHead
            double angle = Math.atan2((endY - startY), (endX - startX)) - Math.PI / 2.0;
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            //point1
            double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
            double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;
            //point2
            double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
            double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;

            getElements().add(new LineTo(x1, y1));
            getElements().add(new LineTo(x2, y2));
            getElements().add(new LineTo(endX, endY));
        } else {
            // Keep all steady and prevent gaps
            if (endY == 0 && endX != 0) {
                endX = keepSteadySE(endX, startX, size);
            } else if (endX == 0 && endY != 0) {
                endY = keepSteadySE(endY, startY, size);
            } else {
                if (startY == 0 && startX != 0) {
                    startX = keepSteadySE(startX, endX, size);
                } else if (startX == 0 && startY != 0) {
                    startY = keepSteadySE(startY, endY, size);
                }
            }

            arrowHeadSize += size;

            // Adjust the thick line's thickness based on the arrowhead size
            size = Math.min(size, (arrowHeadSize / 2));

            // Calculate the angle of the main arrow line
            double angle = Math.atan2((endY - startY), (endX - startX));

            // Calculate offsets for the rectangle (thick line)
            double offsetX = size / 2 * Math.sin(angle);
            double offsetY = size / 2 * Math.cos(angle);

            // Points for the rectangle
            double rectStartLeftX = startX - offsetX;
            double rectStartLeftY = startY + offsetY;
            double rectStartRightX = startX + offsetX;
            double rectStartRightY = startY - offsetY;

            double rectEndLeftX = endX - Math.cos(angle) * arrowHeadSize - offsetX;
            double rectEndLeftY = endY - Math.sin(angle) * arrowHeadSize + offsetY;
            double rectEndRightX = endX - Math.cos(angle) * arrowHeadSize + offsetX;
            double rectEndRightY = endY - Math.sin(angle) * arrowHeadSize - offsetY;

            // Create the rectangle (thick line)
            getElements().add(new MoveTo(rectStartLeftX, rectStartLeftY));
            getElements().add(new LineTo(rectEndLeftX, rectEndLeftY));
            getElements().add(new LineTo(rectEndRightX, rectEndRightY));
            getElements().add(new LineTo(rectStartRightX, rectStartRightY));
            getElements().add(new LineTo(rectStartLeftX, rectStartLeftY)); // Close the rectangle

            // Points for the triangle (arrowhead)
            double arrowTipX = endX;
            double arrowTipY = endY;

            double arrowLeftX = endX - Math.cos(angle - Math.PI / 6) * arrowHeadSize;
            double arrowLeftY = endY - Math.sin(angle - Math.PI / 6) * arrowHeadSize;

            double arrowRightX = endX - Math.cos(angle + Math.PI / 6) * arrowHeadSize;
            double arrowRightY = endY - Math.sin(angle + Math.PI / 6) * arrowHeadSize;

            // Create the triangle (arrowhead)
            getElements().add(new MoveTo(rectEndLeftX, rectEndLeftY)); // Connect to rectangle
            getElements().add(new LineTo(arrowLeftX, arrowLeftY)); // Left side of the arrowhead
            getElements().add(new LineTo(arrowTipX, arrowTipY)); // Tip of the arrowhead
            getElements().add(new LineTo(arrowRightX, arrowRightY)); // Right side of the arrowhead
            getElements().add(new LineTo(rectEndRightX, rectEndRightY)); // Connect back to rectangle
            getElements().add(new LineTo(rectEndLeftX, rectEndLeftY)); // Close the arrowhead
        }
        getElements().add(new ClosePath());

        this.startX.noFireSet(startX);
        this.startY.noFireSet(startY);
        this.endX.noFireSet(endX);
        this.endY.noFireSet(endY);
        this.size.noFireSet(size);
        this.arrowHeadSize.noFireSet(arrowHeadSize);
    }

    private double keepSteadySE(double s, double e, double size) {
        return s+(Math.abs(s)==size?(s<0?-4+e:4+e):0);
    }

    /* *************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    private final Property startX = new Property() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "startX";
        }

        @Override
        protected void invalidate() {
            build();
        }
    };

    public final DoubleProperty startXProperty() {
        return startX;
    }

    public final double getStartX() {
        return startX.get();
    }
    public final void setStartX(double value) {
        startX.set(value);
    }

    private final Property startY = new Property() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "startY";
        }

        @Override
        protected void invalidate() {
            build();
        }
    };

    public DoubleProperty startYProperty() {
        return startY;
    }
    public final double getStartY() {
        return startY.get();
    }
    public final void setStartY(double value) {
        startY.set(value);
    }

    private final Property endX = new Property() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "endX";
        }

        @Override
        protected void invalidate() {
            build();
        }
    };

    public final DoubleProperty endXProperty() {
        return endX;
    }
    public final double getEndX() {
        return endX.get();
    }
    public final void setEndX(double value) {
        endX.set(value);
    }

    private final Property endY = new Property() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "endY";
        }

        @Override
        protected void invalidate() {
            build();
        }
    };

    public final DoubleProperty endYProperty() {
        return endY;
    }
    public final double getEndY() {
        return endY.get();
    }
    public final void setEndY(double value) {
        endY.set(value);
    }

    private final Property size = new Property() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "size";
        }

        @Override
        protected void invalidate() {
            build();
        }
    };

    public final DoubleProperty sizeProperty() {
        return size;
    }
    public final double getSize() {
        return size.get();
    }
    public final void setSize(double value) {
        size.set(value);
    }

    private final Property arrowHeadSize = new Property() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "arrowHeadSize";
        }

        @Override
        protected void invalidate() {
            build();
        }
    };

    public final DoubleProperty arrowHeadSizeProperty() {
        return arrowHeadSize;
    }
    public final double getArrowHeadSize() {
        return arrowHeadSize.get();
    }
    public final void setArrowHeadSize(double value) {
        arrowHeadSize.set(value);
    }

    private abstract static class Property extends DoublePropertyBase {
        private boolean isSkip = false;
        private final ArrayList<InvalidationListener> invalidationListeners = new ArrayList<>();
        private final ArrayList<ChangeListener<? super Number>> changeListeners = new ArrayList<>();
        @Override
        protected final void invalidated() {
            if (!isSkip) {
                invalidate();
            }
        }

        protected void invalidate() {}

        public final void noFireSet(double value) {
            isSkip = true;
            for (InvalidationListener invalidationListener : invalidationListeners) {
                super.removeListener(invalidationListener);
            }
            for (ChangeListener<? super Number> changeListener : changeListeners) {
                super.removeListener(changeListener);
            }
            set(value);
            isSkip = false;
            for (InvalidationListener invalidationListener : invalidationListeners) {
                super.addListener(invalidationListener);
            }
            for (ChangeListener<? super Number> changeListener : changeListeners) {
                super.addListener(changeListener);
            }
        }

        @Override
        public void addListener(InvalidationListener listener) {
            super.addListener(listener);
            invalidationListeners.add(listener);
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            super.removeListener(listener);
            invalidationListeners.remove(listener);
        }

        @Override
        public void addListener(ChangeListener<? super Number> listener) {
            super.addListener(listener);
            changeListeners.add(listener);
        }

        @Override
        public void removeListener(ChangeListener<? super Number> listener) {
            super.removeListener(listener);
            changeListeners.remove(listener);
        }
    }
}
