module com.airent.extendedjavafxnodes {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;


    //opens com.airent.extendedjavafxnodes to javafx.fxml;
    //exports com.airent.extendedjavafxnodes;

    opens com.airent.extendedjavafxnodes.control to javafx.fxml;
    exports com.airent.extendedjavafxnodes.control;
}