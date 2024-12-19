package com.airent.extendedjavafxnodes.shape;

import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

public class Arrow extends Path {
    private static final double defaultArrowHeadSize = 5.0;
    private static final double defaultSize = 4.0;

    public Arrow(double startX, double startY, double endX, double endY, double size, double arrowHeadSize) {
        super();
        setStroke(Color.BLACK);
        setFill(Color.BLACK);

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

    private double keepSteadySE(double s, double e, double size) {
        return s+(Math.abs(s)==size?(s<0?-4+e:4+e):0);
    }
}
