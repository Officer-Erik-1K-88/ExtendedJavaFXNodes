package com.airent.extendedjavafxnodes.gaxml.themes;

import com.airent.extendedjavafxnodes.utils.Named;
import javafx.scene.paint.Paint;
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

    protected Theme(Paint primary, Paint secondary, Paint tertiary) {
        this.primary = new Color(primary);
        this.secondary = new Color(secondary);
        this.tertiary = new Color(tertiary);
    }

    public final Paint getPrimary() {
        return primary.getActual();
    }

    public final Paint getSecondary() {
        return secondary.getActual();
    }

    public final Paint getTertiary() {
        return tertiary.getActual();
    }

    public final Color getPrimeColor() {
        return primary;
    }
    public final Color getSecondColor() {
        return secondary;
    }
    public final Color getThirdColor() {
        return tertiary;
    }

    public Color mixPrimary(Color color) {
        return Color.mix(primary, color);
    }
    public Color mixSecondary(Color color) {
        return Color.mix(secondary, color);
    }
    public Color mixTertiary(Color color) {
        return Color.mix(tertiary, color);
    }

    public Color mixPrimary(Paint paint) {
        return Color.mix(primary, paint);
    }
    public Color mixSecondary(Paint paint) {
        return Color.mix(secondary, paint);
    }
    public Color mixTertiary(Paint paint) {
        return Color.mix(tertiary, paint);
    }

    /* *************************************************************************
     *                                                                         *
     * Themes                                                                  *
     *                                                                         *
     **************************************************************************/

}
