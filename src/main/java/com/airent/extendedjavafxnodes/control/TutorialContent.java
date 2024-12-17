package com.airent.extendedjavafxnodes.control;

import com.airent.extendedjavafxnodes.gaxml.Formatter;
import com.airent.extendedjavafxnodes.gaxml.themes.Theme;
import com.airent.extendedjavafxnodes.utils.ListMap;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TutorialContent extends VBox {
    private ImageView imageView;
    private VBox textContent;
    private Button next;
    private Button previous;
    private Label flowCounter;

    private int current = 0;

    private final ListMap<String, TutorialInfo> info = new ListMap<>();

    public TutorialContent() {
        super();
        imageView = new ImageView();
        textContent = new VBox();
        previous = new Button("Previous");
        previous.setDisable(true);
        flowCounter = new Label("0 / 0");
        next = new Button("Next");
        next.setDisable(true);
        HBox hBox = new HBox(previous, flowCounter, next);
        getChildren().addAll(imageView, textContent, hBox);
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
            textContent.getChildren().clear();
        } else {
            TutorialInfo tutorialInfo = info.getValue(current);
            imageView.setImage(tutorialInfo.getImage());
            Formatter formatter = new Formatter(tutorialInfo.getTheme());
            String[] paras = tutorialInfo.getDescription().split("\n");
            for (String para : paras) {

            }
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
