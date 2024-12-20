package com.airent.extendedjavafxnodes.control.tutorial;

import com.airent.extendedjavafxnodes.gaxml.themes.Theme;
import com.airent.extendedjavafxnodes.shape.Arrow;
import com.airent.extendedjavafxnodes.utils.ListMap;
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@DefaultProperty("slides")
public abstract class TutorialNode extends VBox implements SlideInfo {
    private final TutorialPopup popup;
    private final Label flowCounter;
    private final Button next;
    private final Button previous;

    private final Arrow leftArrow;
    private final Arrow rightArrow;
    private final Arrow topArrow;
    private final Arrow bottomArrow;

    private final ImageView imageView;
    private final VBox content;
    private final Label noContent;
    private final Label title;

    private final ListMap<String, SlideInfo> slides = new ListMap<>();

    public ListMap<String, SlideInfo> getSlides() {
        return slides;
    }

    private int current = 0;
    private boolean isSlide = false;
    private final Image image;

    public TutorialNode() {
        this((Image) null);
    }

    public TutorialNode(SlideInfo... slides) {
        this(null, slides);
    }

    public TutorialNode(List<SlideInfo> slides) {
        this(null, slides);
    }

    public TutorialNode(Image image, SlideInfo... slides) {
        this(image);
        if (slides != null) {
            for (SlideInfo slide : slides) {
                addSlide(slide.getTitle(), slide);
            }
        }
    }

    public TutorialNode(Image image, List<SlideInfo> slides) {
        this(image);
        if (slides != null) {
            for (SlideInfo slide : slides) {
                addSlide(slide.getTitle(), slide);
            }
        }
    }

    public TutorialNode(Image image) {
        super(6);
        this.rightArrow = new Arrow(40, 40);
        this.leftArrow = new Arrow(40, 40);
        this.topArrow = new Arrow(40, 40);
        this.bottomArrow = new Arrow(40, 40);

        this.image = image;

        setAlignment(Pos.TOP_CENTER);
        setFillWidth(true);
        //setPrefWidth(300);
        setMaxWidth(300);

        updateTheme(this);

        Hyperlink close = new Hyperlink("X");
        close.setFont(new Font(20));
        //close.setPadding(new Insets(2));
        close.setBorder(Border.EMPTY);
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
        //content.setPrefWidth(300);
        content.setMaxWidth(300);

        VBox vBox = new VBox(imageView, content);
        setVBox(vBox);

        getChildren().addAll(closeHolder, title, vBox, separator, flowHolder);

        popup = new TutorialPopup(this);
    }

    private void setVBox(@NotNull VBox vBox) {
        vBox.setSpacing(6);
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.TOP_CENTER);
        //vBox.setPrefWidth(300);
        vBox.setMaxWidth(300);
        updateTheme(vBox);
        vBox.setPadding(new Insets(10));
    }

    private void updateTheme(Node node) {
        Theme theme = getTheme();
        boolean allowBorder = true;
        if (node instanceof TutorialNode tutorialNode) {
            allowBorder = !tutorialNode.isSlide;
        }
        if (node instanceof Region region) {
            if (allowBorder) {
                BorderStroke borderStroke = new BorderStroke(
                        theme.getSecondary(),
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(2));
                Border border = new Border(borderStroke);
                region.setBorder(border);
            } else {
                region.setBorder(Border.EMPTY);
            }
            region.setBackground(new Background(new BackgroundFill(
                    theme.getPrimary(),
                    null,
                    null)));
        }
    }

    private List<Node> singleListOfSelf = null;

    @Override
    public List<Node> getDescription() {
        if (singleListOfSelf == null) {
            singleListOfSelf = List.of(this);
        }
        return singleListOfSelf;
    }

    @Override
    public Theme getTheme() {
        SlideInfo slide = getCurrentSlide();
        if (slide == null) return defaultTheme();
        return slide.getTheme();
    }

    public abstract Theme defaultTheme();

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public Node getLinkedNode() {
        SlideInfo slide = getCurrentSlide();
        if (slide == null) return null;
        return slide.getLinkedNode();
    }

    public final void addSlide(String name, @NotNull SlideInfo slide) {
        if (slide.getLinkedNode() == null) {
            throw new RuntimeException("Cannot have a slide with no linked node,");
        }
        slides.put(name, slide);
        if (slide instanceof TutorialNode tutorialNode) {
            tutorialNode.isSlide = true;
            tutorialNode.setBorder(Border.EMPTY);
        }
        updateContent();
    }

    public final SlideInfo getSlide(String name) {
        return slides.get(name);
    }

    public final SlideInfo getSlide(int index) {
        if (slides.isEmpty()) return null;
        return slides.getValue(index);
    }

    public final SlideInfo getCurrentSlide() {
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
        Theme theme = getTheme();
        if (!slides.isEmpty()) {
            SlideInfo currentSlide = getCurrentSlide();
            if (currentSlide instanceof TutorialNode tutorialNode) {
                updateTheme(tutorialNode);
            }
            imageView.setImage(currentSlide.getImage());
            content.getChildren().addAll(currentSlide.getDescription());
            title.setText(this.getTitle()+": "+currentSlide.getTitle());
        } else {
            imageView.setImage(null);
            title.setText("None:");
            content.getChildren().add(noContent);
        }
        updateTheme(content.getParent());

        updateFlowCounter();
        if (popup.isShowing()) {
            show();
        }
    }

    public final void proceedToNext(ActionEvent event) {
        if (current + 1 < slides.size()) {
            updateContent(current+1);
        } else {
            current = 0;
            hide();
            updateContent();
        }
    }
    public final void proceedToPrevious(ActionEvent event) {
        if (current > 0) {
            updateContent(current-1);
        }
    }

    private void showArrow() {
        if (location.equals("left")) {

        } else if (location.equals("right")) {

        } else if (location.equals("top")) {

        } else if (location.equals("bottom")) {

        }
    }

    public void hide() {
        popup.hide();
        popup.setMaxWidth(300);
        popup.setMaxHeight(-1);
    }
    public void show() {
        Node linkedNode = getCurrentSlide().getLinkedNode();
        Scene scene = linkedNode.getScene();
        if (scene != null) {
            if (scene.getWindow() == null) {
                throw new RuntimeException("The linked node isn't in a scene that has a window.");
            }
        } else {
            throw new RuntimeException("The linked node doesn't have a scene.");
        }
        popup.show(scene.getWindow());
        popup.hide();

        double width = this.getLayoutBounds().getWidth();
        double height = this.getLayoutBounds().getHeight();
        if (scene.getWidth() <= (width+25)) {
            width = width/2;
            if (scene.getWidth() <= width) {
                width = scene.getWidth()-10;
            }
            if (width < 10) width = 10;
            popup.setMaxWidth(width);
        }
        if (scene.getHeight() <= (height+25)) {
            height = height/2;
            if (scene.getWidth() <= height) {
                height = scene.getHeight()-10;
            }
            if (height < 10) height = 10;
            popup.setMaxHeight(height);
        }

        positionPopup(linkedNode, width, height);
        System.out.println("AnchorX: "+anchorX);
        System.out.println("AnchorY: "+anchorY);
        popup.show(linkedNode, anchorX, anchorY);
    }

    private double anchorX = 0;
    private double anchorY = 0;
    private String location = "center";

    private void positionPopup(Node node, double popupWidth, double popupHeight) {
        double gap = 4;

        // Get the window where the node resides
        Window window = node.getScene().getWindow();

        // Get the bounds of the node relative to the screen
        Bounds nodeBounds = node.localToScreen(node.getBoundsInLocal());

        // Calculate space around the node
        double windowWidth = window.getWidth();
        double windowHeight = window.getHeight();
        double windowX = window.getX();
        double windowY = window.getY();

        double nodeRight = nodeBounds.getMaxX();
        double nodeLeft = nodeBounds.getMinX();
        double nodeTop = nodeBounds.getMinY();
        double nodeBottom = nodeBounds.getMaxY();
        double nodeCenterX = (nodeLeft + nodeRight) / 2;
        double nodeCenterY = (nodeTop + nodeBottom) / 2;

        double spaceToRight = windowX + windowWidth - nodeRight;
        double spaceToLeft = nodeLeft - windowX;
        double spaceToBottom = windowY + windowHeight - nodeBottom;
        double spaceToTop = nodeTop - windowY;

        double popupX, popupY;

        // Position the popup

        // vertical positioning (Can be override if horizontal position is center of node)
        popupY = nodeCenterY - (popupHeight / 2);

        // horizontal positioning
        if (spaceToRight >= popupWidth + gap) {
            // Align to the right of the node if there's space
            popupX = nodeRight + gap;
            location = "right";
        } else if (spaceToLeft >= popupWidth + gap) {
            // Align to the left of the node if there's space
            popupX = nodeLeft - popupWidth - gap;
            location = "left";
        } else {
            if (spaceToBottom >= popupHeight + gap) {
                // Align to the bottom of the node if there's space
                popupY = nodeBottom + gap;
                // Center horizontally to the node
                popupX = nodeCenterX - (popupWidth / 2);
                location = "bottom";
            } else if (spaceToTop >= popupHeight + gap) {
                // Align to the top of the node if there's space
                popupY = nodeTop - popupHeight - gap;
                // Center horizontally to the node
                popupX = nodeCenterX - (popupWidth / 2);
                location = "top";
            } else {
                // Center horizontally in the window
                popupX = windowX + (windowWidth - popupWidth) / 2;
                // Center vertically in the window
                popupY = windowY + (windowHeight - popupHeight) / 2;
                location = "center";
            }
        }

        // Ensure the popup does not go off the screen horizontally
        popupX = Math.max(windowX, Math.min(popupX, windowX + windowWidth - popupWidth));

        // Ensure the popup does not go off the screen vertically
        popupY = Math.max(windowY, Math.min(popupY, windowY + windowHeight - popupHeight));

        // Set the popup position
        anchorX = popupX;
        anchorY = popupY;
    }
}
