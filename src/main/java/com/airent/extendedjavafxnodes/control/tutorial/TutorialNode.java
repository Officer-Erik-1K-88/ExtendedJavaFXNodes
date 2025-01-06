package com.airent.extendedjavafxnodes.control.tutorial;

import com.airent.extendedjavafxnodes.gaxml.themes.Theme;
import com.airent.extendedjavafxnodes.shape.Arrow;
import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@DefaultProperty("slides")
public abstract class TutorialNode extends Parent implements SlideInfo {
    private static final double minHeight = 200;
    private static final double maxSize = 400;
    private static final double defaultArrowSize = 40;
    private final TutorialPopup popup;
    private final Label flowCounter;
    private final Button next;
    private final Button previous;

    private final BorderPane actual;
    private final VBox heldContent;

    private final ImageView imageView;
    private final VBox content;
    private final Label noContent;
    private final Label title;

    private final ObservableList<SlideInfo> slides = new ModifiableObservableListBase<>() {
        private final ArrayList<SlideInfo> actual = new ArrayList<>();
        @Override
        public SlideInfo get(int index) {
            return actual.get(index);
        }

        @Override
        public int size() {
            return actual.size();
        }

        @Override
        protected void doAdd(int index, @NotNull SlideInfo slide) {
            if (slide.getLinkedNode() == null) {
                throw new RuntimeException("Cannot have a slide with no linked node.");
            }
            actual.add(index, slide);
            if (slide instanceof TutorialNode tutorialNode) {
                tutorialNode.isSlide = true;
                tutorialNode.isSub = !TutorialNode.this.isSub;
                if (tutorialNode.isSub) {
                    tutorialNode.heldContent.setBorder(Border.EMPTY);
                }
            }
            updateContent();
        }

        @Override
        protected SlideInfo doSet(int index, @NotNull SlideInfo slide) {
            if (slide.getLinkedNode() == null) {
                throw new RuntimeException("Cannot have a slide with no linked node.");
            }
            SlideInfo old = actual.set(index, slide);
            if (slide instanceof TutorialNode tutorialNode) {
                tutorialNode.isSlide = true;
                tutorialNode.isSub = !TutorialNode.this.isSub;
                if (tutorialNode.isSub) {
                    tutorialNode.heldContent.setBorder(Border.EMPTY);
                }
            }
            updateContent();
            return old;
        }

        @Override
        protected SlideInfo doRemove(int index) {
            SlideInfo removed = actual.remove(index);
            if (removed != null) {
                if (removed instanceof TutorialNode tutorialNode) {
                    tutorialNode.isSlide = false;
                    tutorialNode.isSub = false;
                }
            }
            return removed;
        }
    };

    private int current = 0;
    private boolean isSlide = false;
    private boolean isSub = false;
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
                addSlide(slide);
            }
        }
    }

    public TutorialNode(Image image, List<SlideInfo> slides) {
        this(image);
        if (slides != null) {
            for (SlideInfo slide : slides) {
                addSlide(slide);
            }
        }
    }

    public TutorialNode(Image image) {
        super();
        heldContent = new VBox(6);

        this.image = image;

        heldContent.setAlignment(Pos.TOP_CENTER);
        heldContent.setFillWidth(true);

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
        noContent.setWrapText(true);
        content = new VBox(noContent);
        content.setFillWidth(true);
        content.setAlignment(Pos.TOP_CENTER);

        VBox vBox = new VBox(imageView, content);
        vBox.setSpacing(6);
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setMinHeight(40);

        ScrollPane contentScroll = new ScrollPane(vBox);
        contentScroll.setFitToWidth(true);

        heldContent.getChildren().addAll(closeHolder, title, contentScroll, separator, flowHolder);

        // arrow creation
        Arrow rightArrow = new Arrow(defaultArrowSize, defaultArrowSize);
        Arrow leftArrow = new Arrow(defaultArrowSize, defaultArrowSize);
        leftArrow.setRotate(180);
        Arrow topArrow = new Arrow(defaultArrowSize, defaultArrowSize);
        topArrow.setRotate(-90);
        Arrow bottomArrow = new Arrow(defaultArrowSize, defaultArrowSize);
        bottomArrow.setRotate(90);
        // for alignment of arrows.
        VBox right = new VBox(rightArrow);
        right.setAlignment(Pos.CENTER);
        VBox left = new VBox(leftArrow);
        left.setAlignment(Pos.CENTER);
        HBox top = new HBox(topArrow);
        top.setAlignment(Pos.CENTER);
        HBox bottom = new HBox(bottomArrow);
        bottom.setAlignment(Pos.CENTER);

        this.actual = new BorderPane(heldContent, top, right, bottom, left);

        this.getChildren().add(this.actual);
        showArrow();

        popup = new TutorialPopup(this);
        popup.setMaxWidth(maxSize);

        updateTheme();
    }

    private void updateTheme() {
        updateTheme(this, this.isSub, true);
    }

    private void updateTheme(Node node, boolean isSub, boolean allowBorder) {
        Theme theme = getTheme();
        boolean loopParent = true;
        if (node instanceof TutorialNode tutorialNode) {
            isSub = tutorialNode.isSub;
            allowBorder = true;

            node = tutorialNode.actual.getCenter();
            if (node instanceof ScrollPane) {
                tutorialNode.heldContent.setBorder(Border.EMPTY);
            }
        }
        if (node instanceof Parent parent) {
            if (parent instanceof Region region) {
                if (region instanceof Labeled labeled) {
                    labeled.setWrapText(true);
                } else {
                    if (region instanceof ScrollPane scrollPane) {
                        loopParent = false;
                        updateTheme(scrollPane.getContent(), false, false);
                    }
                    if ((region instanceof ScrollPane) || (allowBorder && !isSub)) {
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
            if (loopParent) {
                parent.getChildrenUnmodifiable().forEach(node1 -> updateTheme(node1, false, false));
            }
        }
    }

    private List<Node> singleListOfSelf = null;

    @Override
    public final List<Node> getDescription() {
        if (singleListOfSelf == null) {
            singleListOfSelf = List.of(this);
        }
        return singleListOfSelf;
    }

    /**
     * Gets and returns the {@link Theme} of the
     * currently selected slide.
     * If no slide is selected then the {@link #defaultTheme() default Theme}
     * is used instead. The default Theme is also used if the slide has no Theme.
     *
     * @return The {@code Theme} that is currently selected.
     */
    @Override
    public final Theme getTheme() {
        SlideInfo slide = getCurrentSlide();
        if (slide == null) return defaultTheme();
        Theme theme = slide.getTheme();
        if (theme == null) return defaultTheme();
        return theme;
    }

    /**
     * Gets the {@link Theme} that is used by default
     * when there is no slide selected.
     *
     * @return The {@code Theme} to use when no slide is selected.
     */
    public abstract Theme defaultTheme();

    @Override
    public final Image getImage() {
        return image;
    }

    /**
     * Gets and returns the linked node of the
     * currently selected slide.
     * If no slide is selected, then the
     * {@link #defaultLinkedNode() default link node}
     * is used instead.
     *
     * @return The linked node that is tied to the currently selected slide.
     */
    @Override
    public final Node getLinkedNode() {
        SlideInfo slide = getCurrentSlide();
        if (slide == null) return defaultLinkedNode();
        return slide.getLinkedNode();
    }

    /**
     * The default linked node is used when there is no slide selected
     * of this {@link TutorialNode}.
     *
     * @return The linked node to use when no slide is selected.
     */
    protected Node defaultLinkedNode() {
        return null;
    }

    /**
     * Gets the {@link ObservableList} that holds the slides
     * of this {@code TutorialNode}.
     *
     * @return The list of stored slides.
     */
    public final ObservableList<SlideInfo> getSlides() {
        return slides;
    }

    /**
     * Adds a new slide at the end of this {@code TutorialNode}.
     *
     * @param slide The slide to add.
     */
    public final void addSlide(@NotNull SlideInfo slide) {
        slides.add(slide);
    }

    /**
     * Gets and returns the slide at the stated index.
     *
     * @param index The index position of the slide to get.
     * @return The slide at the stated index.
     */
    public final @Nullable SlideInfo getSlide(int index) {
        if (slides.isEmpty()) return null;
        return slides.get(index);
    }

    /**
     * Gets and returns the currently selected slide.
     *
     * @return The currently selected slide.
     */
    public final SlideInfo getCurrentSlide() {
        return getSlide(current);
    }

    /**
     * Updates the visual graphics that allow for
     * traversal over the slides in this {@code TutorialNode}.
     */
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

    /**
     * This method is used to select a new slide and update
     * the content holder with the necessary data from the
     * newly selected slide.
     *
     * @param to The index of the slide to select.
     * @see #updateContent()
     */
    public final void updateContent(int to) {
        current = to;
        updateContent();
    }

    /**
     * Updates the content holder of this {@code TutorialNode}
     * with the necessary data from the currently selected slide.
     *
     * @see #updateContent(int)
     */
    public final void updateContent() {
        content.getChildren().clear();

        if (!slides.isEmpty()) {
            SlideInfo currentSlide = getCurrentSlide();
            imageView.setImage(currentSlide.getImage());
            content.getChildren().addAll(currentSlide.getDescription());
            title.setText(this.getTitle()+": "+currentSlide.getTitle());
        } else {
            imageView.setImage(null);
            title.setText("None:");
            content.getChildren().add(noContent);
        }
        updateTheme();

        updateFlowCounter();
        if (popup.isShowing()) {
            show();
        }
    }

    /**
     * This method will go to the next slide in the order,
     * or if on the last slide then will close out the popup
     * and set the current slide to the first slide.
     *
     * @param event The event that caused this action.
     */
    public final void proceedToNext(ActionEvent event) {
        if (current + 1 < slides.size()) {
            updateContent(current+1);
        } else {
            current = 0;
            hide();
            updateContent();
        }
    }

    /**
     * This method will go to the previous slide in the order.
     *
     * @param event The event that caused this action.
     */
    public final void proceedToPrevious(ActionEvent event) {
        if (current > 0) {
            updateContent(current-1);
        }
    }

    private void showArrow() {
        actual.getBottom().setVisible(false);
        actual.getTop().setVisible(false);
        actual.getLeft().setVisible(false);
        actual.getRight().setVisible(false);
        switch (location) {
            case "left" -> actual.getRight().setVisible(true);
            case "right" -> actual.getLeft().setVisible(true);
            case "top" -> actual.getBottom().setVisible(true);
            case "bottom" -> actual.getTop().setVisible(true);
        }
    }

    /**
     * Hides the popup to this {@code TutorialNode}.
     */
    public void hide() {
        popup.hide();
        popup.setMaxWidth(maxSize);
        popup.setMaxHeight(-1);
    }

    /**
     * Shows the popup to this {@code TutorialNode}.
     * The popup is positioned to be either to the right, left, bottom, or top
     * of the node that the currently selected slide is linked to, given there
     * is space for the popup to be.
     * If there is no space in any of the four directions,
     * then the popup will be put in the center of the window.
     */
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
        popup.hide();
        popup.show(scene.getWindow());

        double width = this.getLayoutBounds().getWidth();
        double height = this.getLayoutBounds().getHeight();
        if (scene.getWidth() <= (width+25)) {
            width = (width/2)+defaultArrowSize;
            if (scene.getWidth() <= width) {
                width = scene.getWidth()-10;
            }
            if (width < defaultArrowSize) width = defaultArrowSize;
            popup.setMaxWidth(width);
        }
        if (scene.getHeight() <= (height+25)) {
            height = (height/2)+defaultArrowSize;
            if (scene.getWidth() <= height) {
                height = scene.getHeight()-10;
            }
            if (height < defaultArrowSize) height = defaultArrowSize;
            popup.setMaxHeight(height);
        }

        System.out.println(height);
        if (height < minHeight) {
            this.actual.setCenter(null);
            ScrollPane scrollPane = new ScrollPane(heldContent);
            scrollPane.setFitToWidth(false);
            this.actual.setCenter(scrollPane);
        } else {
            if (heldContent.getParent() instanceof ScrollPane scrollPane) {
                scrollPane.setContent(null);
                this.actual.setCenter(heldContent);
            }
        }

        positionPopup(linkedNode, width, height);
        //System.out.println("AnchorX: "+anchorX);
        //System.out.println("AnchorY: "+anchorY);
        showArrow();
        updateTheme();
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
        double spaceToTop = windowY + windowHeight - nodeTop;

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

    /* *************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    public DoubleProperty minWidthProperty() {
        return actual.minWidthProperty();
    }

    public DoubleProperty prefWidthProperty() {
        return actual.prefWidthProperty();
    }

    public DoubleProperty maxWidthProperty() {
        return actual.maxWidthProperty();
    }

    public DoubleProperty minHeightProperty() {
        return actual.minHeightProperty();
    }

    public DoubleProperty prefHeightProperty() {
        return actual.prefHeightProperty();
    }

    public DoubleProperty maxHeightProperty() {
        return actual.maxHeightProperty();
    }
}
