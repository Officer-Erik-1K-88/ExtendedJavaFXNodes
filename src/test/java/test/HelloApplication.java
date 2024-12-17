package test;

import com.airent.extendedjavafxnodes.control.Alert;
import com.airent.extendedjavafxnodes.control.FilePicker;
import com.airent.extendedjavafxnodes.control.TutorialContent;
import com.airent.extendedjavafxnodes.gaxml.XMLProcessor;
import com.airent.extendedjavafxnodes.gaxml.themes.Light;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        if (scene.getRoot() instanceof VBox vBox) {
            FilePicker filePicker = new FilePicker();
            TutorialContent tutorialContent = new TutorialContent();
            tutorialContent.addSlide("Page 1", null, HelloApplication.class.getResource("Page1.xml"), new Light());
            vBox.getChildren().addAll(filePicker, tutorialContent);
        }
        //Alert.showAlert("Test", "This is a Test.");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}