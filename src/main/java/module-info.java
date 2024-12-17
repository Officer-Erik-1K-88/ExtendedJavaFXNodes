module com.airent.extendedjavafxnodes {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires java.desktop;


    //opens com.airent.extendedjavafxnodes to javafx.fxml;
    //exports com.airent.extendedjavafxnodes;

    opens com.airent.extendedjavafxnodes.control to javafx.fxml;
    exports com.airent.extendedjavafxnodes.control;
    exports com.airent.extendedjavafxnodes.utils;
    exports com.airent.extendedjavafxnodes.themes;
    exports com.airent.extendedjavafxnodes.text;
}