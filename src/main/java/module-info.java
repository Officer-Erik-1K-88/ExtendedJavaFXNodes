module com.airent.extendedjavafxnodes {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;

    requires ch.obermuhlner.math.big;
    requires org.json;
    requires org.jsoup;
    requires org.mozilla.rhino;

    requires java.desktop;


    //opens com.airent.extendedjavafxnodes to javafx.fxml;
    //exports com.airent.extendedjavafxnodes;

    opens com.airent.extendedjavafxnodes.control to javafx.fxml;
    exports com.airent.extendedjavafxnodes.control;
    exports com.airent.extendedjavafxnodes.utils;
    exports com.airent.extendedjavafxnodes.utils.math;
    exports com.airent.extendedjavafxnodes.utils.json;
    exports com.airent.extendedjavafxnodes.gaxml;
    exports com.airent.extendedjavafxnodes.gaxml.themes;
    exports com.airent.extendedjavafxnodes.gaxml.javascript;
    exports com.airent.extendedjavafxnodes.gaxml.story;
    exports com.airent.extendedjavafxnodes.control.tutorial;
    opens com.airent.extendedjavafxnodes.control.tutorial to javafx.fxml;
    exports com.airent.extendedjavafxnodes.shape;
}