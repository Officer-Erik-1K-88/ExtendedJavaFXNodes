package com.airent.extendedjavafxnodes.control.tutorial;

import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;

public class TutorialPopupSkin implements Skin<TutorialPopup> {
    /* *************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private TutorialNode<?> tutorialNode;

    private TutorialPopup tutorialPopup;



    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new TooltipSkin instance for the given {@link Tooltip}.
     * @param t the tooltip
     */
    public TutorialPopupSkin(TutorialPopup t) {
        this.tutorialPopup = t;
        tutorialNode = t.getTutorial();
        tutorialNode.minWidthProperty().bind(t.minWidthProperty());
        tutorialNode.prefWidthProperty().bind(t.prefWidthProperty());
        tutorialNode.maxWidthProperty().bind(t.maxWidthProperty());
        tutorialNode.minHeightProperty().bind(t.minHeightProperty());
        tutorialNode.prefHeightProperty().bind(t.prefHeightProperty());
        tutorialNode.maxHeightProperty().bind(t.maxHeightProperty());

        // RT-7512 - skin needs to have styleClass of the control
        // TODO - This needs to be bound together, not just set! Probably should
        // do the same for id and style as well.
        //tutorialNode.getStyleClass().setAll(t.getStyleClass());
        //tutorialNode.setStyle(t.getStyle());
        //t.setId(tutorialNode.getId());
        //tutorialNode.setId(t.getId());
    }



    /* *************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /** {@inheritDoc} */
    @Override public TutorialPopup getSkinnable() {
        return tutorialPopup;
    }

    /** {@inheritDoc} */
    @Override public Node getNode() {
        return tutorialNode;
    }

    /** {@inheritDoc} */
    @Override public void dispose() {
        tutorialNode = null;
        tutorialPopup = null;
    }
}
