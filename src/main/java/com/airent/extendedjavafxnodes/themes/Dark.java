package com.airent.extendedjavafxnodes.themes;

import javafx.scene.paint.Color;

public class Dark extends Theme {
    public Dark() {
        super(Color.BLACK, Color.WHITE, Color.PINK);
    }

    @Override
    public String getName() {
        return "DarkTheme";
    }
}
