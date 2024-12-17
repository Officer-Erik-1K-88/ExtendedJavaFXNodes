package com.airent.extendedjavafxnodes.control;

import com.airent.extendedjavafxnodes.utils.ListMap;
import javafx.beans.property.ObjectProperty;
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

    private final ListMap<String, TutorialInfo> tutorialInfoListMap = new ListMap<>();

    public TutorialContent() {
        super();
        imageView = new ImageView();
        textFlow = new TextFlow();
        previous = new Button("Previous");
        flowCounter = new Label("0/0");
        next = new Button("Next");
        HBox hBox = new HBox(previous, flowCounter, next);
        getChildren().addAll(imageView, textFlow, hBox);
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
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    public final static class TutorialInfo {

    }
}
