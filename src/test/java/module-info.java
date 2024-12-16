module test {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires com.airent.extendedjavafxnodes;

    opens test to javafx.fxml;
    exports test;
}