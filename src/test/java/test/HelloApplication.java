package test;

import com.airent.extendedjavafxnodes.control.Alert;
import com.airent.extendedjavafxnodes.control.FilePicker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        if (scene.getRoot() instanceof VBox vBox) {
            FilePicker filePicker = new FilePicker();
            vBox.getChildren().add(filePicker);
        }
        Alert.showAlert("Test", "This is a Test.");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}