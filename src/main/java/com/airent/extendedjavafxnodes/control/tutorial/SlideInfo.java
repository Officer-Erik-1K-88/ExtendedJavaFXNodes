package com.airent.extendedjavafxnodes.control.tutorial;

import com.airent.extendedjavafxnodes.gaxml.themes.Theme;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.util.List;

public interface SlideInfo {
    String getTitle();
    Image getImage();
    Theme getTheme();
    Node getLinkedNode();
    List<Node> getDescription();
}
