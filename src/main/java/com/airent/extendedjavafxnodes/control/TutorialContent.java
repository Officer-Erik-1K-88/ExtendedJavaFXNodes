package com.airent.extendedjavafxnodes.control;

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

public class TutorialContent extends VBox {
    private final ImageView imageView;
    private final VBox textContent;
    private final Button next;
    private final Button previous;
    private final Label flowCounter;

    private int current = 0;

    private final ListMap<String, TutorialInfo> info = new ListMap<>();

    public TutorialContent() {
        super(6);
        setFillWidth(true);
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(300);
        setMaxWidth(300);
        BorderStroke borderStroke = new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(2));
        Border border = new Border(borderStroke);
        setBorder(border);
        setPadding(new Insets(10));

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

        getChildren().addAll(imageView, textContent, separator, hBox);
    }

    public final void addSlide(String title, Image image, URL url, Theme theme) {
        info.put(title, new TutorialInfo(title, image, url, theme));
        updateContent();
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
            TutorialInfo tutorialInfo = info.getValue(current);
            imageView.setImage(tutorialInfo.getImage());
            textContent.getChildren().addAll(tutorialInfo.load());
        }
        updateFlowCounter();
    }

    public final void proceedToNext(ActionEvent event) {
        if (current + 1 < info.size()) {
            updateContent(current+1);
        } else {
            setVisible(false);
            current = 0;
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

        public TutorialInfo(String title, Image image, URL url, Theme theme) {
            this.title = title;
            this.image = image;
            this.url = url;
            this.theme = theme;
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

        private List<Node> load() {
            XMLProcessor processor = new XMLProcessor(getURL());
            processor.setTheme(theme);
            return processor.load(new Attributes(new Pair<>("align", "top_center")));
        }
    }
}
