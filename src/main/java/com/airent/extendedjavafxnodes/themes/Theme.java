package com.airent.extendedjavafxnodes.themes;

import com.airent.extendedjavafxnodes.utils.Named;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

public abstract class Theme implements Named {
    private final Color primary;
    private final Color secondary;
    private final Color tertiary;
    
    protected Theme(Color primary, Color secondary, Color tertiary) {
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
    }

    public final Color getPrimary() {
        return primary;
    }

    public final Color getSecondary() {
        return secondary;
    }

    public final Color getTertiary() {
        return tertiary;
    }

    public Color mixPrimary(Color color) {
        return mix(primary, color);
    }
    public Color mixSecondary(Color color) {
        return mix(secondary, color);
    }
    public Color mixTertiary(Color color) {
        return mix(tertiary, color);
    }

    public static Color mix(Color color1, Color color2) {
        return mix(color1, color2, false);
    }

    private static Color mix(@NotNull Color color1, @NotNull Color color2, boolean supposedSame) {
        double hue = (color1.getHue()+color2.getHue())/2;
        double brightness = (color1.getBrightness()+color2.getBrightness())/2;
        double saturation = (color1.getSaturation()+color2.getSaturation())/2;
        double red = (color1.getRed()+color2.getRed())/2;
        double green = (color1.getGreen()+color2.getGreen())/2;
        double blue = (color1.getBlue()+color2.getBlue())/2;
        double opacity = (color1.getOpacity()+color2.getOpacity())/2;
        Color c1 = Color.color(red, green, blue, opacity);
        Color c2 = Color.hsb(hue, saturation, brightness, opacity);
        if (supposedSame) {
            if (colorsSame(c1, c2)) {
                return c1;
            }
            return Color.hsb((c1.getHue()+c2.getHue())/2,
                    (c1.getSaturation()+c2.getSaturation())/2,
                    (c1.getBrightness()+c2.getBrightness())/2,
                    opacity);
        }
        return mix(c1, c2, true);
    }

    public static boolean colorsSame(@NotNull Color color1, @NotNull Color color2) {
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
    public static String asRGB(@NotNull Color color) {
        return "rgba("+(color.getRed()*255)+","+(color.getGreen()*255)+","+(color.getBlue()*255)+","+color.getOpacity()+")";
    }
}
