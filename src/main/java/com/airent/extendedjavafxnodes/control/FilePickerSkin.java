package com.airent.extendedjavafxnodes.control;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;

public class FilePickerSkin extends SkinBase<FilePicker> {

    private final ListView<Label> listView;
    private final Button button;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected FilePickerSkin(FilePicker control) {
        super(control);
        this.listView = control.getFileDisplay();
        this.button = control.getTrigger();

        CustomPane layout = new CustomPane(listView, button);
        getChildren().add(layout);
    }

    private static class CustomPane extends Pane {
        private final ListView<Label> listView;
        private final Button button;
        private final double gap = 1; // Gap between ListView and Button
        private final double addedHeight = 4;
        private final double sbsWidth = 200;

        public CustomPane(ListView<Label> listView, Button button) {
            this.listView = listView;
            this.button = button;

            getChildren().addAll(listView, button);
            setPrefSize(340, 30);
        }

        @Override
        protected void layoutChildren() {
            double width = getWidth(); // Width of the custom control
            double height = getHeight(); // Height of the custom control
            double buttonMinHeight = button.minHeight(-1); // Minimum height of the button

            double buttonHeight = button.prefHeight(-1); // Height of the button
            double listViewHeight = Math.min(buttonHeight + addedHeight, height); // Ensure ListView is 4 pixels taller than the Button

            double buttonWidth = button.prefWidth(buttonHeight); // Button's preferred width for its height
            double listViewWidth = width >= sbsWidth ? (width-buttonWidth)-gap : width; // Adjust width when compact

            // Position the ListView
            listView.resizeRelocate(0, 0, listViewWidth, listViewHeight);

            // Position the Button
            button.setVisible(true); // Ensure button is visible
            if (width < sbsWidth) {
                // Stack vertically when width < 200

                if (height >= buttonMinHeight) {
                    // Show button only if height >= button's minimum height
                    button.resizeRelocate(0, listViewHeight + gap, listViewWidth, buttonHeight);
                } else {
                    button.setVisible(false); // Hide button if not enough height
                }
            } else {
                // Layout side by side when width >= 200

                // Calculate vertical alignment
                double buttonTop = (listViewHeight - buttonHeight) / 2; // Center the button vertically within the ListView

                button.resizeRelocate(listViewWidth + gap, buttonTop, buttonWidth, buttonHeight);
            }
        }

        @Override
        protected double computePrefWidth(double height) {
            double buttonWidth = button.prefWidth(height);
            double width = getWidth();
            return ((width-buttonWidth)-gap) + gap + buttonWidth;
        }

        @Override
        protected double computePrefHeight(double width) {
            if (width < sbsWidth) {
                return listView.prefHeight(width) + gap + button.prefHeight(width);
            }
            double buttonHeight = button.prefHeight(-1);
            return buttonHeight + addedHeight; // Account for the 2-pixel extension above and below
        }
    }
}
