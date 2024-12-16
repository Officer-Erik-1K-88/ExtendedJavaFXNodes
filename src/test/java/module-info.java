module test {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;

    opens test to javafx.fxml;
    exports test;
}