package com.airent.extendedjavafxnodes.control.tutorial;

import com.airent.extendedjavafxnodes.gaxml.Attributes;
import com.airent.extendedjavafxnodes.gaxml.XMLProcessor;
import com.airent.extendedjavafxnodes.gaxml.themes.Theme;
import com.airent.extendedjavafxnodes.utils.Pair;
import javafx.beans.property.DoublePropertyBase;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;

public class TutorialInfo implements SlideInfo {
    private final String title;
    private Image image;
    private final URL url;
    private Theme theme;

    private final Node linkedNode;

    public TutorialInfo(String title, Image image, URL url, Theme theme, Node linkedNode) {
        this.title = title;
        this.image = image;
        this.url = url;
        this.theme = theme;
        this.linkedNode = linkedNode;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Gets and returns the url that represents the location
     * of an XML file that can be parsed as {@code GAXML}.
     *
     * @return The url to the {@code GAXML} formatted XML file.
     */
    public URL getURL() {
        return url;
    }

    @Override
    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    @Override
    public Node getLinkedNode() {
        return linkedNode;
    }

    /**
     * Parses and returns the {@code GAXML} file that was used
     * to describe this slide's description.
     *
     * @return The list of nodes that make up the description of this slide.
     */
    @Override
    public List<Node> getDescription() {
        XMLProcessor processor = new XMLProcessor(getURL());
        processor.setDefaultWidth(380);
        processor.setTheme(getTheme());
        return processor.load(new Attributes(new Pair<>("align", "top_center")));
    }
}
