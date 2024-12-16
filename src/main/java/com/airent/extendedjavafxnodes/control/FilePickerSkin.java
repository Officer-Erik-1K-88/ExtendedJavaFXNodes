package com.airent.extendedjavafxnodes.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;

public class FilePickerSkin extends SkinBase<FilePicker> {

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected FilePickerSkin(FilePicker control) {
        super(control);

        CustomPane layout = new CustomPane(control);
        getChildren().add(layout);
    }

    private static class CustomPane extends Pane {
        private final ListView<Label> listView;
        private final Button button;
        private final DoubleProperty spacing; // Gap between ListView and Button
        private final double addedHeight = 4;
        private final double sbsWidth = 200;

        public CustomPane(FilePicker filePicker) {
            this.listView = filePicker.getFileDisplay();
            this.button = filePicker.getTrigger();

            spacing = filePicker.spacingProperty();

            getChildren().addAll(listView, button);
            setPrefSize(340, 30);
        }

        public double getSpacing() {
            return spacing.get();
        }

        @Override
        protected void layoutChildren() {
            double width = getWidth(); // Width of the custom control
            double height = getHeight(); // Height of the custom control
            double buttonMinHeight = button.minHeight(-1); // Minimum height of the button

            // Account for padding in layout calculations
            double contentWidth = width - getPadding().getLeft() - getPadding().getRight();
            double contentHeight = height - getPadding().getTop() - getPadding().getBottom();

            double buttonHeight = button.prefHeight(-1); // Height of the button
            double listViewHeight = Math.min(buttonHeight + addedHeight, contentHeight); // Ensure ListView is 4 pixels taller than the Button

            double buttonWidth = button.prefWidth(buttonHeight); // Button's preferred width for its height
            double listViewWidth = contentWidth >= sbsWidth ? (contentWidth-buttonWidth)- getSpacing() : contentWidth; // Adjust width when compact

            // Position the ListView
            listView.resizeRelocate(
                    getPadding().getLeft(),
                    getPadding().getTop(),
                    listViewWidth, listViewHeight);

            // Position the Button
            button.setVisible(true); // Ensure button is visible
            if (width < sbsWidth) {
                // Stack vertically when width < 200

                if (contentHeight >= buttonMinHeight) {
                    // Show button only if height >= button's minimum height
                    button.resizeRelocate(
                            getPadding().getLeft(),
                            getPadding().getTop()+listViewHeight + getSpacing(),
                            listViewWidth, buttonHeight);
                } else {
                    button.setVisible(false); // Hide button if not enough height
                }
            } else {
                // Layout side by side when width >= 200

                // Calculate vertical alignment
                double buttonTop = (listViewHeight - buttonHeight) / 2; // Center the button vertically within the ListView

                button.resizeRelocate(
                        getPadding().getLeft()+listViewWidth + getSpacing(),
                        getPadding().getTop()+buttonTop,
                        buttonWidth, buttonHeight);
            }
        }

        @Override
        protected double computePrefWidth(double height) {
            double buttonWidth = button.prefWidth(height);
            double width = getWidth();
            return ((width-buttonWidth)- getSpacing()) + getSpacing() + buttonWidth +
                    getPadding().getLeft() + getPadding().getRight();
        }

        @Override
        protected double computePrefHeight(double width) {
            double ret;
            if (width < sbsWidth) {
                ret = listView.prefHeight(width) + getSpacing() + button.prefHeight(width);
            } else {
                double buttonHeight = button.prefHeight(-1);
                ret = buttonHeight + addedHeight; // Account for the 2-pixel extension above and below
            }
            return ret + getPadding().getTop() + getPadding().getBottom();
        }
    }
}
