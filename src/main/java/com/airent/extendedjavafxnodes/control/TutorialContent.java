package com.airent.extendedjavafxnodes.control;

import com.airent.extendedjavafxnodes.themes.Theme;
import com.airent.extendedjavafxnodes.utils.ListMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextFlow;

public class TutorialContent extends VBox {
    private ImageView imageView;
    private TextFlow textFlow;
    private Button next;
    private Button previous;
    private Label flowCounter;

    private int current = 0;

    private final ListMap<String, TutorialInfo> info = new ListMap<>();

    public TutorialContent() {
        super();
        imageView = new ImageView();
        textFlow = new TextFlow();
        previous = new Button("Previous");
        previous.setDisable(true);
        flowCounter = new Label("0 / 0");
        next = new Button("Next");
        next.setDisable(true);
        HBox hBox = new HBox(previous, flowCounter, next);
        getChildren().addAll(imageView, textFlow, hBox);
    }

    public final void addSlide(String title, Image image, String description, Theme theme) {
        info.put(title, new TutorialInfo(title, image, description, theme));
        updateFlowCounter();
    }

    public final void updateFlowCounter() {
        if (info.isEmpty()) {
            flowCounter.setText("0 / 0");
            previous.setDisable(true);
            next.setDisable(true);
        } else {
            flowCounter.setText((current+1)+" / "+info.size());
            previous.setDisable(current <= 0);
            next.setDisable(current + 1 >= info.size());
        }
    }

    public final void updateContent(int to) {
        current = to;
        updateContent();
    }

    public final void updateContent() {
        if (info.isEmpty()) {
            imageView.setImage(null);
            textFlow.getChildren().clear();
        } else {
            TutorialInfo tutorialInfo = info.getValue(current);
            imageView.setImage(tutorialInfo.getImage());
        }
        updateFlowCounter();
    }

    public final void proceedToNext() {
        if (current + 1 < info.size()) {
            updateContent(current+1);
        }
    }
    public final void proceedToPrevious() {
        if (current > 0) {
            updateContent(current-1);
        }
    }

    /* *************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    public final ObjectProperty<Image> imageProperty() {
        return imageView.imageProperty();
    }
    public final Image getImage() {
        return imageView.getImage();
    }
    public final void setImage(Image image) {
        imageView.setImage(image);
    }

    /* *************************************************************************
     *                                                                         *
     * classes                                                                 *
     *                                                                         *
     **************************************************************************/

    public final static class TutorialInfo {
        private final String title;
        private Image image;
        private String description;
        private Theme theme;

        public TutorialInfo(String title, Image image, String description, Theme theme) {
            this.title = title;
            this.image = image;
            this.description = description;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Theme getTheme() {
            return theme;
        }

        public void setTheme(Theme theme) {
            this.theme = theme;
        }
    }
}
