package com.airent.extendedjavafxnodes.gaxml.themes;

import javafx.scene.paint.Color;

public class Light extends Theme {
    public Light() {
        super(Color.WHITE, Color.BLACK, Color.DODGERBLUE);
    }

    @Override
    public String getName() {
        return "LightTheme";
    }
}