package com.airent.extendedjavafxnodes.control.tutorial;

import com.airent.extendedjavafxnodes.utils.ListMap;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class TutorialNode<T extends SlideInfo> extends VBox {
    private final Tooltip popup;
    private final Label flowCounter;
    private final Button next;
    private final Button previous;

    private final ImageView imageView;
    private final VBox content;
    private final Label noContent;
    private final Label title;

    private final ListMap<String, T> slides = new ListMap<>();
    private int current = 0;

    public TutorialNode() {
        super(6);
        setAlignment(Pos.TOP_CENTER);
        setFillWidth(true);
        setPrefWidth(300);
        setMaxWidth(300);

        Button close = new Button("X");
        close.setFont(new Font(24));
        close.setOnAction(event -> hide());
        HBox closeHolder = new HBox(close);
        closeHolder.setAlignment(Pos.TOP_RIGHT);

        previous = new Button("Previous");
        previous.setDisable(true);
        previous.setOnAction(this::proceedToPrevious);
        flowCounter = new Label("0 / 0");
        next = new Button("Next");
        next.setText("Close");
        next.setOnAction(this::proceedToNext);

        HBox flowHolder = new HBox(previous, flowCounter, next);
        flowHolder.setFillHeight(true);
        flowHolder.setSpacing(10);
        flowHolder.setAlignment(Pos.CENTER);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setMinHeight(16);
        separator.setPrefHeight(30);

        imageView = new ImageView();

        title = new Label("None:");
        noContent = new Label("Proceed to the next tutorial or close out.");
        content = new VBox(noContent);
        content.setFillWidth(true);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPrefWidth(300);
        content.setMaxWidth(300);

        VBox vBox = new VBox(imageView, content);
        setVBox(vBox);

        getChildren().addAll(closeHolder, title, vBox, separator, flowHolder);

        popup = new Tooltip();
    }

    private void setVBox(@NotNull VBox vBox) {
        vBox.setSpacing(6);
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPrefWidth(300);
        vBox.setMaxWidth(300);
        BorderStroke borderStroke = new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(2));
        Border border = new Border(borderStroke);
        vBox.setBorder(border);
        vBox.setPadding(new Insets(10));
    }

    private void updateTheme(List<Node> nodes) {
        for (Node node : nodes) {
            if (node instanceof Parent parent) {
                if (parent instanceof Region region) {
                    Border border = region.getBorder();
                    List<BorderStroke> borderStrokes = new ArrayList<>();
                    for (BorderStroke borderStroke : border.getStrokes()) {
                        Paint bottom = getCurrentSlide().getTheme().mixSecondary(borderStroke.getBottomStroke()).getActual();
                        Paint top = getCurrentSlide().getTheme().mixSecondary(borderStroke.getTopStroke()).getActual();
                        Paint left = getCurrentSlide().getTheme().mixSecondary(borderStroke.getLeftStroke()).getActual();
                        Paint right = getCurrentSlide().getTheme().mixSecondary(borderStroke.getRightStroke()).getActual();
                        borderStrokes.add(new BorderStroke(
                                top, right, bottom, left,
                                borderStroke.getTopStyle(),
                                borderStroke.getRightStyle(),
                                borderStroke.getBottomStyle(),
                                borderStroke.getLeftStyle(),
                                borderStroke.getRadii(),
                                borderStroke.getWidths(),
                                borderStroke.getInsets()));
                    }
                    border = new Border(borderStrokes, border.getImages());
                    region.setBorder(border);
                    if (parent instanceof Pane pane) {
                        updateTheme(pane.getChildren());
                    } else if (parent instanceof Control control) {
                        if (control instanceof Labeled labeled) {

                        }
                    }
                }
            } else if (node instanceof Shape shape) {}
        }
    }

    public final void addSlide(String name, T slide) {
        onSlideAdd(slide);
        slides.put(name, slide);
        updateContent();
    }

    public final T getSlide(String name) {
        return slides.get(name);
    }

    public final T getSlide(int index) {
        return slides.getValue(index);
    }

    public final T getCurrentSlide() {
        return getSlide(current);
    }

    public final void updateFlowCounter() {
        if (slides.isEmpty()) {
            flowCounter.setText("0 / 0");
            previous.setDisable(true);
            next.setText("Close");
        } else {
            flowCounter.setText((current+1)+" / "+ slides.size());
            previous.setDisable(current <= 0);
            if (current + 1 >= slides.size()) {
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
        content.getChildren().clear();
        if (!slides.isEmpty()) {
            T currentSlide = getCurrentSlide();
            imageView.setImage(currentSlide.getImage());
            content.getChildren().addAll(currentSlide.getDescription());
            title.setText(slides.getKey(current)+": "+currentSlide.getTitle());
        } else {
            title.setText("None:");
            content.getChildren().add(noContent);
        }
        updateFlowCounter();
    }

    public final void proceedToNext(ActionEvent event) {
        if (current + 1 < slides.size()) {
            updateContent(current+1);
        } else {
            current = 0;
            hide();
        }
    }
    public final void proceedToPrevious(ActionEvent event) {
        if (current > 0) {
            updateContent(current-1);
        }
    }

    protected abstract void onSlideAdd(T slide);

    public void hide() {}
}
