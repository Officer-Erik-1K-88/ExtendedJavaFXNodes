package com.airent.extendedjavafxnodes.gaxml.themes;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Color {
    private final String type;
    private final Paint actual;
    private javafx.scene.paint.Color color;
    private LinearGradient linearGradient;
    private RadialGradient radialGradient;
    private ImagePattern imagePattern;

    public Color(String color) {
        this(Paint.valueOf(color));
    }

    public Color(Paint paint) {
        actual = paint;
        switch (actual) {
            case javafx.scene.paint.Color color1 -> {
                this.color = color1;
                type = "color";
            }
            case LinearGradient linearGradient1 -> {
                this.linearGradient = linearGradient1;
                type = "linear";
            }
            case RadialGradient radialGradient1 -> {
                this.radialGradient = radialGradient1;
                type = "radial";
            }
            case ImagePattern imagePattern1 -> {
                this.imagePattern = imagePattern1;
                type = "image";
            }
            case null, default -> type = "unknown";
        }
    }

    public String getType() {
        return type;
    }

    public Paint getActual() {
        return actual;
    }

    public javafx.scene.paint.Color getColor() {
        if (color == null) {
            color = paintToColor(getActual());
        }
        return color;
    }

    public LinearGradient getLinearGradient() {
        if (linearGradient == null) {
            linearGradient = paintToLinear(actual);
        }
        return linearGradient;
    }

    public RadialGradient getRadialGradient() {
        if (radialGradient == null) {
            radialGradient = paintToRadial(actual);
        }
        return radialGradient;
    }

    public ImagePattern getImagePattern() {
        if (imagePattern == null) {
            imagePattern = paintToImagePattern(actual);
        }
        return imagePattern;
    }

    public double getRed() {
        return getColor().getRed();
    }
    public double getGreen() {
        return getColor().getGreen();
    }
    public double getBlue() {
        return getColor().getBlue();
    }
    public double getOpacity() {
        return getColor().getOpacity();
    }

    public double getHue() {
        return getColor().getHue();
    }
    public double getSaturation() {
        return getColor().getSaturation();
    }
    public double getBrightness() {
        return getColor().getBrightness();
    }

    public static javafx.scene.paint.Color paintToColor(Paint paint) {
        if (paint instanceof javafx.scene.paint.Color color) {
            return color;
        } else if (paint instanceof LinearGradient linearGradient) {
            return linearToColor(linearGradient);
        } else if (paint instanceof RadialGradient radialGradient) {
            return radialToColor(radialGradient);
        } else if (paint instanceof ImagePattern imagePattern) {
            return imageToColor(imagePattern.getImage());
        }
        return null;
    }
    public static LinearGradient paintToLinear(Paint paint) {
        if (paint instanceof javafx.scene.paint.Color color) {
            return colorToLinear(color, 4, false, true, CycleMethod.NO_CYCLE);
        } else if (paint instanceof LinearGradient linearGradient) {
            return linearGradient;
        } else if (paint instanceof RadialGradient radialGradient) {
            return radialToLinear(radialGradient);
        } else if (paint instanceof ImagePattern imagePattern) {
            return imageToLinear(imagePattern.getImage(), imagePattern.isProportional(), CycleMethod.NO_CYCLE);
        }
        return null;
    }

    public static RadialGradient paintToRadial(Paint paint) {
        if (paint instanceof javafx.scene.paint.Color color) {
            return colorToRadial(color, 4, false, true, CycleMethod.NO_CYCLE);
        } else if (paint instanceof LinearGradient linearGradient) {
            return linearToRadial(linearGradient);
        } else if (paint instanceof RadialGradient radialGradient) {
            return radialGradient;
        } else if (paint instanceof ImagePattern imagePattern) {
            return imageToRadial(imagePattern.getImage(), imagePattern.isProportional(), CycleMethod.NO_CYCLE);
        }
        return null;
    }

    public static ImagePattern paintToImagePattern(Paint paint) {
        if (paint instanceof ImagePattern imagePattern) return imagePattern;
        Rectangle rectangle = new Rectangle(200, 200);
        rectangle.setFill(paint);
        rectangle.setStroke(null);
        rectangle.setSmooth(true);
        rectangle.setVisible(true);
        WritableImage image = new WritableImage((int) rectangle.getWidth(), (int) rectangle.getHeight());
        SnapshotParameters parameters = new SnapshotParameters();

        rectangle.snapshot(parameters, image);

        return new ImagePattern(image, rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), true);
    }

    public static LinearGradient radialToLinear(@NotNull RadialGradient radialGradient) {
        double startX = radialGradient.getCenterX();
        double startY = radialGradient.getCenterY();
        double endX = startX + radialGradient.getRadius();
        double endY = startY + radialGradient.getRadius();
        return new LinearGradient(
                startX,
                startY,
                endX,
                endY,
                radialGradient.isProportional(),
                radialGradient.getCycleMethod(),
                radialGradient.getStops());
    }

    public static RadialGradient linearToRadial(LinearGradient linearGradient) {
        return linearToRadial(linearGradient, 0, 0);
    }
    public static RadialGradient linearToRadial(LinearGradient linearGradient, double focusAngle, double focusDistance) {
        double centerX = linearGradient.getStartX();
        double centerY = linearGradient.getStartY();

        double x = linearGradient.getEndX() - centerX;
        double y = linearGradient.getEndY() - centerY;

        double radius = (x+y)/2.0;
        if (radius <= 0) radius = 1;
        return new RadialGradient(
                focusAngle,
                focusDistance,
                centerX,
                centerY,
                radius,
                linearGradient.isProportional(),
                linearGradient.getCycleMethod(),
                linearGradient.getStops()
        );
    }

    public static javafx.scene.paint.Color linearToColor(LinearGradient linearGradient) {
        List<Stop> stops = linearGradient.getStops();
        javafx.scene.paint.Color color = stops.getFirst().getColor();
        if (stops.size() <= 4) {
            for (int i=1; i<stops.size(); i++) {
                color = mix(color, stops.get(i).getColor());
            }
        } else {
            color = mix(color, stops.getLast().getColor());
            color = mix(color, stops.get((stops.size()/2)-1).getColor());
        }
        return color;
    }

    public static javafx.scene.paint.Color radialToColor(RadialGradient radialGradient) {
        List<Stop> stops = radialGradient.getStops();
        javafx.scene.paint.Color color = stops.getFirst().getColor();
        if (stops.size() <= 4) {
            for (int i=1; i<stops.size(); i++) {
                color = mix(color, stops.get(i).getColor());
            }
        } else {
            color = mix(color, stops.getLast().getColor());
            color = mix(color, stops.get((stops.size()/2)-1).getColor());
        }
        return color;
    }

    public static javafx.scene.paint.Color imageToColor(Image image) {
        List<javafx.scene.paint.Color> colors = imageColors(image);
        javafx.scene.paint.Color color = colors.getFirst();
        if (colors.size() <= 4) {
            for (int i=1; i<colors.size(); i++) {
                color = mix(color, colors.get(i));
            }
        } else {
            color = mix(color, colors.getLast());
            color = mix(color, colors.get((colors.size()/2)-1));
        }
        return color;
    }

    public static LinearGradient colorToLinear(javafx.scene.paint.Color color, int stopCount, boolean brighten, boolean proportional, CycleMethod cycleMethod) {
        return new LinearGradient(
                0,
                0,
                1,
                1,
                proportional,
                cycleMethod,
                colorStops(color, stopCount, brighten)
        );
    }

    public static RadialGradient colorToRadial(javafx.scene.paint.Color color, int stopCount, boolean brighten, boolean proportional, CycleMethod cycleMethod) {
        return new RadialGradient(
                0,
                0,
                0,
                0,
                1,
                proportional,
                cycleMethod,
                colorStops(color, stopCount, brighten)
        );
    }

    public static LinearGradient imageToLinear(Image image, boolean proportional, CycleMethod cycleMethod) {
        return new LinearGradient(
                0,
                0,
                1,
                1,
                proportional,
                cycleMethod,
                imageStops(image)
        );
    }

    public static RadialGradient imageToRadial(Image image, boolean proportional, CycleMethod cycleMethod) {
        return new RadialGradient(
                0,
                0,
                0,
                0,
                1,
                proportional,
                cycleMethod,
                imageStops(image)
        );
    }

    public static List<Stop> colorStops(javafx.scene.paint.Color toMakeAsStops, int stopCount, boolean brighten) {
        List<Stop> stops = new ArrayList<>();
        double saturationFactor = 0.9;
        double brightnessFactor = 0.7;
        if (brighten) {
            saturationFactor = 1.0 / saturationFactor;
            brightnessFactor = 1.0 / brightnessFactor;
        }

        javafx.scene.paint.Color color = toMakeAsStops;
        double offset = 1.0/stopCount;
        for (int i=0; i<stopCount; i++) {
            stops.add(new Stop(offset*i, color));
            color = color.deriveColor(0, saturationFactor, brightnessFactor, 1);
        }

        return stops;
    }

    public static List<Stop> imageStops(Image image) {
        List<Stop> stops = new ArrayList<>();
        List<javafx.scene.paint.Color> colors = imageColors(image);
        double offset = 1.0/colors.size();
        for (int i=0; i<colors.size(); i++) {
            stops.add(new Stop(offset*i, colors.get(i)));
        }
        return stops;
    }

    public static List<javafx.scene.paint.Color> imageColors(Image image) {
        List<javafx.scene.paint.Color> colors = new ArrayList<>();
        PixelReader reader = image.getPixelReader();
        double height = image.getHeight();
        double width = image.getWidth();
        int xInc = (int) (height/200);
        int yInc = (int) (width/200);
        if (xInc == 0) xInc = 1;
        if (yInc == 0) yInc = 1;
        for (int x=0; x < width; x += xInc) {
            for (int y=0; y < height; y += yInc) {
                javafx.scene.paint.Color color = reader.getColor(x, y);
                if (!colors.contains(color)) {
                    colors.add(color);
                }
            }
        }
        return colors;
    }

    public static Paint mix(Paint paint1, Paint paint2) {
        return mix(new Color(paint1), new Color(paint2)).getActual();
    }

    public static Color mix(Color color1, Paint paint2) {
        return mix(color1, new Color(paint2));
    }

    public static Color mix(Paint paint1, Color color2) {
        return mix(new Color(paint1), color2);
    }

    public static Color mix(@NotNull Color color1, @NotNull Color color2) {
        String type1 = color1.getType();
        String type2 = color2.getType();
        if (type1.equals(type2)) {
            return switch (type1) {
                case "linear" -> new Color(mix(color1.getLinearGradient(), color2.getLinearGradient()));
                case "radial" -> new Color(mix(color1.getRadialGradient(), color2.getRadialGradient()));
                case "image" -> new Color(mix(color1.getImagePattern(), color2.getImagePattern()));
                default -> new Color(mix(color1.getColor(), color2.getColor()));
            };
        }
        if (type1.equals("radial") || type2.equals("radial")) {
            return new Color(mix(color1.getRadialGradient(), color2.getRadialGradient()));
        } else if (type1.equals("linear") || type2.equals("linear")) {
            return new Color(mix(color1.getLinearGradient(), color2.getLinearGradient()));
        } else if (type1.equals("color") || type2.equals("color")) {
            return new Color(mix(color1.getColor(), color2.getColor()));
        }
        return new Color(mix(color1.getImagePattern(), color2.getImagePattern()));
    }

    public static LinearGradient mix(LinearGradient linearGradient1, LinearGradient linearGradient2) {
        double startX = (linearGradient1.getStartX()+linearGradient2.getStartX())/2;
        double startY = (linearGradient1.getStartY()+linearGradient2.getStartY())/2;
        double endX = (linearGradient1.getEndX()+linearGradient2.getEndX())/2;
        double endY = (linearGradient1.getEndY()+linearGradient2.getEndY())/2;
        CycleMethod cycleMethod = getCycleMethod(linearGradient1.getCycleMethod(), linearGradient2.getCycleMethod());
        List<Stop> stops1 = linearGradient1.getStops();
        List<Stop> stops2 = linearGradient2.getStops();
        List<Stop> stops = new ArrayList<>();
        if (stops1.size() == stops2.size()) {
            for (int i=0; i<stops1.size(); i++) {
                Stop stop1 = stops1.get(i);
                Stop stop2 = stops2.get(i);
                stops.add(new Stop(
                        (stop1.getOffset()+stop2.getOffset())/2,
                        mix(stop1.getColor(), stop2.getColor())
                ));
            }
        } else if (stops1.size() < stops2.size()) {
            int j = 0;
            for (Stop stop2 : stops2) {
                if (j == stops1.size()) j = 0;
                Stop stop1 = stops1.get(j);
                stops.add(new Stop(
                        (stop1.getOffset() + stop2.getOffset()) / 2,
                        mix(stop1.getColor(), stop2.getColor())
                ));
                j++;
            }
        } else {
            int j = 0;
            for (Stop stop1 : stops1) {
                if (j == stops2.size()) j = 0;
                Stop stop2 = stops2.get(j);
                stops.add(new Stop(
                        (stop1.getOffset() + stop2.getOffset()) / 2,
                        mix(stop1.getColor(), stop2.getColor())
                ));
                j++;
            }
        }
        return new LinearGradient(
                startX,
                startY,
                endX,
                endY,
                linearGradient1.isProportional() && linearGradient2.isProportional(),
                cycleMethod,
                stops
        );
    }

    public static RadialGradient mix(RadialGradient radialGradient1, RadialGradient radialGradient2) {
        double focusAngle = (radialGradient1.getFocusAngle()+radialGradient2.getFocusAngle())/2;
        double focusDistance = (radialGradient1.getFocusDistance()+radialGradient2.getFocusDistance())/2;
        double centerX = (radialGradient1.getCenterX()+radialGradient2.getCenterX())/2;
        double centerY = (radialGradient1.getCenterY()+radialGradient2.getCenterY())/2;
        double radius = (radialGradient1.getRadius()+radialGradient2.getRadius())/2;
        CycleMethod cycleMethod = getCycleMethod(radialGradient1.getCycleMethod(), radialGradient2.getCycleMethod());
        List<Stop> stops1 = radialGradient1.getStops();
        List<Stop> stops2 = radialGradient2.getStops();
        List<Stop> stops = new ArrayList<>();
        if (stops1.size() == stops2.size()) {
            for (int i=0; i<stops1.size(); i++) {
                Stop stop1 = stops1.get(i);
                Stop stop2 = stops2.get(i);
                stops.add(new Stop(
                        (stop1.getOffset()+stop2.getOffset())/2,
                        mix(stop1.getColor(), stop2.getColor())
                ));
            }
        } else if (stops1.size() < stops2.size()) {
            int j = 0;
            for (Stop stop2 : stops2) {
                if (j == stops1.size()) j = 0;
                Stop stop1 = stops1.get(j);
                stops.add(new Stop(
                        (stop1.getOffset() + stop2.getOffset()) / 2,
                        mix(stop1.getColor(), stop2.getColor())
                ));
                j++;
            }
        } else {
            int j = 0;
            for (Stop stop1 : stops1) {
                if (j == stops2.size()) j = 0;
                Stop stop2 = stops2.get(j);
                stops.add(new Stop(
                        (stop1.getOffset() + stop2.getOffset()) / 2,
                        mix(stop1.getColor(), stop2.getColor())
                ));
                j++;
            }
        }
        return new RadialGradient(
                focusAngle,
                focusDistance,
                centerX,
                centerY,
                radius,
                radialGradient1.isProportional() && radialGradient2.isProportional(),
                cycleMethod,
                stops
        );
    }

    public static ImagePattern mix(ImagePattern imagePattern1, ImagePattern imagePattern2) {
        Image image1 = imagePattern1.getImage();
        Image image2 = imagePattern2.getImage();

        double width = (image1.getWidth()+image2.getWidth())/2;
        double height = (image1.getHeight()+image2.getHeight())/2;
        double x = (imagePattern1.getX()+imagePattern2.getX())/2;
        double y = (imagePattern1.getY()+imagePattern2.getY())/2;

        int forthOfWidth = (int) (width/4);
        int forthOfHeight = (int) (height/4);

        WritableImage image = new WritableImage((int) width, (int) height);
        PixelWriter writer = image.getPixelWriter();
        PixelReader reader1 = image1.getPixelReader();
        PixelReader reader2 = image2.getPixelReader();

        if (height <= 1000 || width <= 1000) {
            boolean first = true;
            int ri = 0;
            int rj;
            int argb;
            for (int i = 0; i < height; i++) {
                rj = 0;
                for (int j = 0; j < width; j++) {
                    if (first) {
                        first = false;
                        if (ri >= image1.getHeight()) ri = 0;
                        if (rj >= image1.getWidth()) rj = 0;
                        argb = reader1.getArgb(rj, ri);
                    } else {
                        if (ri >= image2.getHeight()) ri = 0;
                        if (rj >= image2.getWidth()) rj = 0;
                        argb = reader2.getArgb(rj, ri);
                        first = true;
                    }
                    writer.setArgb(j, i, argb);
                    rj++;
                }
                ri++;
            }
        } else {
            int srcXForOne = Math.abs((int)(image1.getWidth()-forthOfWidth));
            int srcYForOne = Math.abs((int)(image1.getHeight()-forthOfHeight));

            int srcXForTwo = Math.abs((int)(image2.getWidth()-forthOfWidth));
            int srcYForTwo = Math.abs((int)(image2.getHeight()-forthOfHeight));

            // from image pattern one
            image.getPixelWriter().setPixels(0, 0,
                    forthOfWidth, forthOfHeight,
                    image1.getPixelReader(),
                    0, 0);
            image.getPixelWriter().setPixels(forthOfWidth, forthOfHeight,
                    forthOfWidth, forthOfHeight,
                    image1.getPixelReader(),
                    srcXForOne,
                    srcYForOne);

            // from image pattern two
            image.getPixelWriter().setPixels(0, forthOfHeight,
                    forthOfWidth, forthOfHeight,
                    image2.getPixelReader(),
                    0, 0);
            image.getPixelWriter().setPixels(forthOfWidth, 0,
                    forthOfWidth, forthOfHeight,
                    image2.getPixelReader(),
                    srcXForTwo,
                    srcYForTwo);
        }

        return new ImagePattern(image, x, y,
                (imagePattern1.getWidth()+imagePattern2.getWidth())/2,
                (imagePattern1.getHeight()+imagePattern2.getHeight())/2,
                imagePattern1.isProportional() && imagePattern2.isProportional());
    }

    private static CycleMethod getCycleMethod(CycleMethod cycleMethod1, CycleMethod cycleMethod2) {
        CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
        if (cycleMethod1.equals(cycleMethod2)) {
            cycleMethod = cycleMethod1;
        } else if (cycleMethod1.equals(CycleMethod.REFLECT)) {
            if (cycleMethod2.equals(CycleMethod.NO_CYCLE)) {
                cycleMethod = CycleMethod.REPEAT;
            }
        } else if (cycleMethod1.equals(CycleMethod.REPEAT)) {
            if (cycleMethod2.equals(CycleMethod.NO_CYCLE)) {
                cycleMethod = CycleMethod.REFLECT;
            }
        } else if (cycleMethod1.equals(CycleMethod.NO_CYCLE)) {
            cycleMethod = cycleMethod2;
        }
        return cycleMethod;
    }

    public static javafx.scene.paint.Color mix(javafx.scene.paint.Color color1, javafx.scene.paint.Color color2) {
        return mix(color1, color2, false);
    }

    private static javafx.scene.paint.Color mix(@NotNull javafx.scene.paint.Color color1, @NotNull javafx.scene.paint.Color color2, boolean supposedSame) {
        double hue = (color1.getHue()+color2.getHue())/2;
        double brightness = (color1.getBrightness()+color2.getBrightness())/2;
        double saturation = (color1.getSaturation()+color2.getSaturation())/2;
        double red = (color1.getRed()+color2.getRed())/2;
        double green = (color1.getGreen()+color2.getGreen())/2;
        double blue = (color1.getBlue()+color2.getBlue())/2;
        double opacity = (color1.getOpacity()+color2.getOpacity())/2;
        javafx.scene.paint.Color c1 = javafx.scene.paint.Color.color(red, green, blue, opacity);
        javafx.scene.paint.Color c2 = javafx.scene.paint.Color.hsb(hue, saturation, brightness, opacity);
        if (supposedSame) {
            if (colorsSame(c1, c2)) {
                return c1;
            }
            return javafx.scene.paint.Color.hsb((c1.getHue()+c2.getHue())/2,
                    (c1.getSaturation()+c2.getSaturation())/2,
                    (c1.getBrightness()+c2.getBrightness())/2,
                    opacity);
        }
        return mix(c1, c2, true);
    }

    public static boolean colorsSame(@NotNull javafx.scene.paint.Color color1, @NotNull javafx.scene.paint.Color color2) {
        if (color1.equals(color2)) {
            return true;
        }
        if (color1.getOpacity() == color2.getOpacity()) {
            if (color1.getHue() == color2.getHue()) {
                if (color1.getSaturation() == color2.getSaturation()) {
                    if (color1.getBrightness() == color2.getBrightness()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @NotNull
    public static String asRGB(@NotNull javafx.scene.paint.Color color) {
        return "rgba("+(color.getRed()*255)+","+(color.getGreen()*255)+","+(color.getBlue()*255)+","+color.getOpacity()+")";
    }
}
