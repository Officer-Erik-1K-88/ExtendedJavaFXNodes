package com.airent.extendedjavafxnodes.utils;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.MouseEvent;

public class Action {

    public static void onClick(Node toClick, EventHandler<Event> action) {
        if (toClick instanceof ButtonBase btn) {
            EventHandler<ActionEvent> btnClick = btn.getOnAction();
            btn.setOnAction((btnClick!=null?event -> {
                btnClick.handle(event);
                action.handle(event);
            } : action::handle));
        } else {
            EventHandler<? super MouseEvent> click = toClick.getOnMouseClicked();
            toClick.setOnMouseClicked((click!=null?event -> {
                click.handle(event);
                action.handle(event);
            } : action));
        }
    }
}
