package com.airent.extendedjavafxnodes.control.tutorial;

import com.airent.extendedjavafxnodes.utils.ListMap;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import org.jetbrains.annotations.NotNull;

public class TutorialPopup extends Popup {
    private final Label flowCounter;
    private final Button next;
    private final Button previous;
    private final VBox tutorialContent;
    private final Label noContent;
    private final Label title;

    private final ListMap<String, TutorialContent> content = new ListMap<>();
    private int current = 0;

    public TutorialPopup() {
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

        HBox hBox = new HBox(previous, flowCounter, next);
        hBox.setFillHeight(true);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setMinHeight(16);
        separator.setPrefHeight(30);

        title = new Label("None:");
        noContent = new Label("Proceed to the next tutorial or close out.");
        tutorialContent = new VBox(noContent);
        setVBox(tutorialContent);

        VBox vBox = new VBox(closeHolder, title, tutorialContent, separator, hBox);
        setVBox(vBox);
        getContent().add(vBox);
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

    private void onContentClose() {
        tutorialContent.getChildren().clear();
        tutorialContent.getChildren().add(noContent);
        title.setText("None:");
    }

    private void onContentChange() {
        title.setText(content.getKey(current)+": "+getTutorial(current).getCurrentSlide().getTitle());
    }

    public final void addTutorial(String name, @NotNull TutorialContent tutorial) {
        tutorial.onClose = this::onContentClose;
        tutorial.onChange = this::onContentChange;
        content.put(name, tutorial);
        updateContent();
    }

    public final TutorialContent getTutorial(String name) {
        return content.get(name);
    }

    public final TutorialContent getTutorial(int index) {
        return content.getValue(index);
    }

    public final void updateFlowCounter() {
        if (content.isEmpty()) {
            flowCounter.setText("0 / 0");
            previous.setDisable(true);
            next.setText("Close");
        } else {
            flowCounter.setText((current+1)+" / "+content.size());
            previous.setDisable(current <= 0);
            if (current + 1 >= content.size()) {
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
        if (!content.isEmpty()) {
            tutorialContent.getChildren().clear();
            TutorialContent tutCon = getTutorial(current);
            tutorialContent.getChildren().addAll(tutCon.getNodes());
            onContentChange();
        }
        updateFlowCounter();
    }

    public final void proceedToNext(ActionEvent event) {
        if (current + 1 < content.size()) {
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

    public final void show() {
        show(getTutorial(current).getCurrentSlide().getLinkedNode(), 0, 0);
    }
}
