package com.airent.extendedjavafxnodes.control;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Paint;

public class TutorialScene extends Scene {
    private void build() {
        rootProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue instanceof TutorialContent)) {
                throw new RuntimeException("The root of a TutorialScene must be an instance of TutorialContent.");
            }
        });
    }

    public TutorialScene(TutorialContent root) {
        super(root);
        build();
    }

    public TutorialScene(TutorialContent root, double width, double height) {
        super(root, width, height);
        build();
    }

    public TutorialScene(TutorialContent root, Paint fill) {
        super(root, fill);
        build();
    }

    public TutorialScene(TutorialContent root, double width, double height, Paint fill) {
        super(root, width, height, fill);
        build();
    }

    public TutorialScene(TutorialContent root, double width, double height, boolean depthBuffer) {
        super(root, width, height, depthBuffer);
        build();
    }

    public TutorialScene(TutorialContent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing) {
        super(root, width, height, depthBuffer, antiAliasing);
        build();
    }
}
