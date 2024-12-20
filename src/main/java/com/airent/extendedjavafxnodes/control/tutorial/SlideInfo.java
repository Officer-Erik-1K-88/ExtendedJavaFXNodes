package com.airent.extendedjavafxnodes.control.tutorial;

import com.airent.extendedjavafxnodes.gaxml.themes.Theme;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.util.List;

public interface SlideInfo {
    /**
     * Gets the name associated with this slide.
     *
     * @return The name of this slide.
     */
    String getTitle();

    /**
     * Gets the image of the linked node for a more
     * visual approach to describing the node.
     *
     * @return An image or null if there is no image.
     */
    Image getImage();

    /**
     * Gets and returns the {@link Theme} that is
     * to be used when displaying this slide.
     *
     * @return The theme that best fits this slide.
     */
    Theme getTheme();

    /**
     * Gets and returns the node linked to this slide,
     * so as to display this slide around said
     * node.
     *
     * @return The linked node.
     */
    Node getLinkedNode();

    /**
     * Gets and returns a list of nodes that will
     * be used as the displayed content of this
     * slide's description.
     *
     * @return A list of nodes that make up the description of this slide.
     */
    List<Node> getDescription();
}
