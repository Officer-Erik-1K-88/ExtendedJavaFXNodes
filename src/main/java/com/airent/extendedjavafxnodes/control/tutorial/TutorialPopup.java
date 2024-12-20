package com.airent.extendedjavafxnodes.control.tutorial;

import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.TooltipSkin;

public class TutorialPopup extends PopupControl {
    private final TutorialNode tutorialNode;

    public TutorialPopup(TutorialNode tutorialNode) {
        this.tutorialNode = tutorialNode;
        this.setAutoHide(false);
        this.setAutoFix(true);
    }

    public TutorialNode getTutorial() {
        return tutorialNode;
    }

    /** {@inheritDoc} */
    @Override protected Skin<?> createDefaultSkin() {
        return new TutorialPopupSkin(this);
    }
}
