package com.airent.extendedjavafxnodes.control.tutorial;

import com.airent.extendedjavafxnodes.gaxml.Attributes;
import com.airent.extendedjavafxnodes.gaxml.XMLProcessor;
import com.airent.extendedjavafxnodes.gaxml.themes.Theme;
import com.airent.extendedjavafxnodes.utils.ListMap;
import com.airent.extendedjavafxnodes.utils.Pair;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;

public class TutorialContent {
    private final ImageView imageView;
    private final VBox textContent;
    private final Button next;
    private final Button previous;
    private final Label flowCounter;

    Runnable onClose = null;
    Runnable onChange = null;

    private int current = 0;

    private final ListMap<String, TutorialInfo> info = new ListMap<>();
    private final List<Node> nodes;

    public TutorialContent() {

        imageView = new ImageView();

        textContent = new VBox();
        textContent.setFillWidth(true);
        textContent.setAlignment(Pos.TOP_CENTER);
        textContent.setPrefWidth(300);
        textContent.setMaxWidth(300);

        previous = new Button("Previous");
        previous.setDisable(true);
        previous.setOnAction(this::proceedToPrevious);
        flowCounter = new Label("0 / 0");
        next = new Button("Next");
        next.setText("Close");
        next.setOnAction(this::proceedToNext);

        HBox hBox = new HBox(previous, flowCounter, next);
        hBox.setFillHeight(true);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setMinHeight(16);
        separator.setPrefHeight(30);

        nodes = List.of(imageView, textContent, separator, hBox);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public final void addSlide(String title, Image image, URL url, Theme theme, Node linkedNode) {
        info.put(title, new TutorialInfo(title, image, url, theme, linkedNode));
        updateContent();
    }

    public final TutorialInfo getSlide(String title) {
        return info.get(title);
    }

    public final TutorialInfo getSlide(int index) {
        return info.getValue(index);
    }

    public final TutorialInfo getCurrentSlide() {
        return getSlide(current);
    }

    public final void updateFlowCounter() {
        if (info.isEmpty()) {
            flowCounter.setText("0 / 0");
            previous.setDisable(true);
            next.setText("Close");
        } else {
            flowCounter.setText((current+1)+" / "+info.size());
            previous.setDisable(current <= 0);
            if (current + 1 >= info.size()) {
                next.setText("Close");
            } else {
                next.setText("Next");
            }
        }
    }

    public final void updateContent(int to) {
        current = to;
        updateContent();
    }

    public final void updateContent() {
        if (info.isEmpty()) {
            imageView.setImage(null);
            textContent.getChildren().clear();
        } else {
            TutorialInfo tutorialInfo = getCurrentSlide();
            imageView.setImage(tutorialInfo.getImage());
            textContent.getChildren().addAll(tutorialInfo.load());
            if (onChange != null) {
                onChange.run();
            }
        }
        updateFlowCounter();
    }

    public final void proceedToNext(ActionEvent event) {
        if (current + 1 < info.size()) {
            updateContent(current+1);
        } else {
            current = 0;
            if (onClose != null) {
                onClose.run();
            }
        }
    }
    public final void proceedToPrevious(ActionEvent event) {
        if (current > 0) {
            updateContent(current-1);
        }
    }

    /* *************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/



    /* *************************************************************************
     *                                                                         *
     * classes                                                                 *
     *                                                                         *
     **************************************************************************/

    public final static class TutorialInfo {
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

        public String getTitle() {
            return title;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public URL getURL() {
            return url;
        }

        public Theme getTheme() {
            return theme;
        }

        public void setTheme(Theme theme) {
            this.theme = theme;
        }

        public Node getLinkedNode() {
            return linkedNode;
        }

        private List<Node> load() {
            XMLProcessor processor = new XMLProcessor(getURL());
            processor.setTheme(theme);
            return processor.load(new Attributes(new Pair<>("align", "top_center")));
        }
    }
}
