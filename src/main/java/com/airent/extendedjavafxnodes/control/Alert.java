package com.airent.extendedjavafxnodes.control;

import com.airent.extendedjavafxnodes.utils.Resources;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

/**
 * This class is almost exactly the same as JavaFX's
 * alert class, but with a few additional abilities.
 * @see AlertType
 */
public class Alert extends Dialog<ButtonType> {
    /**
     * How much of the width of this Alert's
     * {@link javafx.scene.control.DialogPane DialogPane}
     * will be divided by before being attached
     * as the body's {@code WrappingWidth} for the height
     * calculations of the {@code ContentText}.
     */
    public static final double BODYDIV = 1.27686112;
    public static final double PADDING = 10;
    public static final Font FONT = new Font(12);

    private final ObjectProperty<AlertType> alertType = new SimpleObjectProperty<>();
    private TextField input = null;

    /**
     * Builds the default None AlertType Alert.
     */
    public Alert() {
        super(new javafx.scene.control.Alert(AlertType.NONE.convert()));
        setAlertType(AlertType.NONE);
        init("NULL Action Declared!",
                "The action of `NULL` was issued!",
                "An undefined action was executed.");
    }

    public Alert(@NotNull AlertType alertType) {
        super(new javafx.scene.control.Alert(alertType.convert()));
        setAlertType(alertType);
        init(alertType.name()+" Action Declared!",
                "An action of "+alertType.name()+" was declared.",
                "An empty construct of the `AlertType` \""+alertType.name()+"\" was called.");
    }

    public Alert(@NotNull AlertType alertType, String contentText, ButtonType... buttonTypes) {
        super(new javafx.scene.control.Alert(alertType.convert(), contentText, buttonTypes));
        setAlertType(alertType);
        init(getTitle(), getHeaderText(), getContentText());
    }

    public Alert(@NotNull AlertType alertType, String title, String header, String body) {
        super(new javafx.scene.control.Alert(alertType.convert()));
        setAlertType(alertType);
        init(title, header, body);
    }

    public Alert(@NotNull AlertType alertType, String title, String header) {
        super(new javafx.scene.control.Alert(alertType.convert()));
        setAlertType(alertType);
        init(title, header, null);
    }

    /**
     * Initializes this {@code Alert} container.
     * @param title The title of this {@code Alert}.
     * @param header The header of this {@code Alert}.
     * @param body The body content of this {@code Alert}.
     */
    protected void init(String title, String header, String body) {
        if (getAlertType() != AlertType.NONE) {
            ImageView imageView = new ImageView(getAlertType().getImage());
            setGraphic(imageView);
        }
        if (title == null) {
            title = "NULL";
        }
        if (header == null) {
            header = "";
        }
        if (body == null) {
            body = "";
        }
        if (!getTitle().equals(title)) {
            this.setTitle(title);
        }
        if (!getHeaderText().equals(header)) {
            this.setHeaderText(header);
        }
        if (!getContentText().equals(body)) {
            this.setContentText(body);
        }
        //this.setResizable(true);

        if (getAlertType() == AlertType.PROMPT) {
            if (input == null) {
                input = new TextField();
                input.setMinHeight(26);
                input.setPrefHeight(26);
                input.setMaxHeight(26);
                input.setMinWidth(200);
                input.setPrefWidth(200);
                //input.setMaxWidth(200);
                input.setFont(FONT);
            }
        }

        AtomicInteger placementIndex = new AtomicInteger();
        AtomicReference<Double> placementY = new AtomicReference<>((double) 0);
        initNodes(this.getDialogPane(), ((labelPair, pane) -> {
            Label label = labelPair.getValue();
            placementIndex.set(labelPair.getKey());
            double requiredHeight;
            if (pane.equals(this.getDialogPane()) && input != null) {
                requiredHeight = setRequiredHeight(label, pane, input.getPrefHeight()+20);
            } else {
                requiredHeight = setRequiredHeight(label, pane);
            }
            placementY.set(placementY.get()+requiredHeight);
            return requiredHeight;
        }));
        if (input != null) {
            input.autosize();
            input.setLayoutX(PADDING);
            input.setLayoutY(placementY.get()+input.getHeight()+PADDING);

            this.getDialogPane().getChildren().add(placementIndex.get()+1, input);
        }
    }

    private void initNodes(@NotNull Pane parent, BiFunction<Pair<Integer, Label>, Pane, Double> labelFunction) {
        ObservableList<Node> children = parent.getChildren();
        for(int i=0; i<children.size(); i++) {
            Node node = children.get(i);
            if (node instanceof Pane pane) {
                initNodes(pane, labelFunction);
            } else if (node instanceof Label label) {
                label.setFont(FONT);
                double requiredHeight = labelFunction.apply(new Pair<>(i, label), parent);
                label.setMinHeight(requiredHeight);
                label.setPrefHeight(requiredHeight);
            }
        }
    }

    /**
     * The same as {@link #showAndWait()}, but handles the factor of a
     * {@link AlertType#PROMPT prompted} alert and returns the text of the {@link #input}.
     * <br>
     * Returns null if any exceptions occur after this {@code Alert} is closed.
     *
     * @return The text provided to a {@link AlertType#PROMPT} on confirmation.
     */
    public final @Nullable String showAndCollect() {
        Optional<ButtonType> buttonType = this.showAndWait();
        try {
            boolean good = buttonType.isPresent() && buttonType.get().equals(ButtonType.OK);
            if (good) {
                return input.getText();
            } else {
                return "";
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Displays this Alert and waits for user confirmation.
     * This method can only be used with validate and confirmation
     * AlertTypes.
     * 
     * @param action The action to exicute if user selected OK.
     * @return Whether the action was exicuted.
     */
    public final boolean showAndCollect(Runnable action) {
        boolean good;
        Optional<ButtonType> buttonType = this.showAndWait();
        try {
            good = buttonType.isPresent() && buttonType.get().equals(ButtonType.OK);
            if (good) {
                Platform.runLater(action);
            }
        } catch (Exception e) {
            good = false;
        }
        return good;
    }

    /**
     * Removes all content from this Alert,
     * if te Alert was open then it is closed.
     */
    public void clear() {
        init("NULL", null, null);
        if (isShowing()) {
            close();
        }
    }

    @Override
    public javafx.scene.control.Alert getMaster() {
        return (javafx.scene.control.Alert) super.getMaster();
    }

    // AlertType
    public final ObjectProperty<AlertType> alertTypeProperty() {
        return this.alertType;
    }
    /**
     * Gets the type of Alert this Alert is.
     * 
     * @return The AlertType for this Alert.
     */
    public final AlertType getAlertType() {
        return this.alertType.get();
    }
    /**
     * Changes the AlertType of this Alert.
     * 
     * @param alertType The AlertType to change this Alert to.
     */
    public final void setAlertType(AlertType alertType) {
        this.alertType.setValue(alertType);
        if (getMaster() != null) {
            getMaster().setAlertType(alertType.convert());
        }
    }

    // ButtonType
    public final ObservableList<ButtonType> getButtonTypes() {
        return this.getDialogPane().getButtonTypes();
    }

    // Input
    /**
     * Gets the input of this Alert,
     * given this Alert has an AlertType of prompt.
     * 
     * @return The TextField of an prompt AlertType.
     */
    public final TextField getInput() {
        return input;
    }

    /**
     * Asks the user to validate an action for exicution.
     * 
     * @param actionName The name of the action to validate.
     * @param action The action to preform on validation.
     * @return
     */
    public static boolean validateAction(String actionName, Runnable action) {
        Alert confirm = new Alert(AlertType.VALIDATE,
                "Validate Action: "+actionName,
                "Are you sure you want to `"+actionName+"`?");
        return confirm.showAndCollect(action);
    }

    /**
     * Creates a new prompt and shows and waits
     * for user given input.
     * 
     * @param title The title of the prompt
     * @param header The header of the prompt
     * @param body The body text of the prompt.
     * @return The user input.
     */
    public static String textAlert(String title, String header, String body) {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(header);
        confirm.setContentText(body);
        TextField input = new TextField();
        input.setMinHeight(26);
        input.setPrefHeight(26);
        input.setMaxHeight(26);
        input.setMinWidth(200);
        input.setPrefWidth(200);
        //input.setMaxWidth(200);

        ObservableList<Node> children = confirm.getDialogPane().getChildren();
        int placementIndex = 0;
        double placementY = 0;
        for(int i=0; i<children.size(); i++) {
            Node node = children.get(i);
            //System.out.println(node.getClass().getName());
            //System.out.println(node.getTypeSelector());
            if (node instanceof Label label) {
                placementIndex = i;
                double requiredHeight = setRequiredHeight(label, confirm.getDialogPane(), input.getPrefHeight()+20);
                label.setMinHeight(requiredHeight);
                label.setPrefHeight(requiredHeight);
                placementY += requiredHeight;
            }
        }

        input.autosize();
        input.setLayoutX(PADDING);
        input.setLayoutY(placementY+input.getHeight()+PADDING);

        confirm.getDialogPane().getChildren().add(placementIndex+1, input);

        Optional<ButtonType> buttonType = confirm.showAndWait();
        boolean good = buttonType.isPresent() && buttonType.get().equals(ButtonType.OK);
        if (good) {
            return input.getText();
        } else {
            return "";
        }
    }


    /**
     * Creates and shows an information AlertType Alert.
     * 
     * @param title The title of the Alert
     * @param header The header of the Alert
     * @param body The text content of the Alert
     */
    public static void showAlert(String title, String header, String body) {
        new Alert(AlertType.INFORMATION, title, header, body).show();
    }
    /**
     * Creates and shows an information AlertType Alert.
     * 
     * @param title The title of the Alert
     * @param header The header of the Alert
     */
    public static void showAlert(String title, String header) {
        new Alert(AlertType.INFORMATION, title, header).show();
    }
    /**
     * Creates and shows an information AlertType Alert.
     * 
     * @param title The title of the Alert
     */
    public static void showAlert(String title) {
        new Alert(AlertType.INFORMATION, title).show();
    }

    /**
     * Creates and shows an error AlertType Alert.
     * 
     * @param title The title of the error Alert
     * @param header The header of the error Alert
     * @param body The text content of the error Alert
     */
    public static void showError(String title, String header, String body) {
        new Alert(AlertType.ERROR, title, header, body).showAndWait();
    }
    /**
     * Creates and shows an error AlertType Alert.
     * 
     * @param title The title of the error Alert
     * @param header The header of the error Alert
     */
    public static void showError(String title, String header) {
        new Alert(AlertType.ERROR, title, header).showAndWait();
    }
    /**
     * Creates and shows an error AlertType Alert.
     * 
     * @param title The title of the error Alert
     */
    public static void showError(String title) {
        new Alert(AlertType.ERROR, title).showAndWait();
    }

    private static double setRequiredHeight(@NotNull Label label, @NotNull Pane parent, double increase) {
        label.setWrapText(true);
        Text text = new Text(label.getText());
        text.setFont(label.getFont());
        double wrappingWidth;
        if (parent.getWidth() == 0) {
            wrappingWidth = 200;
        } else {
            wrappingWidth = parent.getWidth();
        }
        wrappingWidth /= 2;//BODYDIV;//2.816;
        //increase += PADDING*2;
        text.setWrappingWidth(wrappingWidth);
        text.setLineSpacing(label.getLineSpacing());
        double requiredHeight = text.getLayoutBounds().getHeight()+increase;
        label.setMinHeight(requiredHeight);
        label.setPrefHeight(requiredHeight);
        //System.out.println(label.getParent() == parent);
        //System.out.println(label.getText());
        //System.out.println("Font Size: "+label.getFont().getSize());
        //System.out.println("Font Family: "+label.getFont().getFamily());
        //System.out.println("Font Name: "+label.getFont().getName());
        //System.out.println("Font Style: "+label.getFont().getStyle());
        //System.out.println(parent.getWidth());
        //System.out.println(parent.getHeight());
        //System.out.println(wrappingWidth);
        //System.out.println(text.getWrappingWidth());
        //System.out.println(requiredHeight);
        //System.out.println(parent.getWidth()/BODYDIV);
        //System.out.println(parent.getWidth());
        return requiredHeight;
    }
    public static double setRequiredHeight(Label label, Pane parent) {
        return setRequiredHeight(label, parent, 0);
    }

    /**
     * Holds the types of Alerts.
     * 
     * @see javafx.scene.control.Alert.AlertType
     */
    public enum AlertType {
        /**
         * The None AlertType is default and is the same as
         * the information AlertType with the minus of the
         * information icon.
         */
        NONE,
        /**
         * The simple AlertType for declaring the alert of some
         * information.
         */
        INFORMATION("Alert.Information.png"),
        /**
         * Alerts the user of a possible minor issue
         * that doesn't need to be adressed,
         * but is important.
         */
        WARNING("Alert.Warning.png"),
        /**
         * Asks the user a yes or no question.
         */
        CONFIRMATION("Alert.Confirmation.png"),
        /**
         * Asks the user for some text imput.
         */
        PROMPT(CONFIRMATION.equivalent, "Alert.Prompt.png"),
        /**
         * The same as the confirmation AlertType,
         * but is explicitely for validating that some
         * action should be executed.
         */
        VALIDATE(CONFIRMATION.equivalent, "Alert.Validate.png"),
        /**
         * Calls out an error that has occured,
         * this highlights major issues that
         * will affect other actions.
         */
        ERROR("Alert.Error.png");

        private javafx.scene.control.Alert.AlertType equivalent;
        private final Image image;
        private final double iHeight = 60;
        private final double iWidth = 60;
        private boolean original;

        AlertType() {
            image = null;
            try {
                equivalent = javafx.scene.control.Alert.AlertType.valueOf(this.name());
                original = true;
            } catch (IllegalArgumentException e) {
                equivalent = javafx.scene.control.Alert.AlertType.NONE;
                original = false;
            }
        }

        AlertType(String imgPath) {
            image = new Image(
                    Objects.requireNonNull(Alert.class.getResource(imgPath)).toExternalForm(),
                    //Resources.loadResource(imgPath).url().toExternalForm(),
                    iWidth,
                    iHeight,
                    true,
                    true);
            try {
                equivalent = javafx.scene.control.Alert.AlertType.valueOf(this.name());
                original = true;
            } catch (IllegalArgumentException e) {
                equivalent = javafx.scene.control.Alert.AlertType.NONE;
                original = false;
            }
        }

        AlertType(javafx.scene.control.Alert.AlertType equivalent) {
            image = null;
            this.equivalent = equivalent;
            original = false;
        }

        AlertType(javafx.scene.control.Alert.AlertType equivalent, String imgPath) {
            image = new Image(
                    Objects.requireNonNull(Alert.class.getResource(imgPath)).toExternalForm(),
                    //Resources.loadResource(imgPath).url().toExternalForm(),
                    iWidth,
                    iHeight,
                    true,
                    true);
            this.equivalent = equivalent;
            original = false;
        }

        /**
         * Converts this {@code AlertType} enum into it's equivalent
         * {@code javafx.scene.control.Alert.AlertType} enum.
         * <br>
         * if this {@code AlertType} enum doesn't have a counterpart
         * {@code javafx.scene.control.Alert.AlertType} enum,
         * then {@link javafx.scene.control.Alert.AlertType#NONE} is returned.
         *
         * @return A {@code javafx.scene.control.Alert.AlertType} enum that is equivalent to
         * this {@code AlertType} enum.
         */
        public javafx.scene.control.Alert.AlertType convert() {
            return equivalent;
        }

        /**
         * Gets the image that will be displayed
         * with an Alert message as the Alert box's
         * icon.
         * 
         * @return The image that is linked to the AlertType.
         */
        public Image getImage() {
            return image;
        }

        /**
         * Gets whether this {@code AlertType} is also in
         * {@code javafx.scene.control.Alert.AlertType}.
         * @return true if this {@code AlertType} is in
         * {@code javafx.scene.control.Alert.AlertType}, otherwise false.
         */
        public boolean isOriginal() {
            return original;
        }
    }
}
