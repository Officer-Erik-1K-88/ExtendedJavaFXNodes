package com.airent.extendedjavafxnodes.control;

import javafx.scene.control.PopupControl;

public class TutorialWindow extends PopupControl {
    public TutorialWindow() {
        super();
        this.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue instanceof TutorialScene)) {
                throw new RuntimeException("The scene of a TutorialWindow must be an instance of TutorialScene.");
            }
        });
    }

    /* *************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/
}
